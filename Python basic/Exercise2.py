#%%
import pandas as pd

#%%
# Read the CSV file
df = pd.read_csv("./cars.csv")

#%%
# Display the contents of the DataFrame
print(df)

#%%
print(df.info())
# info(): Hàm info() được sử dụng để hiển thị thông tin chung về DataFrame, bao gồm số lượng hàng và cột, các tên cột, kiểu dữ liệu của từng cột và tổng số giá trị không null trong mỗi cột. Đây là một cách nhanh chóng để có cái nhìn tổng quan về cấu trúc và kiểu dữ liệu của DataFrame.

# %%
print(df.head(20))
# head(): Hàm head() được sử dụng để hiển thị một số hàng đầu tiên trong DataFrame. Mặc định, nó hiển thị 5 hàng đầu tiên, nhưng bạn có thể chỉ định số hàng muốn hiển thị bằng cách truyền tham số vào hàm. Ví dụ: df.head(10) sẽ hiển thị 10 hàng đầu tiên của DataFrame df.

#%%
print(df.describe())
# describe(): Hàm describe() được sử dụng để tạo ra một tóm tắt thống kê của các cột số học trong DataFrame. Nó cung cấp thông tin như số lượng, giá trị trung bình, độ lệch chuẩn, giá trị tối thiểu và tối đa của từng cột. Hàm này chỉ áp dụng cho các cột có kiểu dữ liệu số học. Ví dụ: df.describe() sẽ tạo ra một bảng tóm tắt thống kê cho DataFrame df.



# Lấy dữ liệu của 1 hàng trong bảng và vẽ đồ thị sử dụng matplotlib. Gợi ý: một số dữ liệu về Việt Nam có thể tìm thấy tại trang Tổng Cục Thống Kê, gso.gov.vn. Bạn có thể tải về dạng ‘Phân tách bởi dấu chấm phẩy’, sau đó mở bằng MS Excel, xóa bớt dòng và cột không dùng, rồi lưu lại dạng CSV UTF-8.  

#%%
df2 = pd.read_csv("./govData.csv")
# %%

#%%
df2

#%%
# Chuyển vị DataFrame
df3 = df2.transpose()

#%%
df3.columns = ['Something']

#%%
import matplotlib.pyplot as plt

#%%
x = df3.index.tolist()
y = df3['Something'].tolist()

plt.plot(x,y)
plt.show()