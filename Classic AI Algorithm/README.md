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

AI (agent) tác động 1  action nào đó vào môi trường (enviroment) làm enviroment thay đổi trạng thái từ $s \rightarrow s'$ và ngược lại môi trường cung cấp các percepts (observation) để agent biết được thông tin về môi trường

Sau vài action thì agent sẽ có reward để nó hướng tới kết quả

> và Agent sẽ cố gắng có reward cao nhất

$\rightarrow$ Thiết kế hệ thống chấm điểm hợp lý cho Agent

Nên đưa chỉ dẫn chi tiết gợi ý chi tiết cho Agent, tuy nhiên phải đủ chứ không nên thiên về 1 hướng; 

> Chi tiết thì tốt nhưng phải đầy đủ
> Tập trung hướng tới mục đích cuối cùng


## 2. Uninform Search

