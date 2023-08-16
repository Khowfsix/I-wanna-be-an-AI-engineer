package search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.management.Query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.hppc.IntIntHashMap.IntIntCursor;

import utils.LuceneUtils;

/**
 * This is an example of accessing corpus statistics and corpus-level term
 * statistics.
 *
 * @author Lý Hồng Phát
 * @version 2023-02-26
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

			List<SearchResult> resultsBooleanAND = searchBooleanAND(index, field, queryTerms);
			List<SearchResult> resultsTFIDF = searchTFIDF(index, field, queryTerms);
			List<SearchResult> resultsVSMCosine = searchVSMCosine(index, field, queryTerms);

			// Không thay đổi các thiết lập output sau
			File dirOutput = new File(outputPath);
			dirOutput.mkdirs();

			{
				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "results_BooleanAND")));
				SearchResult.writeTRECFormat(writer, "0", "BooleanAND", resultsBooleanAND, resultsBooleanAND.size());
				SearchResult.writeTRECFormat(System.out, "0", "BooleanAND", resultsBooleanAND, 10);
				writer.close();
			}

			{
				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "results_TFIDF")));
				SearchResult.writeTRECFormat(writer, "0", "TFIDF", resultsTFIDF, resultsTFIDF.size());
				SearchResult.writeTRECFormat(System.out, "0", "TFIDF", resultsTFIDF, 10);
				writer.close();
			}

			{
				PrintStream writer = new PrintStream(new FileOutputStream(new File(dirOutput, "results_VSMCosine")));
				SearchResult.writeTRECFormat(writer, "0", "VSMCosine", resultsVSMCosine, resultsVSMCosine.size());
				SearchResult.writeTRECFormat(System.out, "0", "VSMCosine", resultsVSMCosine, 10);
				writer.close();
			}

			index.close();
			dir.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hiện thực Boolean AND
	 *
	 * @param index      Lucene index reader
	 * @param field      index field để tìm query
	 * @param queryTerms Danh sách các query term
	 * 
	 * @return Danh sách các kết quả (sắp xếp theo docno)
	 */
	public static List<SearchResult> searchBooleanAND(IndexReader index, String field, List<String> queryTerms)
			throws Exception {

		// WRITE ALGORITHM HERE
//		SearchResult(int docid, String docno, double score)

		List<SearchResult> booleanSearchANDList = new ArrayList<SearchResult>();

//		Khởi tạo list answerDocID gồm các docId của term đầu tiên 
		List<Integer> answerDocId = new ArrayList<Integer>();

		PostingsEnum posting0 = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(queryTerms.get(0)),
				PostingsEnum.FREQS);
		answerDocId.addAll(getListFromPostingList(posting0));

		for (int i = 0; i < queryTerms.size() - 1; i++) {
			PostingsEnum nextPosting = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(queryTerms.get(i)),
					PostingsEnum.FREQS);
			answerDocId = andPostingList(answerDocId, getListFromPostingList(nextPosting));
		}

		for (int docId : answerDocId) {
//			Get docno
			String docnoString = LuceneUtils.getDocno(index, "docno", docId);
			booleanSearchANDList.add(new SearchResult(docId, docnoString, 1));
		}

