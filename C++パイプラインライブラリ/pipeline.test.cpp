#include "./pipeline.h"

#include <catch2/catch.hpp>
#include <sstream>

struct simple_source : ppl::source<int> {
	int current_value = 0;
	simple_source() = default;

	auto name() const -> std::string override {
		return "SimpleSource";
	}

	auto poll_next() -> ppl::poll override {
		if (current_value >= 10)
			return ppl::poll::closed;
		++current_value;
		return ppl::poll::ready;
	}

	auto value() const -> const int& override {
		return current_value;
	}
};

struct simple_source2 : ppl::source<std::string> {
	std::string current_value = "a";
	simple_source2() = default;

	auto name() const -> std::string override {
		return "SimpleSource";
	}

	auto poll_next() -> ppl::poll override {
		if (current_value == "closed")
			return ppl::poll::closed;
		current_value = "closed";
		return ppl::poll::ready;
	}

	auto value() const -> const std::string& override {
		return current_value;
	}
};

struct simple_sink : ppl::sink<int> {
	const ppl::producer<int>* slot0 = nullptr;

	simple_sink() = default;

	auto name() const -> std::string override {
		return "SimpleSink";
	}

	void connect(const ppl::node* src, int slot) override {
		if (slot == 0) {
			slot0 = static_cast<const ppl::producer<int>*>(src);
		}
	}

	auto poll_next() -> ppl::poll override {
		std::cout << slot0->value() << '\n';
		return ppl::poll::ready;
	}
};

struct simple_sink2 : ppl::sink<std::string> {
	const ppl::producer<std::string>* slot0 = nullptr;

	simple_sink2() = default;

	auto name() const -> std::string override {
		return "SimpleSink";
	}

	void connect(const ppl::node* src, int slot) override {
		if (slot == 0) {
			slot0 = static_cast<const ppl::producer<std::string>*>(src);
		}
	}

	auto poll_next() -> ppl::poll override {
		std::cout << slot0->value() << '\n';
		return ppl::poll::ready;
	}
};

struct simple_component : ppl::component<std::tuple<int, std::string>, int> {
	const ppl::producer<int>* slot0 = nullptr;
	const ppl::producer<std::string>* slot1 = nullptr;

	simple_component() = default;

	auto name() const -> std::string override {
		return "SimpleComponent";
	}

	void connect(const ppl::node* src, int slot) override {
		if (slot == 0) {
			slot0 = static_cast<const ppl::producer<int>*>(src);
		}
		if (slot == 1) {
			slot1 = static_cast<const ppl::producer<std::string>*>(src);
		}
	}

	auto value() const -> const int& override {
		return slot0->value();
	}

	auto poll_next() -> ppl::poll override {
		if (slot0 == nullptr || slot1 == nullptr)
			return ppl::poll::empty;
		else
			return ppl::poll::ready;
	}
};

TEST_CASE("Testing pipeline_error") {
	auto e1 = ppl::pipeline_error(ppl::pipeline_error_kind::invalid_node_id);
	CHECK(e1.kind() == ppl::pipeline_error_kind::invalid_node_id);
	CHECK(std::string(e1.what()) == "invalid node ID");
	auto e2 = ppl::pipeline_error(ppl::pipeline_error_kind::no_such_slot);
	CHECK(e2.kind() == ppl::pipeline_error_kind::no_such_slot);
	CHECK(std::string(e2.what()) == "no such slot");
	auto e3 = ppl::pipeline_error(ppl::pipeline_error_kind::slot_already_used);
	CHECK(e3.kind() == ppl::pipeline_error_kind::slot_already_used);
	CHECK(std::string(e3.what()) == "slot already used");
	auto e4 = ppl::pipeline_error(ppl::pipeline_error_kind::connection_type_mismatch);
	CHECK(e4.kind() == ppl::pipeline_error_kind::connection_type_mismatch);
	CHECK(std::string(e4.what()) == "connection type mismatch");
}

TEST_CASE("Testing create_node") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	const auto comp = pipeline.create_node<simple_component>();

	CHECK(source == 0);
	CHECK(sink == 1);
	CHECK(comp == 2);
}

TEST_CASE("Testing get_node - success") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	auto* node = pipeline.get_node(source);
	CHECK(node != nullptr);
}

TEST_CASE("Testing get_node - fail") {
	auto pipeline = ppl::pipeline{};
	pipeline.create_node<simple_source>();
	ppl::pipeline_error_kind error;
	try {
		pipeline.get_node(1);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
}

TEST_CASE("Testing connect - does not throw error") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
}

TEST_CASE("Testing connect - throw invalid node error") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	ppl::pipeline_error_kind error;
	try {
		pipeline.connect(2, sink, 0);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	try {
		pipeline.connect(source, 2, 0);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
}

TEST_CASE("Testing connect - throw slot already used") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	ppl::pipeline_error_kind error;
	try {
		pipeline.connect(source, sink, 0);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::slot_already_used);
}

TEST_CASE("Testing connect - throw no such slot") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	ppl::pipeline_error_kind error;
	try {
		pipeline.connect(source, sink, 1);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::no_such_slot);
}

TEST_CASE("Testing connect - multiple nodes") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto source2 = pipeline.create_node<simple_source2>();
	const auto comp = pipeline.create_node<simple_component>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, comp, 0);
	pipeline.connect(source2, comp, 1);
	pipeline.connect(comp, sink, 0);
}

