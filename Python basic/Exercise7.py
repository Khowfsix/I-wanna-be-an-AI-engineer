#%%
import sympy as sp

#%%
x = sp.Symbol('x')
y = sp.Symbol('y')
R = 5

x1 = -5
x2 = 5

#%%
# Biểu thức hình tròn
circle = sp.sqrt(-x**2 + R**2)

#%%
# Tính tích phân cận x1 x2
definite_integral = sp.integrate(circle, (x, x1, x2))

print(definite_integral)