//		sắp xếp theo docno
		for (int i = 0; i < booleanSearchANDList.size() - 1; i++) {
			for (int j = i + 1; j < booleanSearchANDList.size(); j++) {
				SearchResult firstDoc = booleanSearchANDList.get(i);
				SearchResult secDoc = booleanSearchANDList.get(j);

				int docno1 = Integer.valueOf(firstDoc.docno.substring(4));
				int docno2 = Integer.valueOf(secDoc.docno.substring(4));

				if (docno1 > docno2) {
					SearchResult temp = new SearchResult(booleanSearchANDList.get(i).docid,
							booleanSearchANDList.get(i).docno, booleanSearchANDList.get(i).score);
					booleanSearchANDList.get(i).docid = booleanSearchANDList.get(j).docid;
					booleanSearchANDList.get(i).docno = booleanSearchANDList.get(j).docno;
					booleanSearchANDList.get(i).score = booleanSearchANDList.get(j).score;
					booleanSearchANDList.get(j).docid = temp.docid;
					booleanSearchANDList.get(j).docno = temp.docno;
					booleanSearchANDList.get(j).score = temp.score;
				}
			}
		}

		return booleanSearchANDList;
	}

	public static List<Integer> andPostingList(List<Integer> p1, List<Integer> p2) {
		List<Integer> answer = new ArrayList<Integer>();

		int pp1 = 0;
		int pp2 = 0;

		while ((pp1 < p1.size()) && (pp2 < p2.size())) {
			if (p1.get(pp1) == p2.get(pp2)) {
				answer.add(p1.get(pp1));
				pp1++;
				pp2++;
			} else if (p1.get(pp1) < p2.get(pp2)) {
				answer.add(p1.get(pp1));
				pp1++;
			} else {
				answer.add(p2.get(pp2));
				pp2++;
			}
		}

		return answer;
	}

	public static List<Integer> getListFromPostingList(PostingsEnum postingsEnum) throws IOException {
		List<Integer> myList = new ArrayList<Integer>();
		int docId = postingsEnum.nextDoc();

		while (docId != DocIdSetIterator.NO_MORE_DOCS) {
			myList.add(docId);
			docId = postingsEnum.nextDoc();
		}
		return myList;
	}

	/**
	 * Hiện thực SVM (TF-IDF)
	 *
	 * @param index      Lucene index reader
	 * @param field      index field để tìm query
	 * @param queryTerms Danh sách các query term
	 * 
	 * @return Danh sách các kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> searchTFIDF(IndexReader index, String field, List<String> queryTerms)
			throws IOException {

		// WRITE ALGORITHM HERE
//		SearchResult(int docid, String docno, double score)

		List<SearchResult> searchTFIDF_List = new ArrayList<SearchResult>();

		int M = index.numDocs(); // số lượng documents

		for (String termString : queryTerms) {
			PostingsEnum posting = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(termString),
					PostingsEnum.FREQS);
//			Lấy postingList của term

			Term term = new Term(field, termString);
			if (posting != null) {
//				if the term does not appear in any document, the posting object may be null
				int docid;

				while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
//					Lấy docno để lưu vào kết quả
					String docno = LuceneUtils.getDocno(index, "docno", docid);

//					Tính term frequency TF
					int freqWd = posting.freq();

//					Tính document frequency DF
					int DF = index.docFreq(term);

//					Tính IDF (Inverse document frequency)
					double IDF = Math.log((double) (M + 1) / (DF + 1));

//					Tính score của term này
					double score = freqWd * IDF;

					Boolean isExist = false;
					// Tìm trong kết quả xem đã tồn tại chưa
					for (SearchResult doc : searchTFIDF_List) {
						if (doc.docno.equals(docno)) {
							// Cộng dồn score nếu đã tồn tại trong kết quả
							doc.score += score;
							isExist = true;
							break;
						}
					}
					// Nếu chưa tồn tại thì thêm mới
					if (!isExist) {
						searchTFIDF_List.add(new SearchResult(docid, docno, score));
					}
				}
			}
		}

//		sắp xếp theo score
		for (int i = 0; i < searchTFIDF_List.size() - 1; i++) {
			for (int j = i + 1; j < searchTFIDF_List.size(); j++) {
				SearchResult firstDoc = searchTFIDF_List.get(i);
				SearchResult secDoc = searchTFIDF_List.get(j);
				if (firstDoc.score < secDoc.score) {
					SearchResult temp = new SearchResult(searchTFIDF_List.get(i).docid, searchTFIDF_List.get(i).docno,
							searchTFIDF_List.get(i).score);
					searchTFIDF_List.get(i).docid = searchTFIDF_List.get(j).docid;
					searchTFIDF_List.get(i).docno = searchTFIDF_List.get(j).docno;
					searchTFIDF_List.get(i).score = searchTFIDF_List.get(j).score;
					searchTFIDF_List.get(j).docid = temp.docid;
					searchTFIDF_List.get(j).docno = temp.docno;
					searchTFIDF_List.get(j).score = temp.score;
				}
			}
		}

		return searchTFIDF_List;
	}

	/**
	 * Hiện thực VSM (cosine similarity)
	 *
	 * @param index      Lucene index reader
	 * @param field      index field để tìm query
	 * @param queryTerms Danh sách các query term
	 * 
	 * @return Danh sách các kết quả (sắp xếp theo độ liên quan)
	 */
	public static List<SearchResult> searchVSMCosine(IndexReader index, String field, List<String> queryTerms)
			throws IOException {

//		WRITE ALGORITHM HERE
		List<SearchResult> searchVSMCosineList = new ArrayList<SearchResult>();

//		sumFreqWq bình phương: bình phương tổng tần số của từ w trong truy vấn q
		List<Integer> qfList = new ArrayList<Integer>();
		double SumFreqWq_2 = 0;
		for (int i = 0; i < queryTerms.size(); i++) {
			String term = queryTerms.get(i);
			qfList.add(freqWinQ(term, queryTerms));
			SumFreqWq_2 += qfList.get(i) * qfList.get(i);
		}
		List<Integer> tfList = new ArrayList<Integer>();

		for (int i = 0; i < queryTerms.size(); i++) {
			String termString = queryTerms.get(i);

//			Lấy postingList của term
			PostingsEnum posting = MultiTerms.getTermPostingsEnum(index, field, new BytesRef(termString),
					PostingsEnum.FREQS);

//			Term term = new Term(field, termString);
			if (posting != null) {
				int docid;

				while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
//					Lấy docno để lưu vào kết quả
					String docno = LuceneUtils.getDocno(index, "docno", docid);

//					Tính term frequency TF
					int freqWd = posting.freq();
					tfList.add(freqWd);

//					Tính tần số của từ w trong truy vấn q
					double fredWq = qfList.get(i);

//					Tính score của term này
					double score = (fredWq * freqWd);

					Boolean isExist = false;
					// Tìm trong kết quả xem đã tồn tại chưa
					for (SearchResult doc : searchVSMCosineList) {
						if (doc.docno.equals(docno)) {
							// Cộng dồn score nếu đã tồn tại trong kết quả
							doc.score += score;
							isExist = true;
							break;
						}
					}
					// Nếu chưa tồn tại thì thêm mới
					if (!isExist) {
						searchVSMCosineList.add(new SearchResult(docid, docno, score));
					}
				}
			}
		}

