import numpy as np
import matplotlib.pyplot as plt
# from mpl_toolkits.mplot3d import Axes3D

# Tạo dữ liệu mẫu
## Tạo chuỗi x y
x = np.linspace(-2, 2, 100)
y = np.linspace(-2, 2, 100)
## Meshgrid 2 chiều x y lại thành không gian 2 chiều
x, y = np.meshgrid(x, y)
## Tính z theo công thức
z = x * np.exp(-x**2 - y**2)

# Tạo subplot 3D
## Tạo figure
fig = plt.figure()
## Thêm subplot ax 3D vào
ax = fig.add_subplot(111, 
                     projection='3d')

# Vẽ biểu đồ 3D
ax.plot_surface(x, 
                y, 
                z, 
                # theme
                cmap='viridis')

# Đặt tên cho các trục
ax.set_xlabel('X')
ax.set_ylabel('Y')
ax.set_zlabel('Z')

# Hiển thị biểu đồ
plt.show()
