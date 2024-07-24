import tkinter as tk

# Create the main window
root = tk.Tk()
root.title("Grid World")

# Create the grid
grid_width = 20
grid_height = 20
cell_size = 20

canvas = tk.Canvas(root, width=grid_width*cell_size, height=grid_height*cell_size)
canvas.pack()

# Create the cells
cells = {}
for i in range(grid_width):
    for j in range(grid_height):
        x1 = i * cell_size
        y1 = j * cell_size
        x2 = x1 + cell_size
        y2 = y1 + cell_size
        fill_color = "white"
        if (i, j) == (0, 0):
            fill_color = "green"  # Set the starting cell to green
        elif (i, j) == (8, 9):
            fill_color = "red"  # Set the ending cell to red
        cell = canvas.create_rectangle(x1, y1, x2, y2, fill=fill_color)
        cells[(i, j)] = cell

# Create the walls
walls = [(1, 2), (3, 4), (5, 6)]  # Example wall positions

for wall in walls:
    x1 = wall[0] * cell_size
    y1 = wall[1] * cell_size
    x2 = x1 + cell_size
    y2 = y1 + cell_size
    canvas.create_rectangle(x1, y1, x2, y2, fill="black")

# Create the reset button
def reset_grid():
    for cell in cells.values():
        fill_color = "white"
        cell_position = canvas.coords(cell)
        cell_x = int(cell_position[0] / cell_size)
        cell_y = int(cell_position[1] / cell_size)
        if (cell_x, cell_y) == (0, 0):
            fill_color = "green"
        elif (cell_x, cell_y) == (8, 9):
            fill_color = "red"
        canvas.itemconfigure(cell, fill=fill_color)

reset_button = tk.Button(root, text="Reset", command=reset_grid)
reset_button.pack()

# Start the main loop
root.mainloop()
