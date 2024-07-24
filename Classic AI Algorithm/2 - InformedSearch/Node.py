class Node():
    """A node class for A* search"""

    def __init__(self, parent=None, position=None):
        self.parent = parent
        self.position = position

        self.g = 0 # PATH-COST to the node
        self.h = 0 # heuristic to the goal: straight-line distance hueristic
        self.f = 0 # evaluation function f(n) = g(n) + h(n)

    def __eq__(self, other):
        return self.position == other.position