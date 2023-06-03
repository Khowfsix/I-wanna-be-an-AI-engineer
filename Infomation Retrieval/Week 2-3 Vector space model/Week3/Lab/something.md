---
title: "Lab 2 - Boolean and Vector Space Model"
author: "your name"
date: "2023/02/14"
output: html_document
---


```{r setup, include=FALSE}
knitr::opts_chunk$set(echo = TRUE)
```

## Code mẫu
Để thực hiện bài lab này bạn sẽ sử dụng phần code mẫu trong file **lab2.zip** và viết thêm các hàm hoặc các đoạn mã để thực hiện các công việc theo yêu cầu bên dưới. 

## Cách nộp bài làm
Bạn sẽ nộp lại project đã làm (**không bao gồm các thư viện .jar** trong project). Ngoài ra, bạn cần nộp file báo cáo chứa java code và kết quả dùng file **Lab2.Rmd** được dung cấp sẵn. Đây là file **Rmarkdown**. **Markdown** là cú pháp đơn giản cho phép dễ dạng tạo các báo. **Rmarkdown** tích hợp code R với cú pháp **Markdown** để có thể tạo ra các báo cáo vừa có text, code và kết quả. Ở đây ta không dùng code R mà dùng Java. Xem thêm cú pháp của Rmarkdown ở https://github.com/rstudio/cheatsheets/raw/master/rmarkdown-2.0.pdf để biết cách viết. Dùng **RStudio** để soạn thảo và viết báo cáo. Khi cần biên dịch ra file .html thì nhấp vào **Knit** và chọn **Knit to HTML**. Nếu cần tìm hiểu thêm về **Rmarkdown** thì bạn có thể xem ở https://bookdown.org/yihui/rmarkdown/.

## Yêu cầu
### Lập chỉ mục bộ sưu tập tài liệu (document collection)
Đầu tiên, bạn cần thực thi file **LuceneBuildIndex.java** trong package **index** của project để lập chỉ mục bộ sưu tập tài liệu cho sẵn (file **example_corpus.gz** trong thư mục **docs** của project).

### 1. Hiện thực Boolean retrieval model với phép AND dùng chỉ mục đã tạo
Tiếp theo, bạn sẽ hiện thực hàm tìm kiếm Boolean AND dùng Lucene. Bạn sẽ phải duyệt qua posting list của từng từ (term) trong query từ index đã xây dựng được. Bạn cần hiện thực các hàm `searchBooleanAND` trong file **LuceneSearchIndex.java** để tìm tất cả tài liệu chứa tất cả các query term. Tập kết quả trả về cần được sắp xếp theo `docno`. Sau khi hiện thực hàm `searchBooleanAND` xong, bạn copy nội dung và dán đè vào đoạn mã bên dưới.
  
```{java}
/**
 * Hiện thực Boolean AND
 *
 * @param index		Lucene index reader
 * @param field 	index field để tìm query
 * @param queryTerms 	Danh sách các query term
 * 
 * @return 	Danh sách kết quả (sắp xếp theo docno)
 */
public static List<SearchResult> searchBooleanAND(IndexReader index, 
		   String field, List<String> queryTerms) throws Exception {
	// Viết phần hiện thực của bạn ở đây 
	// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết

	return null;
}
```

Kết quả sau khi chạy với input đã cho là (copy kết quả và dán vào đây, giữa cặp dấu ``` ...```):

```
...
```


### 2. Hiện thực các mô hình không gian vector (VSM) với hàm xếp hạng TF-IDF dùng chỉ mục đã tạo
Bạn sẽ hiện thực hàm xếp hạng TF-IDF dùng Lucene. Bạn sẽ phải duyệt qua posting list của từng từ (term) trong query từ index đã xây dựng được. Bạn cần hiện thực các hàm `searchTFIDF` trong file **LuceneSearchIndex.java**. Sau khi hiện thực hàm `searchTFIDF` xong, bạn copy nội dung và dán đè vào đoạn mã bên dưới. Hàm xếp hạng `TF-IDF` được mô tả bởi công thức sau:
  $$f(d,q) = \sum_{w \in q} freq(w, d) \times log \frac{N + 1}{n_w + 1}$$
Trong đó:
  
- $f(d, q)$ là giá trị mô tả mức độ liên quan của tài liệu $d$ với truy vấn $q$,
  
- $freq(w, d)$ là tần số của từ $w$ trong tài liệu $d$,
  
- $n_w$ là số tài liệu chứa từ $w$ trong collection (còn gọi là *document frequency* của $w$ trong collection),
  
- $N$ là số tài liệu trong collection.


```{java}
/**
 * Hiện thực TFxIDF dùng chiến lược term-at-a-time (taat)
 *
 * @param index 	Lucene index reader
 * @param field 	index field để tìm query
 * @param queryTerms 	Danh sách các query term
 *            
 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
 */
public static List<SearchResult> searchTFIDF(IndexReader index, 
		String field, List<String> queryTerms) throws IOException {
	// Viết phần hiện thực của bạn ở đây
	// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết

	return null;
}
```

Kết quả sau khi chạy với input đã cho là (copy kết quả và dán vào đây, giữa cặp dấu ``` ...```):

```
...
```


### 3. Hiện thực các mô hình không gian vector (VSM) với hàm xếp hạng cosine dùng chỉ mục đã tạo
Bạn sẽ hiện thực hàm xếp hạng cosine dùng Lucene. Bạn sẽ phải duyệt qua posting list của từng từ (term) trong query từ index đã xây dựng được. Bạn cần hiện thực các hàm `searchSVMCosine` trong file **LuceneSearchIndex.java**. Sau khi hiện thực hàm `searchSVMCosine` xong, bạn copy nội dung và dán đè vào đoạn mã bên dưới. Hàm xếp hạng `SVM Cosine` được mô tả bởi công thức sau:

$$f(d,q) = \frac{\sum_{w \in q} freq(w, q) \times freq(w, d)}{\sqrt{\sum_{w \in q} freq(w, q)^2} \times \sqrt{\sum_{w \in d} freq(w, d)^2}} $$

Trong đó:
    
- $f(d, q)$ là giá trị mô tả mức độ liên quan của tài liệu $d$ với truy vấn $q$,
    
- $freq(w, q)$, $freq(w, d)$ lần lượt là tần số của từ $w$ trong truy vấn $q$ và tài liệu $d$.
    
```{java}
/**
 * Hiện thực VSM (cosine similarity) dùng chiến lược term-at-a-time (taat)
 *
 * @param index 	Lucene index reader
 * @param field 	index field để tìm query
 * @param queryTerms 	Danh sách các query term
 *            
 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
 */
public static List<SearchResult> searchSVMCosine(IndexReader index, 
		String field, List<String> queryTerms) throws IOException {
	// Viết phần hiện thực của bạn ở đây
	// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết
	
	return null;
}
```

Kết quả sau khi chạy với input đã cho là (copy kết quả và dán vào đây, giữa cặp dấu ``` ...```):

```
...
```

