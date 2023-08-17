# Thuật toán AI cổ điển


## 1. Basic concept


### 4 cách tiếp cận (trường phái) của AI của AI:
Thinking humanly

> - AI suy nghĩ như con người
> - ANN (neron)
> - Watching thought

Thinking rationally: 
    
> Không cần suy nghĩ như con người, chỉ cần suy nghĩ theo hướng logic toán học 

Action humanly: 

> - Không quan tâm thuật toán bên dưới, chỉ cần hành động diễn ra đúng là được
> - Thí nghiệm: Turing dest
> - Nhiều hạn chế, dễ bị qua mặt, giả AI (sử dụng mẹo)


Action rationally: 

> Tìm **solution** bằng 1 chuỗi các **action**

### Cơ sở của AI

- Triết học
- Toán
- Neuron science
- Tâm lý học
- Ngôn ngữ học
- Kỹ thuật máy tính
- Lý thuyết điều khiển


### Basic Concept

AI (agent) tác động 1 action nào đó vào môi trường (enviroment) làm enviroment thay đổi trạng thái từ s => s' và ngược lại môi trường cung cấp các percepts (observation) để agent biết được thông tin về môi trường

Sau vài action thì agent sẽ có reward để nó hướng tới kết quả

> và Agent sẽ cố gắng có reward cao nhất

==> Thiết kế hệ thống chấm điểm hợp lý cho Agent

Nên đưa chỉ dẫn chi tiết gợi ý chi tiết cho Agent, tuy nhiên phải đủ chứ không nên thiên về 1 hướng; 

> Chi tiết thì tốt nhưng phải đầy đủ
> Tập trung hướng tới mục đích cuối cùng


## 2. Uninformed search

### 2 bước chính của 1 thuật toán

- Chọn 1 Node (Search Stratery)
- Mở rộng ra các Node
- Lặp cho tới khi kết thúc

### Tree search

- Frontier: tập các node mà chưa đi đến/có thể mở rộng
- Vấn đề: vòng lặp vô tận

### Graph search

- Tương tự Tree search
- Thêm mảng explored: chứa các Node đã đi qua

### 2 loại thuật toán tìm kiếm

- Tìm kiếm mù: Uninformed search (không có thông tin trước); Chọn các node 1 cách ngẫu nhiên
- Tìm kiếm có thông tin:  Informed search
  - Nhanh hơn
  - Nhưng phải cung cấp dữ liệu

### Breadth first search

![image-20230817152753812](../assets/image-20230817152753812.png)

- Chọn 1 node gốc, mở rộng nó ra đầu tiên
- Mở rộng tất các các node con của node gốc 
- Tiếp tục mở rộng các node con của nó

#### 1 thành phần của node:

- state: trạng thái hiện tại
- parent: node cha của node này
- action: hành động dẫn đến node này  ==>  để truy solution
- path-cost: từ initial state đến đây
- Hàm **Child Node**(problem, parent, action): ==> node

