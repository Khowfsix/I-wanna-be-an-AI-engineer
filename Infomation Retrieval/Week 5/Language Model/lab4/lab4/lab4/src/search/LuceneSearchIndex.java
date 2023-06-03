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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import utils.LuceneUtils;

/**
 * This is an example of accessing corpus statistics and corpus-level term
 * statistics.
 *
 * @author Lý Hồng Phát
 * @version 2023-03-14
 */
public class LuceneSearchIndex {

	public static void main(String[] args) {
		try {

			// index path
			String indexPath = "./index";
			// folder để xuất kết quả
			String outputPath = "./out";

			// query và field để tìm kiếm
			String field = "text";
			String query = "retrieval model";

			// Analyzer specifies options for text tokenization and normalization (e.g.,
			// stemming, stop words removal, case-folding)
			Analyzer analyzer = new Analyzer() {
				@Override
				protected TokenStreamComponents createComponents(String fieldName) {
					// Step 1: tokenization (Lucene's StandardTokenizer is suitable for most text
					// retrieval occasions)
					TokenStreamComponents ts = new TokenStreamComponents(new StandardTokenizer());
					// Step 2: transforming all tokens into lowercased ones (recommended for the
					// majority of the problems)
					ts = new TokenStreamComponents(ts.getSource(), new LowerCaseFilter(ts.getTokenStream()));
					// Step 3: whether to remove stop words (unnecessary to remove stop words unless
					// you can't afford the extra disk space)
					// Uncomment the following line to remove stop words
					// ts = new TokenStreamComponents( ts.getSource(), new StopFilter(
					// ts.getTokenStream(), EnglishAnalyzer.ENGLISH_STOP_WORDS_SET ) );
					// Step 4: whether to apply stemming
					// Uncomment one of the following two lines to apply Krovetz or Porter stemmer
					// (Krovetz is more common for IR research)
					ts = new TokenStreamComponents(ts.getSource(), new KStemFilter(ts.getTokenStream()));
					// ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter(
					// ts.getTokenStream() ) );
					return ts;
				}
			};

			// Tách query thành các token (dùng analyzer giống bước indexing)
			List<String> queryTerms = LuceneUtils.tokenize(query, analyzer);

			Directory dir = FSDirectory.open(new File(indexPath).toPath());
			IndexReader index = DirectoryReader.open(dir);

			float lambda = 0.1f;
			float mu = 2000;

			List<SearchResult> QLJMSmoothingResults = QLJMSmoothing(index, field, queryTerms, lambda);

			List<SearchResult> QLDirichletSmoothingResults = QLDirichletSmoothing(index, field, queryTerms, mu);

			// Không thay đổi các thiết lập output sau
			File dirOutput = new File(outputPath);
			dirOutput.mkdirs();

			{
				PrintStream writer = new PrintStream(
						new FileOutputStream(new File(dirOutput, "QLJMSmoothing_results")));
				SearchResult.writeTRECFormat(writer, "0", "QLJMSmoothing", QLJMSmoothingResults,
						QLJMSmoothingResults.size());
				SearchResult.writeTRECFormat(System.out, "0", "QLJMSmoothing", QLJMSmoothingResults, 10);
				writer.close();
			}

//			{
//				PrintStream writer = new PrintStream(
//						new FileOutputStream(new File(dirOutput, "QLDirichletSmoothing_results")));
//				SearchResult.writeTRECFormat(writer, "0", "QLDirichletSmoothing", QLDirichletSmoothingResults,
//						QLDirichletSmoothingResults.size());
//				SearchResult.writeTRECFormat(System.out, "0", "QLDirichletSmoothing", QLDirichletSmoothingResults, 10);
//				writer.close();
//			}

			index.close();
			dir.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hiện thực mô hình Query Likelihood with Jelinek-Mercer smoothing
	 * 
	 * @param index      Lucene index reader
	 * @param field      index field để tìm query
	 * @param queryTerms Danh sách các query term
	 * @param lambda     Tham số lambda (0.1 <= lambda <= 0.9) của Jelinek-Mercer
	 *                   smoothing
	 * 
	 * @return Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> QLJMSmoothing(IndexReader index, String field, List<String> queryTerms,
			double lambda) throws IOException {
		// Viết phần hiện thực của bạn ở đây
		// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết

		PriorityQueue<SearchResult> pq = new PriorityQueue<>((o1, o2) -> o1.getScore().compareTo(o2.getScore()));

		List<String> qterms_filtered = new ArrayList<>();
		for (String term : queryTerms) {
			if (index.docFreq(new Term(field, term)) > 0) {
				qterms_filtered.add(term);
			}
		}
		queryTerms = qterms_filtered;

		int k = 10; // priority queue size
		int[] cursors = new int[queryTerms.size()]; // cursors point to current docid for each posting
		double[] p_wC = new double[queryTerms.size()];
		PostingsEnum[] postings = new PostingsEnum[queryTerms.size()]; // postings for each query term
		int f_wq[] = new int[queryTerms.size()];

		long C = index.getSumTotalTermFreq("text");

		for (int ix = 0; ix < queryTerms.size(); ix++) {
			Term term = new Term(queryTerms.get(ix));
			p_wC[ix] = (double) index.totalTermFreq(term) / C;

			postings[ix] = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(queryTerms.get(ix)),
					PostingsEnum.FREQS);
			cursors[ix] = postings[ix].nextDoc();
			f_wq[ix] = freqQ(queryTerms.get(ix), queryTerms);
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
			for (int ix = 0; ix < cursors.length; ix++) {
				if (cursors[ix] == docid_smallest) {
					double doclen = 0;
					TermsEnum termsEnum = index.getTermVector(cursors[ix], field).iterator();
					while (termsEnum.next() != null) {
						doclen += termsEnum.totalTermFreq();
					}

					score += f_wq[ix] * Math.log(1 + ((1 - lambda) / lambda) * (postings[ix].freq() / (doclen)));
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
					pq.add(new SearchResult(docid_smallest, docno, score));
				}
			}
		}

		List<SearchResult> results = new ArrayList<>(pq.size());
		results.addAll(pq);
		Collections.sort(results, (o1, o2) -> o2.getScore().compareTo(o1.getScore()));

		return results;
	}

	/**
	 * Hiện thực mô hình Query Likelihood with Dirichlet smoothing
	 *
	 * @param index      Lucene index reader
	 * @param field      index field để tìm query
	 * @param queryTerms Danh sách các query term
	 * @param mu         Tham số mu (500 <= mu <= 5000) của Dirichlet smoothing
	 * 
	 * @return Danh sách kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> QLDirichletSmoothing(IndexReader index, String field, List<String> queryTerms,
			double mu) throws IOException {
		// Viết phần hiện thực của bạn ở đây
		// Bạn có thể viết thêm các hàm phụ trợ nếu cần thiết
		PriorityQueue<SearchResult> pq = new PriorityQueue<>((o1, o2) -> o1.getScore().compareTo(o2.getScore()));

		List<String> qterms_filtered = new ArrayList<>();
		for (String term : queryTerms) {
			if (index.docFreq(new Term(field, term)) > 0) {
				qterms_filtered.add(term);
			}
		}
		queryTerms = qterms_filtered;

		int k = 10; // priority queue size
		int[] cursors = new int[queryTerms.size()]; // cursors point to current docid for each posting
		double[] idfs = new double[queryTerms.size()]; // idfs for each query term
		PostingsEnum[] postings = new PostingsEnum[queryTerms.size()]; // postings for each query term
		for (int ix = 0; ix < queryTerms.size(); ix++) {
			postings[ix] = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(queryTerms.get(ix)),
					PostingsEnum.FREQS);
			cursors[ix] = postings[ix].nextDoc();
//			idfs[ix] = getIDF(index, field, queryTerms.get(ix));
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
			for (int ix = 0; ix < cursors.length; ix++) {
				if (cursors[ix] == docid_smallest) {
					score += idfs[ix] * postings[ix].freq(); // cumulative sum of tf-idf for each doc
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
					pq.add(new SearchResult(docid_smallest, docno, score));
				}
			}
		}

		List<SearchResult> results = new ArrayList<>(pq.size());
		results.addAll(pq);
		Collections.sort(results, (o1, o2) -> o2.getScore().compareTo(o1.getScore()));

		return results;
	}

	public static int freqQ(String word, List<String> query) {
		int freqWq = 0;
		for (String t : query) {
			if (t.equals(word)) {
				freqWq = freqWq + 1;
			}
		}
		return freqWq;
	}

}