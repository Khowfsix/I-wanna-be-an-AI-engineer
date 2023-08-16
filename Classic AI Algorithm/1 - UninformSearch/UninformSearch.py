import sys
from collections import deque
from Puzzle import PuzzleProblem
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


''' IMPLEMENT THE FOLLOWING FUNCTION '''


def depth_limited_search(problem, limit=50):
    """See [Figure 3.17] for the algorithm"""

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


''' IMPLEMENT THE FOLLOWING FUNCTION '''


def iterative_deepening_search(problem):
    """See [Figure 3.18] for the algorithm"""

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


def PuzzleMap():
    pass


if __name__ == '__main__':
    import time

    problem = PuzzleProblem(
        initial=(3, 1, 2, 6, 0, 8, 7, 5, 4),
        goal=(0, 1, 2, 3, 4, 5, 6, 7, 8))

    result1 = breadth_first_graph_search(problem)
    print(result1.solution())

    # USE BELOW CODE TO TEST YOUR IMPLEMENTED FUNCTIONS
    # result2 = iterative_deepening_search(problem)
    # print(result2.solution())
