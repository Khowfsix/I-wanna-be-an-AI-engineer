import tkinter as tk
import random

# Tạo một đối tượng cửa sổ
window = tk.Tk()

# Thiết lập tiêu đề cho cửa sổ
window.title("Sorting Buttons Example")

# Tạo một lưới để chứa các ô
grid = tk.Frame(window)
grid.pack(side="left")

# Tạo danh sách các ô
cells = []
for i in range(3):
    row = []
    for j in range(3):
        cell = tk.Label(grid, width=10, height=5,
                        relief="solid", borderwidth=1)
        cell.grid(row=i, column=j)
        row.append(cell)
    cells.append(row)

# Hàm điều chỉnh màu


def adjust_color(base_color, r_intensity, g_intensity, b_intensity):
    base_color = base_color.lstrip("#")
    r, g, b = tuple(int(base_color[i:i + 2], 16) for i in (0, 2, 4))
    r = min(255, max(0, r + r_intensity))
    g = min(255, max(0, g + g_intensity))
    b = min(255, max(0, b + b_intensity))
    adjusted_color = f"#{r:02x}{g:02x}{b:02x}"
    return adjusted_color


# Thiết lập giá trị và màu sắc cho các ô
values = [[1, 2, 3], [4, 5, 6], [7, 8, ""]]
base_color = "#FFD700"  # Màu vàng cơ sở
r_intensity = -30  # Điều chỉnh thành phần màu đỏ
g_intensity = 30  # Điều chỉnh thành phần màu xanh lá cây
b_intensity = 0  # Điều chỉnh thành phần màu xanh dương
for i in range(3):
    for j in range(3):
        cell_value = values[i][j]
        if cell_value != "":
            cell_value = int(cell_value)
            adjusted_color = adjust_color(
                base_color, r_intensity * cell_value, g_intensity * cell_value, b_intensity * cell_value)
            cells[i][j].config(text=cell_value, bg=adjusted_color)

# Container chứa nút chức năng sắp xếp
sort_buttons_container = tk.Frame(window)
sort_buttons_container.pack(side="right")

# Hàm sắp xếp mảng theo thứ tự tăng dần


def sort_array_asc():
    values_flat = [cell["text"] for row in cells for cell in row]
    values_flat.sort()
    for i, row in enumerate(cells):
        for j, cell in enumerate(row):
            cell.config(text=values_flat[i * 3 + j])

# Hàm sắp xếp mảng theo thứ tự giảm dần


def sort_array_desc():
    values_flat = [cell["text"] for row in cells for cell in row]
    values_flat.sort(reverse=True)
    for i, row in enumerate(cells):
        for j, cell in enumerate(row):
            cell.config(text=values_flat[i * 3 + j])

# Hàm sắp xếp mảng theo thứ tự ngẫu nhiên


def sort_array_random():
    values_flat = [cell["text"] for row in cells for cell in row]
    random.shuffle(values_flat)
    for i, row in enumerate(cells):
        for j, cell in enumerate(row):
            cell.config(text=values_flat[i * 3 + j])


# Tạo nút chức năng sắp xếp tăng dần
btn_sort_asc = tk.Button(sort_buttons_container,
                         text="Sort Asc", command=sort_array_asc)
btn_sort_asc.pack()

# Tạo nút chức năng sắp xếp giảm dần
btn_sort_desc = tk.Button(sort_buttons_container,
                          text="Sort Desc", command=sort_array_desc)
btn_sort_desc.pack()

# Tạo nút chức năng sắp xếp ngẫu nhiên
btn_sort_random = tk.Button(sort_buttons_container,
                            text="Sort Random", command=sort_array_random)
btn_sort_random.pack()

# Chạy vòng lặp chính của ứng dụng
window.mainloop()
