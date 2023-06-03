import pandas
from sklearn import tree
from sklearn.tree import DecisionTreeClassifier
import matplotlib.pyplot as plt

#Load dữ liệu từ file csv
df = pandas.read_csv("../input/data.csv")

#Chuyển đổi kiểu dữ liệu sang kiểu số
d = {'Sunny': 0, 'Overcast': 1, 'Rain': 2}
df['Outlook'] = df['Outlook'].map(d)

d= {'Hot': 0,'Mild': 1,'Cool': 2}
df['Temperature'] = df['Temperature'].map(d)

d= {'High': 0,'Normal': 1}
df['Humidity'] = df['Humidity'].map(d)

d= {'Weak': 0,'Strong': 1}
df['Wind'] = df['Wind'].map(d)

d = {'Yes': 1, 'No': 0}
df['Go-Play'] = df['Go-Play'].map(d)

#Các đặc tính, đối tượng
features = ['Outlook', 'Temperature', 'Humidity', 'Wind']

X = df[features]
y = df['Go-Play']

#Tạo cây quyết định Decistion Tree
dtree = DecisionTreeClassifier()
dtree = dtree.fit(X, y)

#Vẽ cây quyết định Decision Tree
tree.plot_tree(dtree, feature_names=features)
plt.show()