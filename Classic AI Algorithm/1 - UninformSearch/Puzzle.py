class PuzzleProblem:
    """ The problem of sliding tiles numbered from 1 to 8 on a 3x3 board, where one of the
    squares is a blank. A state is represented as a tuple of length 9, where  element at
    index i represents the tile number  at index i (0 if it's an empty square) """

    def __init__(self, initial, goal=(0, 1, 2, 3, 4, 5, 6, 7, 8)):
        """
            Define goal state and initialize a problem
        """
        self.initial = initial
        self.goal = goal

    def actions(self, state):
        """
            Return the actions that can be executed in the given state.
            The result would be a list, since there are only four possible actions
            in any given state of the environment
        """

        possible_actions = ['UP', 'DOWN', 'LEFT', 'RIGHT']
        index_blank_square = self.find_blank_square(state)

        if index_blank_square % 3 == 0:
            possible_actions.remove('LEFT')
        if index_blank_square < 3:
            possible_actions.remove('UP')
        if index_blank_square % 3 == 2:
            possible_actions.remove('RIGHT')
        if index_blank_square > 5:
            possible_actions.remove('DOWN')

        return possible_actions

    def result(self, state, action):
        """
            Given state and action, return a new state that is the result of the action.
            Action is assumed to be a valid action in the state
        """

        # blank is the index of the blank square
        blank = self.find_blank_square(state)
        new_state = list(state)

        delta = {'UP': -3, 'DOWN': 3, 'LEFT': -1, 'RIGHT': 1}
        neighbor = blank + delta[action]
        new_state[blank], new_state[neighbor] = new_state[neighbor], new_state[blank]

        return tuple(new_state)

    def goal_test(self, state):
        """
            Given a state, return True if state is a goal state or False, otherwise
        """
        return state == self.goal

    def path_cost(self, c, state1, action, state2):
        """
            Return the cost of a solution path that arrives at state2 from
            state1 via action, assuming cost c to get up to state1. If the problem
            is such that the path doesn't matter, this function will only look at
            state2. If the path does matter, it will consider c and maybe state1
            and action. The default method costs 1 for every step in the path.
        """
        return c + 1

    def find_blank_square(self, state):
        """
            Return the index of the blank square in a given state
        """
        return state.index(0)
