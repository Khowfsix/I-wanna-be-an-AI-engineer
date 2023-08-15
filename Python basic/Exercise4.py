#%%
import sympy as sp

#%%
x = sp.Symbol('x')
u = sp.Symbol('u')
v = sp.Symbol('v')

#%%
# Define the equation
equation1 = sp.Eq(5 * x**2 + 6 * x - 37, 0)
equation2 = sp.Eq(2 * x**3 - x, 0)
equation3 = sp.Eq(u * x + v * x, 0)

#%%
# Solve the equation
solution1 = sp.solve((equation1, equation2, equation3), x)
solution2 = sp.solve((equation1, equation2, equation3), u)
solution3 = sp.solve((equation1, equation2, equation3), v)

# Print the solutions
print(solution1)
print(solution2)
print(solution3)
