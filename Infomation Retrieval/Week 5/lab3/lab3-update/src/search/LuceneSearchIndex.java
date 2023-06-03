package search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import utils.LuceneUtils;

/**
 * This is an example of accessing corpus statistics and corpus-level term statistics.
 *
 * @author yourname
 * @version 2023-02-28
 */
public class LuceneSearchIndex {

    public static void main( String[] args ) {
        try {

            // index path
			String indexPath = "./index";
			// folder để xuất kết quả
			String outputPath = "./out";
			
			// query và field để tìm kiếm
			String field = "text";
			String query = "retrieval model";

            // Analyzer specifies options for text tokenization and normalization (e.g., stemming, stop words removal, case-folding)
            Analyzer analyzer = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents( String fieldName ) {
                    // Step 1: tokenization (Lucene's StandardTokenizer is suitable for most text retrieval occasions)
                    TokenStreamComponents ts = new TokenStreamComponents( new StandardTokenizer() );
                    // Step 2: transforming all tokens into lowercased ones (recommended for the majority of the problems)
                    ts = new TokenStreamComponents( ts.getSource(), new LowerCaseFilter( ts.getTokenStream() ) );
                    // Step 3: whether to remove stop words (unnecessary to remove stop words unless you can't afford the extra disk space)
                    // Uncomment the following line to remove stop words
                    // ts = new TokenStreamComponents( ts.getSource(), new StopFilter( ts.getTokenStream(), EnglishAnalyzer.ENGLISH_STOP_WORDS_SET ) );
                    // Step 4: whether to apply stemming
                    // Uncomment one of the following two lines to apply Krovetz or Porter stemmer (Krovetz is more common for IR research)
                    ts = new TokenStreamComponents( ts.getSource(), new KStemFilter( ts.getTokenStream() ) );
                    // ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter( ts.getTokenStream() ) );
                    return ts;
                }
            };

         // Tách query thành các token (dùng analyzer giống bước indexing)
 			List<String> queryTerms = LuceneUtils.tokenize(query, analyzer);

 			Directory dir = FSDirectory.open(new File(indexPath).toPath());
 			IndexReader index = DirectoryReader.open(dir);

 			List<SearchResult> taatTFIDFResults = taatTFIDF(index, field, queryTerms);
 			List<SearchResult> taatVSMCosineResults = taatVSMCosine(index, field, queryTerms);

 			List<SearchResult> daatTFIDFResults = daatTFIDF(index, field, queryTerms);
 			List<SearchResult> daatVSMCosineResults = daatVSMCosine(index, field, queryTerms);
 			
 			// Không thay đổi các thiết lập output sau
 			File dirOutput = new File(outputPath);
 			dirOutput.mkdirs();

 			{
 				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "taat_TFIDF_results")));
 				SearchResult.writeTRECFormat(writer, "0", "taatTFIDF", taatTFIDFResults, taatTFIDFResults.size());
 				SearchResult.writeTRECFormat(System.out, "0", "taatTFIDF", taatTFIDFResults, 10);
 				writer.close();
 			}
 			
 			{
 				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "daat_TFIDF_results")));
 				SearchResult.writeTRECFormat(writer, "0", "daatTFIDF", daatTFIDFResults, daatTFIDFResults.size());
 				SearchResult.writeTRECFormat(System.out, "0", "daatTFIDF", daatTFIDFResults, 10);
 				writer.close();
 			}
 			
 			// Đoạn lệnh bên dưới được chuyển thành comments để test hàm taatTFIDF
 			/*
 			{
 				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "taat_VSMCosine_results")));
 				SearchResult.writeTRECFormat(writer, "0", "taatVSMCosine", taatVSMCosineResults, taatVSMCosineResults.size());
 				SearchResult.writeTRECFormat(System.out, "0", "taatVSMCosine", taatVSMCosineResults, 10);
 				writer.close();
 			}

 			{
 				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "daat_VSMCosine_results")));
 				SearchResult.writeTRECFormat(writer, "0", "daatVSMCosine", daatVSMCosineResults, taatVSMCosineResults.size());
 				SearchResult.writeTRECFormat(System.out, "0", "daatVSMCosine", daatVSMCosineResults, 10);
 				writer.close();
 			}
 			 */	
 			
 			index.close();
 			dir.close();

            } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

	/**
	 * Hiện thực TFxIDF dùng chiến lược term-at-a-time (taat)
	 * 
	 * @param index 	Lucene index reader
	 * @param field 	index field để tìm query
	 * @param queryTerms 	Danh sách các query term
	 *            
	 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> taatTFIDF(IndexReader index, 
			String field, List<String> queryTerms) throws IOException {
		
		// Đây là hiện thực mẫu cho hàm TFxIDF dùng chiến lược term-at-a-time (taat)
		List<SearchResult> rs = null;
		
		if(!queryTerms.isEmpty()) {
			rs = new ArrayList<SearchResult>();
			HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
			
			for(String term : queryTerms) {
				PostingsEnum posting = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(term), PostingsEnum.POSITIONS);
				if (posting != null) { // nếu term không có trong document nào cả, posting sẽ là null
					int docid;
					int tf;	
					int n;		// document frequency của term trong "text" field
					int N;		// tổng số document trong index
					double idf;	// cộng 1 vào N và n để tránh trường hợp N = 0 và n = 0
					double tfidf;
					
					N = index.numDocs();
					n = index.docFreq(new Term(field, term));
					idf = Math.log((N + 1.0) / (n + 1.0)); 
					while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						tf = posting.freq();
						tfidf = tf * idf;
						if (!hm.containsKey(docid))
							hm.put(docid, tfidf);
						else
							hm.put(docid, tfidf + hm.get(docid));
					}
				}
			}
			
			for(int docid : hm.keySet()) {
				String docno = LuceneUtils.getDocno(index, "docno", docid);
				rs.add(new SearchResult(docid, docno, hm.get(docid)));
			}
		}

		rs.sort(Comparator.comparing(SearchResult::getScore).reversed());
		return rs;
	}

	/**
	 * Hiện thực TFxIDF dùng chiến lược document-at-a-time (daat)
	 *
	 * @param index 	Lucene index reader
	 * @param field 	index field để tìm query
	 * @param queryTerms 	Danh sách các query term
	 *            
	 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> daatTFIDF(IndexReader index, 
			String field, List<String> queryTerms) throws IOException {

		// Đây là hiện thực mẫu cho hàm TFxIDF dùng chiến lược document-at-a-time (daat)
		PriorityQueue<SearchResult> pq = new PriorityQueue<>((o1, o2) -> o1.getScore().compareTo(o2.getScore()));
		
		List<String> qterms_filtered = new ArrayList<>();
		for (String term : queryTerms) {
			if (index.docFreq(new Term(field, term)) > 0) {
				qterms_filtered.add(term);
			}
		}
		queryTerms = qterms_filtered;
		
		int k = 10;	// priority queue size
		int[] cursors = new int[queryTerms.size()];	// cursors point to current docid for each posting 
		double[] idfs = new double[queryTerms.size()];	// idfs for each query term 
		PostingsEnum[] postings = new PostingsEnum[queryTerms.size()];	// postings for each query term 
		for (int ix = 0; ix < queryTerms.size(); ix++) {
			postings[ix] = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(queryTerms.get(ix)), PostingsEnum.FREQS );
			cursors[ix] = postings[ix].nextDoc();
			idfs[ix] = getIDF(index, field, queryTerms.get(ix));
		}
		
		String docno;
		while (true) {
			int docid_smallest = Integer.MAX_VALUE;
			for (int cursor : cursors) {
				if (cursor >= 0 && cursor < docid_smallest) {
					docid_smallest = cursor;
				}
			}
			if (docid_smallest == Integer.MAX_VALUE) {
				break;
			}
			double score = 0;
			for (int ix = 0; ix < cursors.length; ix++ ) {
				if (cursors[ix] == docid_smallest) {
					score += idfs[ix] * postings[ix].freq();	// cumulative sum of tf-idf for each doc
					cursors[ix] = postings[ix].nextDoc();
				}
			}
			
			docno = LuceneUtils.getDocno(index, "docno", docid_smallest);
			if (pq.size() < k) {
				pq.add(new SearchResult(docid_smallest, docno, score));
			} else {
				SearchResult result = pq.peek();
				if (score > result.getScore()) {
					pq.poll();
					pq.add(new SearchResult(docid_smallest, docno, score));]
				}
			}
		}
		
		List<SearchResult> results = new ArrayList<>( pq.size() );
		results.addAll(pq);
		Collections.sort(results, (o1, o2) -> o2.getScore().compareTo(o1.getScore()));
		
		return results;
	}
	
	public static double getIDF(IndexReader index, String field, String term) throws IOException {
		int N = index.numDocs();
		int n = index.docFreq(new Term( field, term));
		return (float) Math.log((N + 1.0) / (n + 1.0));
	}
	
	
	/**
	 * Hiện thực VSM (cosine similarity) dùng chiến lược term-at-a-time (taat)
	 *
	 * @param index 	Lucene index reader
	 * @param field 	index field để tìm query
	 * @param queryTerms 	Danh sách các query term
	 *            
	 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> taatVSMCosine(IndexReader index, 
			String field, List<String> queryTerms) throws IOException {
		// Viết phần hiện thực của bạn ở đây
		// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết
		
		return null;
	}
	
	/**
	 * Hiện thực VSM (cosine similarity) dùng chiến lược document-at-a-time (daat)
	 *
	 * @param index 	Lucene index reader
	 * @param field 	index field để tìm query
	 * @param queryTerms 	Danh sách các query term
	 *            
	 * @return 	Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> daatVSMCosine(IndexReader index, 
			String field, List<String> queryTerms) throws IOException {
		// Viết phần hiện thực của bạn ở đây
		// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết
		
		return null;
	}

}