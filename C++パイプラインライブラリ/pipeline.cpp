#include "./pipeline.h"

ppl::pipeline_error::pipeline_error(pipeline_error_kind error) : pipeline_error_kind_{error} {}

auto ppl::pipeline_error::kind() const -> pipeline_error_kind {return pipeline_error_kind_;}

auto ppl::pipeline_error::what() const throw() -> const char * {
    switch (pipeline_error_kind_)
    {
    case pipeline_error_kind::invalid_node_id:
        return "invalid node ID";
    case pipeline_error_kind::no_such_slot:
        return "no such slot";
    case pipeline_error_kind::slot_already_used:
        return "slot already used";
    case pipeline_error_kind::connection_type_mismatch:
        return "connection type mismatch";
    default:
        return "Something went wrong";
    }
    return "";
}

ppl::pipeline::pipeline() 
    : id_{0}, 
    nodes_{},
    input_types_{},
    output_types_{},
    inputs_{},
    dependencies_{},
    sources_{}, 
    sinks_{}, 
    queue_{}, 
    closed_{} {}

ppl::pipeline::pipeline(ppl::pipeline&& other) {
    id_ = std::exchange(other.id_, 0);
    nodes_ = std::exchange(other.nodes_, {});
    input_types_ = std::exchange(other.input_types_, {});
    output_types_ = std::exchange(other.output_types_, {});
    inputs_ = std::exchange(other.inputs_, {});
    dependencies_ = std::exchange(other.dependencies_, {});
    sources_ = std::exchange(other.sources_, {});
    sinks_ = std::exchange(other.sinks_, {});
    queue_ = std::exchange(other.queue_, {});
    closed_ = std::exchange(other.closed_, {});
}

auto ppl::pipeline::operator=(pipeline&& other) -> pipeline& {
    if(this != &other) {
        id_ = std::exchange(other.id_, 0);
        nodes_ = std::exchange(other.nodes_, {});
        input_types_ = std::exchange(other.input_types_, {});
        output_types_ = std::exchange(other.output_types_, {});
        inputs_ = std::exchange(other.inputs_, {});
        dependencies_ = std::exchange(other.dependencies_, {});
        sources_ = std::exchange(other.sources_, {});
        sinks_ = std::exchange(other.sinks_, {});
        queue_ = std::exchange(other.queue_, {});
        closed_ = std::exchange(other.closed_, {});
    }
    return *this;
}

void ppl::pipeline::erase_node(ppl::pipeline::node_id n_id) {
    if(!is_node_id_valid(n_id))
        throw pipeline_error(pipeline_error_kind::invalid_node_id);
    // Need to go through every node that depends on the node to be erased
    auto temp = dependencies_;
    for (const auto& outer_pair : temp) {
        for (const auto& inner_pair : outer_pair.second) {
            if (inner_pair.first == n_id) {
                disconnect(outer_pair.first, n_id);
            }
        }
    }
    // Need to go through every node that the erased node depended on
    auto temp2 = dependencies_[n_id];
    for (const auto& pair : temp2) {
        disconnect(n_id, pair.first);
    }
    dependencies_.erase(n_id);
    inputs_.erase(n_id);
    nodes_[n_id] = nullptr;
    if(sources_.contains(n_id))
        sources_.erase(n_id);
    if(sinks_.contains(n_id))
        sinks_.erase(n_id);
}

auto ppl::pipeline::get_node(ppl::pipeline::node_id n_id) -> node* {
    if(!is_node_id_valid(n_id))
        throw pipeline_error(pipeline_error_kind::invalid_node_id);
    return nodes_[n_id].get();
}

void ppl::pipeline::connect(node_id src, node_id dst, int slot) {
    if(!is_node_id_valid(src) || !is_node_id_valid(dst) || src == dst)
        throw pipeline_error(pipeline_error_kind::invalid_node_id);
    else if(!inputs_[dst].contains(slot))
        throw pipeline_error(pipeline_error_kind::no_such_slot);
    else if(inputs_[dst][slot] != -1)
        throw pipeline_error(pipeline_error_kind::slot_already_used);
    else if(!is_connection_type_match(src, dst, slot))
        throw pipeline_error(pipeline_error_kind::connection_type_mismatch);
    nodes_[dst]->connect(nodes_[src].get(), slot);
    dependencies_[src][dst] = slot;
    inputs_[dst][slot] = src;
}

void ppl::pipeline::disconnect(node_id src, node_id dst) {
    if(!is_node_id_valid(src) || !is_node_id_valid(dst))
        throw pipeline_error(pipeline_error_kind::invalid_node_id);
    if(!dependencies_[src].contains(dst))
        return;
    nodes_[dst]->connect(nullptr, dependencies_[src][dst]);
    for (const auto& pair : inputs_[dst]) {
        if(pair.second == src)
            inputs_[dst][pair.first] = -1;
    }
    dependencies_[src].erase(dst);
}

auto ppl::pipeline::get_dependencies(node_id src) -> std::vector<std::pair<node_id, int>> {
    if(!is_node_id_valid(src))
        throw pipeline_error(pipeline_error_kind::invalid_node_id);

    auto vec = std::vector<std::pair<node_id, int>>();

    for (const auto& [key, value] : dependencies_[src]) {
        vec.push_back(std::pair<node_id, int>(key, value));
    }
    return vec;
}

auto ppl::pipeline::is_valid() noexcept -> bool {
    if(sources_.size() == 0)
        return false;
    if(sinks_.size() == 0)
        return false;
    if(!is_src_slots_filled())
        return false;
    if(!is_dependencies_valid())
        return false;
    return true;
}

auto ppl::pipeline::step() noexcept -> bool {
    if(queue_.empty())
        return true;
    node_id id = queue_.front();
    queue_.pop();

    auto result = nodes_[id]->poll_next();
    if(result == ppl::poll::ready) {
        std::for_each(std::begin(inputs_[id]), std::end(inputs_[id]), [&](auto& pair) { 
            if(closed_.contains(pair.second))
                closed_.insert(id);
            else
                queue_.push(pair.second);
        });
        std::for_each(std::begin(dependencies_[id]), std::end(dependencies_[id]), [&](auto& pair) { 
            if(closed_.contains(pair.first))
                closed_.insert(id);
            else 
                queue_.push(pair.first); 
        });
    }
    else if(result == ppl::poll::empty) {
        queue_.push(id);
    }
    else {
        closed_.insert(id);
    }
    return false;
}

void ppl::pipeline::run() noexcept {
    while(!step()) {}
}

auto ppl::pipeline::is_node_id_valid(node_id n_id) const noexcept ->bool {
    if(n_id >= id_ || n_id < 0 || !nodes_.contains(n_id))
        return false;
    return true;
}

auto ppl::pipeline::is_connection_type_match(node_id src, node_id dst, int slot) const noexcept -> bool {
    if(src+dst+slot == 0)
        return false;
    return true;
}

auto ppl:: pipeline::is_src_slots_filled() const noexcept -> bool {
    for(auto iter = inputs_.begin(); iter != inputs_.end(); ++iter) {
        for(auto iter2 = iter->second.begin(); iter2 != iter->second.end(); ++iter2) {
            if(iter2->second == -1)
                return false;
        }
    }
    return true;
}

auto ppl:: pipeline::is_dependencies_valid() const noexcept -> bool {
    std::size_t i = 0;
    for(auto iter = dependencies_.begin(); iter != dependencies_.end(); ++iter) {
        if(iter->second.size() == 0)
            i++;
    }
    if(i != sinks_.size())
        return false;
    return true;
}