//		sumTF_2: tổng TF bình phương
		double sumTF_2 = 0;
		for (int tf : tfList) {
			sumTF_2 += tf * tf;
		}

		for (SearchResult score : searchVSMCosineList) {
			double newScore = score.getScore() / (Math.sqrt(SumFreqWq_2) * Math.sqrt(sumTF_2));
			score.setScore(newScore);
		}

//		sắp xếp theo score
		for (int i = 0; i < searchVSMCosineList.size() - 1; i++) {
			for (int j = i + 1; j < searchVSMCosineList.size(); j++) {
				SearchResult firstDoc = searchVSMCosineList.get(i);
				SearchResult secDoc = searchVSMCosineList.get(j);
				if (firstDoc.score < secDoc.score) {
					SearchResult temp = new SearchResult(searchVSMCosineList.get(i).docid,
							searchVSMCosineList.get(i).docno, searchVSMCosineList.get(i).score);
					searchVSMCosineList.get(i).docid = searchVSMCosineList.get(j).docid;
					searchVSMCosineList.get(i).docno = searchVSMCosineList.get(j).docno;
					searchVSMCosineList.get(i).score = searchVSMCosineList.get(j).score;
					searchVSMCosineList.get(j).docid = temp.docid;
					searchVSMCosineList.get(j).docno = temp.docno;
					searchVSMCosineList.get(j).score = temp.score;
				}
			}
		}

		return searchVSMCosineList;
	}

	public static int freqWinQ(String word, List<String> querry) {
		int freqWq = 0;
		for (String t : querry) {
			if (t.equals(word)) {
				freqWq = freqWq + 1;
			}
		}
		return freqWq;
	}

}