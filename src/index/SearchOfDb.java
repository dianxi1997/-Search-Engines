package index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.analysis.Analyzer;

/**
 * 查找类
 * @author Administrator
 *
 */
public class SearchOfDb {
	public static String dir = "./index/last/";	// 索引文件
	private IndexSearcher searcher = null; // 搜索句柄
	public static int movie_num = 1000; // 电影条数
	private String[] field = new String[]{  // 要显示的字段
											"count_num",
											"score", 
											"detail_url", 
											"play_url", 
											"img_src", 
											"movie_name", 
											"movie_detail", 
											"inq",
											"year",
											"director",
											"actor",
											"type",
											"country"
											};
	/**
	 * 构造方法
	 * 返回一个搜索资源的句柄
	 */
	public SearchOfDb() {
		// CreateIndexOfDb.INDEXDIR = SearchOfDb.dir;
		IndexReader reader = CreateIndexOfDb.getIndexReader();
		System.out.print(reader);
        searcher = new IndexSearcher(reader);
	}
	/**
	 * 根据索引文件的位置来初始化搜索句柄
	 * @param dir
	 */
	public SearchOfDb(String dir) {
		CreateIndexOfDb.INDEXDIR = SearchOfDb.dir = dir;
		IndexReader reader = CreateIndexOfDb.getIndexReader();
        searcher = new IndexSearcher(reader);
	}
	/**
	 * 单字段检索
	 * @param field
	 * @param value
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONArray searchMovieBySimpleKey(String field, String value) throws IOException, ParseException {
		System.out.println("field: " + field);
		System.out.println("value: " + value);
		// 要返回的HashMap
		List movie = new ArrayList();
		// 初始化搜索器
	    QueryParser parser = new QueryParser(field, CreateIndexOfDb.getAnalyzer());
	    // 设置空格为逻辑与
	    parser.setDefaultOperator(Operator.AND);
	    // 加载搜索的选项
	    Query query = parser.parse(value);
	    // 获取搜索到的数据
		ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
		// 遍历搜索到的数据
	    for (int i = 0; i < hits.length; i++) {
	        movie.add(this.getMovieListThroughDoc(searcher.doc(hits[i].doc))); 	         
	     }
		return new JSONArray(movie);	
	}
	public JSONArray searchMovieBySimpleKeyOutOneField(String field, String value) throws ParseException, IOException {
		System.out.println("field: " + field);
		System.out.println("value: " + value);
		// 要返回的HashMap
		List movie = new ArrayList();
		// 初始化搜索器
	    QueryParser parser = new QueryParser(field, CreateIndexOfDb.getAnalyzer());
	    // 设置空格为逻辑与
	    parser.setDefaultOperator(Operator.AND);
	    // 加载搜索的选项
	    Query query = parser.parse(value);
	    // 获取搜索到的数据
		ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
		// 遍历搜索到的数据
	    for (int i = 0; i < hits.length; i++) {
	    	Document doc = searcher.doc(hits[i].doc);  
	    	movie.add(doc.get(field));
	     }	
	    return new JSONArray(movie);
	}
	/**
	 * 多字段检索
	 * @param fields
	 * @param values
	 * @param clauses
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONArray searchMovieByMultiFields(String[] fields, String[] values, Occur[] clauses) throws IOException, ParseException {
		// 要返回的List
		List movie = new ArrayList();
		// 初始化搜索器
	    Query query = MultiFieldQueryParser.parse(values, fields, clauses, CreateIndexOfDb.getAnalyzer());
	 	//  获取搜索到的数据
	    System.out.println(query.toString());
	 	ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
		// 遍历搜索到的数据
	    for (int i = 0; i < hits.length; i++) {
	    	movie.add(this.getMovieListThroughDoc(searcher.doc(hits[i].doc)));       
	     }	
	    // 返回JSON的数据格式
	    JSONArray jsonArray = new JSONArray(movie);
		return jsonArray;
	}
	
	/**
	 * 通过Document对象返回对应的HashMap
	 * @param doc
	 * @return
	 */
	private HashMap<String, String> getMovieListThroughDoc(Document doc) {
		HashMap<String, String> item = new HashMap<String, String>();
		for(String i : this.field) {			
			item.put(i, doc.get(i));
		}
		return item;
	}
	
}
