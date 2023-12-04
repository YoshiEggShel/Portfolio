#ifndef COMP6771_PIPELINE_H
#define COMP6771_PIPELINE_H

#include <type_traits>
#include <unordered_set>
#include <algorithm>
#include <concepts>
#include <exception>
#include <iostream>
#include <map>
#include <memory>
#include <queue>
#include <set>
#include <string>
#include <tuple>
#include <typeindex>
#include <typeinfo>
#include <vector>

namespace ppl {

	// Errors that may occur in a pipeline.
	enum class pipeline_error_kind {
		// An expired node ID was provided.
		invalid_node_id,
		// Attempting to bind a non-existant slot.
		no_such_slot,
		// Attempting to bind to a slot that is already filled.
		slot_already_used,
		// The output type and input types for a connection don't match.
		connection_type_mismatch,
	};

	// Pipeline Error
	struct pipeline_error : std::exception {
	 public:
		explicit pipeline_error(pipeline_error_kind error);
		auto kind() const -> pipeline_error_kind;
		auto what() const throw() -> const char * override;

	 private:
		const pipeline_error_kind pipeline_error_kind_;
	};

	// The result of a poll_next() operation.
	enum class poll {
		// A value is available.
		ready,
		// No value is available this time, but there might be one later.
		empty,
		// No value is available, and there never will be again:
		// every future poll for this node will return `poll::closed` again.
		closed,
	};

	// Node
	class node {
	 public:
		virtual auto name() const -> std::string = 0;
		virtual ~node() = default;

	 private:
		virtual auto poll_next() -> poll = 0;
		virtual void connect(const node* source, int slot) = 0;

		// You may add any other virtual functions you feel you may want here.
		friend class pipeline;
	};

	// Producer general
	template<typename Output>
	struct producer : node {
	 public:
		using output_type = Output;
		virtual auto value() const -> const output_type& = 0; // only when `Output` is not `void`
	};

	// Producer as sink
	template<>
	struct producer<void> : node {
	 public:
		using output_type = void;
	};

	// Component
	template<typename Input, typename Output>
	struct component : producer<Output> {
	 public:
		using input_type = Input;
	};

	// Sink
	template<typename Input>
	struct sink : component<Input, void> {};

	// Source
	template<typename Output>
	struct source : component<std::tuple<>, Output> {
	 private:
		void connect(const node* source, int slot) override {
			static_cast<void>(source);
			static_cast<void>(slot);
		};
	};

	// The requirements that a type `N` must satisfy
	// to be used as a component in a pipeline.
	template<typename N>
	// 3.6.0
	concept concrete_node = requires {
		// publish the types it consumes through a public member type input_type;
		typename N::input_type;
		// have a std::tuple input_type;
		std::convertible_to<typename N::input_type, std::tuple<>>;
		// publish the type it produces through a public member type output_type;
		typename N::output_type;
		// be derived from the node type;
		std::derived_from<N, node>;
		// also be derived from the appropriate producer type;
		std::derived_from<N, producer<typename N::output_type>>;
		// not be an abstract class (i.e., we can construct it).
		!std::is_abstract_v<N>;
	};

	class pipeline {
	 public:
		// 3.6.1
		using node_id = int16_t;

		// 3.6.2
		pipeline();
		pipeline(pipeline&& other);
		auto operator=(pipeline&& other) -> pipeline&;
		~pipeline() = default;

		// 3.6.3
		template<typename N, typename... Args>
		    requires concrete_node<N> and std::constructible_from<N, Args...>
		auto create_node(Args&&... args) -> node_id {
			auto node = std::make_unique<N>(std::forward<Args>(args)...);
			nodes_[id_] = std::move(node);
			dependencies_[id_] = std::map<node_id, int>();
			inputs_[id_] = std::map<int, node_id>();
			for (int i = 0; i < int(std::tuple_size<typename N::input_type>::value); i++) {
				inputs_[id_][i] = -1;
			}
			if constexpr (std::tuple_size<typename N::input_type>{} == 0)
				sources_.insert(id_);
			else {
				if constexpr (std::is_void<typename N::output_type>::value) {
					sinks_.insert(id_);
					queue_.push(id_);
				}
				/*
				if constexpr (std::tuple_size<typename N::input_type>{} > 0)
				    set_type_info<typename N::input_type, std::tuple_size<typename N::input_type>{}>(type);
				*/
			}
			// auto temp = typeid(int);
			// std::type_index temp2(temp);
			// output_types_[id_] = temp2;
			id_++;
			return id_ - 1;
		}

		void erase_node(node_id n_id);
		auto get_node(node_id n_id) -> node*;

		// 3.6.4
		void connect(node_id src, node_id dst, int slot);
		void disconnect(node_id src, node_id dst);
		auto get_dependencies(node_id src) -> std::vector<std::pair<node_id, int>>;

		// 3.6.5
		auto is_valid() noexcept -> bool;
		auto step() noexcept -> bool;
		void run() noexcept;

		// 3.6.6
		friend std::ostream& operator<<(std::ostream& os, const pipeline& ppl) {
			os << "digraph G {" << std::endl;
			std::for_each(std::begin(ppl.nodes_), std::end(ppl.nodes_), [&](auto& pair) {
				os << "  \"" << pair.first << " " << pair.second->name() << "\"" << std::endl;
			});

			os << std::endl;

			std::for_each(std::begin(ppl.dependencies_), std::end(ppl.dependencies_), [&](auto& pair) {
				std::for_each(std::begin(pair.second), std::end(pair.second), [&](auto& pair2) {
					os << "  \"" << pair.first << " " << ppl.nodes_.at(pair.first)->name() << "\"";
					os << " -> ";
					os << "\"" << pair2.first << " " << ppl.nodes_.at(pair2.first)->name() << "\"" << std::endl;
				});
			});
			os << "}" << std::endl;
			return os;
		};

	 private:
		pipeline(const pipeline&) = delete;
		auto operator=(const pipeline&) -> pipeline& = delete;
		node_id id_;
		std::map<node_id, std::unique_ptr<node>> nodes_;
		std::map<node_id, std::map<int, std::type_index>> input_types_;
		std::map<node_id, std::type_index> output_types_;
		// this uses the src as key and the inner map as the inputs
		std::map<node_id, std::map<int, node_id>> inputs_;
		// This uses the node as key and the inner map is what the key node is depending on (along with slot of dst)
		std::map<node_id, std::map<node_id, int>> dependencies_;
		std::set<node_id> sources_;
		std::set<node_id> sinks_;
		std::queue<node_id> queue_;
		std::unordered_set<node_id> closed_;
		auto is_node_id_valid(node_id n_id) const noexcept -> bool;
		auto is_connection_type_match(node_id src, node_id dst, int slot) const noexcept -> bool;
		auto is_dependencies_valid() const noexcept -> bool;
		auto is_src_slots_filled() const noexcept -> bool;
		auto is_connected(node_id src, node_id dst) const noexcept -> bool;
		/*
		template <typename T, std::size_t I>
		auto set_type_info(std::map<int, std::type_index>& map) -> void {
		    if constexpr(I < 0)
		        return;
		    else {
		        set_type_info<T, static_cast<std::size_t>(I - 1)>(map);
		        using type = std::tuple_element_t<I - 1, T>();
		        map[0] = indexes[0];
		    }
		}*/
	};
} // namespace ppl

#endif // COMP6771_PIPELINE_H
