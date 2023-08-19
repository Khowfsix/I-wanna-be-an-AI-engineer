import sys
from collections import deque
from Node import Node


def breadth_first_graph_search(problem):
    """Bread first search (GRAPH SEARCH version)
    See [Figure 3.11] for the algorithm"""

    node = Node(problem.initial)
    if problem.goal_test(node.state):
        return node
    frontier = deque([node])
    explored = set()
    while frontier:
        node = frontier.popleft()
        explored.add(node.state)
        for child in node.expand(problem):
            if child.state not in explored and child not in frontier:
                if problem.goal_test(child.state):
                    return child
                frontier.append(child)
    return None



def iterative_deepening_search(problem):
    """See [Figure 3.18] for the algorithm"""
    for depth_limit in range(sys.maxsize):  # Iterate with increasing depth limit
        result = depth_limited_search(problem, depth_limit)
        if result is not None:  # Goal node found
            return result
    return None


def depth_limited_search(problem, limit=50):
    """See [Figure 3.17] for the algorithm"""
    node = Node(problem.initial)
    return recursive_dls(node, problem, limit)


def recursive_dls(node, problem, depth_limit):
    # Kiểm tra có phải goal không
    if problem.goal_test(node.state):
        return node
    # Nếu limit == 0 thì cut off
    elif depth_limit == 0:
        return None
    else:
        cutoff_occured = False
        # frontier = deque([node])
        # explored = set()
        
        for action in problem.actions(node.state):
            child = node.child_node(problem, action)
            result = recursive_dls(child, problem, depth_limit - 1)
            if result is not None:
                return result
            cutoff_occured = True
            
        if cutoff_occured:
            return None
    return None