TEST_CASE("Testing disconnect - does not throw error") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	const auto comp = pipeline.create_node<simple_component>();
	pipeline.connect(source, sink, 0);
	pipeline.disconnect(source, sink);
	pipeline.connect(comp, sink, 0);
}

TEST_CASE("Testing disconnect - does nothing") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.disconnect(source, sink);
}

TEST_CASE("Testing disconnect - throw invalid node id") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	ppl::pipeline_error_kind error;
	try {
		pipeline.disconnect(2, sink);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	try {
		pipeline.disconnect(source, 2);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
}

TEST_CASE("Testing get_dependencies - does not throw error") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	auto dep = pipeline.get_dependencies(source);
	CHECK(dep.size() == 1);
	CHECK(dep[0] == std::pair{sink, 0});
}

TEST_CASE("Testing get_dependencies - empty vector should be returned if there are no dependencies") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	auto dep = pipeline.get_dependencies(source);
	CHECK(dep.size() == 0);
}

TEST_CASE("Testing get_dependencies - dependencies should be updated after disconnecting") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	auto dep = pipeline.get_dependencies(source);
	CHECK(dep.size() == 1);
	pipeline.disconnect(source, sink);
	dep = pipeline.get_dependencies(source);
	CHECK(dep.size() == 0);
}

TEST_CASE("Testing get_dependencies - throw invalid node") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	ppl::pipeline_error_kind error;
	try {
		pipeline.get_dependencies(-1);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
}

TEST_CASE("Testing erase_node - does not throw error with two nodes") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	pipeline.erase_node(sink);
	const auto comp = pipeline.create_node<simple_component>();
	CHECK(comp == 2);
	CHECK(pipeline.get_dependencies(source).size() == 0);
}

TEST_CASE("Testing erase_node - does not throw error with multiple nodes") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto source2 = pipeline.create_node<simple_source2>();
	const auto sink = pipeline.create_node<simple_sink>();
	const auto comp = pipeline.create_node<simple_component>();
	pipeline.connect(source, comp, 0);
	pipeline.connect(source2, comp, 1);
	pipeline.connect(comp, sink, 0);
	pipeline.erase_node(comp);
	CHECK(pipeline.get_dependencies(source).size() == 0);
	CHECK(pipeline.get_dependencies(source2).size() == 0);
	pipeline.connect(source, sink, 0);
}

TEST_CASE("Testing erase_node - throw invalid node") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	ppl::pipeline_error_kind error;
	try {
		pipeline.erase_node(-1);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
}

TEST_CASE("Testing move constructor") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	auto move = std::move(pipeline);
	ppl::pipeline_error_kind error;
	try {
		pipeline.get_node(source);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	try {
		pipeline.get_node(sink);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	CHECK(move.get_node(source) != nullptr);
	CHECK(move.get_node(sink));
	CHECK(move.get_dependencies(source).size() == 1);
}

TEST_CASE("Testing move operator") {
	auto pipeline = ppl::pipeline{};
	auto move = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	move = std::move(pipeline);
	ppl::pipeline_error_kind error;
	try {
		pipeline.get_node(source);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	try {
		pipeline.get_node(sink);
	} catch (const ppl::pipeline_error& e) {
		error = e.kind();
	}
	CHECK(error == ppl::pipeline_error_kind::invalid_node_id);
	CHECK(move.get_node(source) != nullptr);
	CHECK(move.get_node(sink));
	CHECK(move.get_dependencies(source).size() == 1);
}

TEST_CASE("Testing is_valid - just with source and sink") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	CHECK(pipeline.is_valid());
}

TEST_CASE("Testing is_valid - with multiple nodes") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto source2 = pipeline.create_node<simple_source2>();
	const auto comp = pipeline.create_node<simple_component>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, comp, 0);
	pipeline.connect(source2, comp, 1);
	pipeline.connect(comp, sink, 0);
	CHECK(pipeline.is_valid());
}

TEST_CASE("Testing is_valid - no source node") {
	auto pipeline = ppl::pipeline{};
	const auto comp = pipeline.create_node<simple_component>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(comp, sink, 0);
	CHECK(!pipeline.is_valid());
}

TEST_CASE("Testing is_valid - no sink node") {
	auto pipeline = ppl::pipeline{};
	const auto comp = pipeline.create_node<simple_component>();
	const auto source = pipeline.create_node<simple_source>();
	pipeline.connect(source, comp, 0);
	CHECK(!pipeline.is_valid());
}

TEST_CASE("Testing is_valid - unfilled src slots") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto comp = pipeline.create_node<simple_component>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, comp, 0);
	pipeline.connect(comp, sink, 0);
	CHECK(!pipeline.is_valid());
}

TEST_CASE("Testing step - for source and sink") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	CHECK(!pipeline.step());
}

TEST_CASE("Testing run - for source and sink") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto sink = pipeline.create_node<simple_sink>();
	pipeline.connect(source, sink, 0);
	pipeline.run();
}

TEST_CASE("Testing run - for two sources, component and sink") {
	auto pipeline = ppl::pipeline{};
	const auto source = pipeline.create_node<simple_source>();
	const auto source2 = pipeline.create_node<simple_source2>();
	const auto sink = pipeline.create_node<simple_sink>();
	const auto comp = pipeline.create_node<simple_component>();
	pipeline.connect(source, comp, 0);
	pipeline.connect(source2, comp, 1);
	pipeline.connect(comp, sink, 0);
	pipeline.run();
}
