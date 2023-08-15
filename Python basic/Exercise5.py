#%%
import sympy as sp

#%%
x = sp.Symbol('x')
y = sp.Symbol('y')
z = sp.Symbol('z')

#%%
# Define the equation
equation1 = sp.Eq(3*x + 2*y - z, 1)
equation2 = sp.Eq(2*x + 2*y - 4*z, -2)
equation3 = sp.Eq(-x + (1/2)*y - z, 0)

#%%
# Solve the equation
solution = sp.solve((equation1, equation2, equation3), (x, y, z))

# Print the solutions
print(solution)