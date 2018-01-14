package index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

import db.DBConnection;
import org.apache.lucene.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryRescorer;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;

/**
 * 创建电影表的索引
 * id 主键ID                           不被分析、不被索引、需要存储 
 * count_num 评论人数                                                          不被分析、不被索引、需要存储 
 * score 分数                                                                              不被分析、不被索引、需要存储
 * detail_url 原连接                                                           不被分析、不被索引、需要存储
 * play_url 播放连接                                                             不被分析、不被索引、需要存储
 * movie_name 电影名                                                           需要分析、需要索引、需要存储
 * 	director 导演                                                                  需要分析、需要索引、需要存储
 * 	actor 演员						     需要分析、需要索引、需要存储
 *  year 上映年份						     不被分析、需要索引、需要存储
 *  type 类型                                                                              需要分析、需要索引、需要存储
 *  country 国别                                                                     不被分析、需要索引、需要存储                
 * movie_detail 电影详情                                                 需要分析、需要索引、需要存储
 * inq 电影经典台词                                                                    需要分析、需要索引、需要存储
 * img_src 图片连接                                                                不被分析、不需要索引、需要存储
 * @author Administrator
 *
 */
public class CreateIndexOfDb {
	public static Connection conn = null; // 数据库连接对象
	public static String INDEXDIR = "/index/last/";//"./index/improve/"; //"./index/movie/"; // 存放索引的文件夹
	public CreateIndexOfDb() {
		// 建立数据库连接
		this.conn = DBConnection.getDBConnection();
	}
	// 建立索引
	@SuppressWarnings("deprecation")
	public static void CreateIndex() throws IOException, SQLException {
		// 建立数据库连接句柄
		conn = conn == null ? DBConnection.getDBConnection() : conn;
		// 建立Directory
		Directory directory = getDirectory(INDEXDIR);		
		// 建立indexWriter配置 引入中文的分词包
		IndexWriterConfig iwc = new IndexWriterConfig(new CJKAnalyzer());		
		// 建立 IndexWriter
		IndexWriter writer = new IndexWriter(directory, iwc);
		// 查询的sql语句
		String sql = "SELECT count_num, score, detail_url, play_url, img_src, movie_name, movie_detail, inq  FROM movietop250 ";
		// 获取数据库表中的记录
		ResultSet ResultSet = getResult(sql);
		// 读取ResultSet并添加索引
		while(ResultSet.next()) {
			// 实例化一个document
			Document doc = new Document();
			//doc.add(new StringField("count_num", ResultSet.getString("count_num"), Field.Store.YES));
			// 添加count_num 不被分析、不被索引、需要存储 
			doc.add(new Field("count_num", ResultSet.getString("count_num"), Field.Store.YES, Field.Index.NO));
			// 添加score     不被分析、需要索引、需要存储
			doc.add(new Field("score", ResultSet.getString("score"), Field.Store.YES, Field.Index.ANALYZED));
			// 添加detail_url    不被分析、不被索引、需要存储
			doc.add(new Field("detail_url", ResultSet.getString("detail_url"), Field.Store.YES, Field.Index.NO));
			// 添加 play_url 播放连接        不被分析、不被索引、需要存储
			String play_url = ResultSet.getString("play_url");
			doc.add(new Field("play_url", play_url.equals("") ? "" : play_url, Field.Store.YES, Field.Index.NO));
			// 添加movie_name 电影名         需要分析、需要索引、需要存储
			doc.add(new Field("movie_name", ResultSet.getString("movie_name"), Field.Store.YES, Field.Index.ANALYZED));
			// 添加movie_detail 电影详情        需要分析、需要索引、需要存储
			doc.add(new Field("movie_detail", ResultSet.getString("movie_detail"), Field.Store.YES, Field.Index.ANALYZED));
			// 添加inq 电影经典台词            不需要分析、不需要索引、需要存储
			doc.add(new Field("inq", ResultSet.getString("inq"), Field.Store.YES, Field.Index.ANALYZED));
			// 添加img_src 图片连接             不被分析、不需要索引、需要存储
			doc.add(new Field("img_src", ResultSet.getString("img_src"), Field.Store.YES, Field.Index.NO));
			// movie_detail进一步分析
			SplitDetail.str = ResultSet.getString("movie_detail");
			// director 导演                                                                  需要分析、需要索引、需要存储
			doc.add(new Field("director", SplitDetail.findDirector(), Field.Store.YES, Field.Index.ANALYZED));
			// actor 演员						     需要分析、需要索引、需要存储
			doc.add(new Field("actor", SplitDetail.findActor(), Field.Store.YES, Field.Index.ANALYZED));
			// year 上映年份						     不被分析、需要索引、需要存储
			doc.add(new Field("year", SplitDetail.findYear(), Field.Store.YES, Field.Index.ANALYZED));
			// type 类型                                                                              需要分析、需要索引、需要存储
			doc.add(new Field("type", SplitDetail.findType(), Field.Store.YES, Field.Index.ANALYZED));
			// country 国别                                                                     需要分析、需要索引、需要存储      
			doc.add(new Field("country", SplitDetail.findCountry(), Field.Store.YES, Field.Index.ANALYZED));
			// 添加到索引中
			writer.addDocument(doc);
		}
		// 关闭writer
		if(writer != null)
			writer.close();
		// 关闭数据集
		ResultSet.close();
		// 关闭数据库
		conn.close();		
	}
	/**
	 * 返回查询的记录
	 * @param sql
	 * @return
	 */
	public static ResultSet getResult(String sql) {
		conn = conn == null ? DBConnection.getDBConnection() : conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    /**
     * 得到索引磁盘存储器
     * @param 索引文件存储位置
     * @return
     */
    public static Directory getDirectory(String indexDir){
        Directory directory=null;
        try {
             directory= FSDirectory.open(Paths.get(indexDir));
        }catch (Exception e){
            e.printStackTrace();
        }
        return  directory;
    }
    /**
     * 得到默认分词器
     * @return
     */
    public static Analyzer getAnalyzer(){
       return new CJKAnalyzer();
    }
    /**
     * 获取读索引实体，并打印读到的索引信息
     * @return
     */
    public  static IndexReader getIndexReader(){
        IndexReader reader=null;
        try {
            reader= DirectoryReader.open(getDirectory(INDEXDIR));
            //通过reader可以有效的获取到文档的数量
            System.out.println("当前存储的文档数：:"+reader.numDocs());
            System.out.println("当前存储的文档数，包含回收站的文档：:"+reader.maxDoc());
            System.out.println("回收站的文档数:"+reader.numDeletedDocs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }
}
