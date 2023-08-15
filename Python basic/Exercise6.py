#%%
import sympy as sp

#%%
x = sp.Symbol('x')
u = sp.Symbol('u')
v = sp.Symbol('v')

#%%
# Định nghĩa biểu thức
expression = sp.sin(u*x) * sp.cos(v*x)

#%%
# Tính đạo hàm bậc 1 theo x
derivative = sp.diff(expression, x, 1)
print(derivative)

#%%
# Tính đạo hàm bậc 1 theo u
derivative = sp.diff(expression, u, 1)
print(derivative)

#%%
# Tính đạo hàm bậc 2 theo x
derivative = sp.diff(expression, x, 2)
print(derivative)