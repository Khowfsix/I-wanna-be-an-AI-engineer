import time
import tkinter as tk
import random
from Puzzle import PuzzleProblem
import UninformSearch as UnS

# Tạo một đối tượng cửa sổ
window = tk.Tk()

# Thiết lập tiêu đề cho cửa sổ
window.title("Puzzle")

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


def adjust_color(base_color, r_intensity, g_intensity, b_intensity):
    """
        Hàm điều chỉnh màu
    """
    base_color = base_color.lstrip("#")
    r, g, b = tuple(int(base_color[i:i + 2], 16) for i in (0, 2, 4))
    r = min(255, max(0, r + r_intensity))
    g = min(255, max(0, g + g_intensity))
    b = min(255, max(0, b + b_intensity))
    adjusted_color = f"#{r:02x}{g:02x}{b:02x}"
    return adjusted_color


# Thiết lập giá trị và màu sắc cho các ô
values = [3, 1, 2, 6, 0, 8, 7, 5, 4]


def tracking_color():
    base_color = "#FFD700"  # Màu vàng cơ sở
    r_intensity = -30  # Điều chỉnh thành phần màu đỏ
    g_intensity = 30  # Điều chỉnh thành phần màu xanh lá cây
    b_intensity = 0  # Điều chỉnh thành phần màu xanh dương

    # Update màu theo giá trị ô
    for i in values:
        cell_value = values[i]
        if cell_value != 0:
            cell_value = int(cell_value)
            adjusted_color = adjust_color(
                base_color  ,
                r_intensity * cell_value,
                g_intensity * cell_value,
                b_intensity * cell_value)
            cells[int(i/3)][i%3].config(text=cell_value, bg=adjusted_color)
        else:
            cells[int(i/3)][i%3].config(text="", bg="#FFFFFF")

tracking_color()

# Container chứa nút chức năng sắp xếp
sort_buttons_container = tk.Frame(window)
sort_buttons_container.pack(side="right")


def sort_array_random():
    """
        Hàm sắp xếp mảng theo thứ tự ngẫu nhiên
    """
    random.shuffle(values)
    for i, row in enumerate(cells):
        for j, cell in enumerate(row):
            cell.config(text=values[i * 3 + j])
    tracking_color()


# Tạo một ô văn bản không cho phép chỉnh sửa
text_box = tk.Entry(window, state="readonly", bg="white", width=30)
text_box.pack(side="right", padx=10, pady=10)
text_box.config(font=("Arial", 14))


def sort_dfs():
    global values
    state = tuple(values)
    
    problem = PuzzleProblem(
        initial=state,
        goal=(0, 1, 2, 3, 4, 5, 6, 7, 8))
    
    result = UnS.breadth_first_graph_search(problem)
    
    print(result.solution())
    
    for sort_state in result.path():
        values = list(sort_state.state)
        tracking_color()
        time.sleep(0.2)
        window.update()

# Tạo nút chức năng sắp xếp ngẫu nhiên
btn_sort_random = tk.Button(sort_buttons_container,
                            text="Sort Random", command=sort_array_random)
btn_sort_random.pack()

# Tạo các nút khác
button1 = tk.Button(window, text="Sorting with BFS", command=sort_dfs)
button1.pack(side="top", padx=10, pady=10)

button2 = tk.Button(window, text="Sorting with DFS")
button2.pack(side="top", padx=10, pady=10)

button3 = tk.Button(window, text="Sorting with DFS limited depth")
button3.pack(side="top", padx=10, pady=10)


# Chạy vòng lặp chính của ứng dụng
window.mainloop()
