package index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import Reptile.FileOperation;

import org.apache.lucene.queryparser.classic.ParseException;

import com.mysql.jdbc.Connection;

import db.DBConnection;
import java.sql.*;

public class TestIndex {
	
	public static void main(String[] args) throws IOException, SQLException, ParseException {
		//CreateIndexOfDb.INDEXDIR = "./index/last1/";
		//CreateIndexOfDb.CreateIndex();
		//CreateIndexOfDb.TestSearchaer();
		SearchOfDb search = new SearchOfDb();
//		 
		String value = "自由";
		String field = "inq";
		 
		JSONArray jsonArray = search.searchMovieBySimpleKey(field, value);
		List list = jsonArray.toList(); // JSONObject转List
		for(int i = 0 ; i < list.size() ; i++) {
			 HashMap map =  (HashMap) list.get(i);
			 Iterator iter = map.entrySet().iterator();
			 while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				System.out.print(key.toString() + "-------");
				System.out.println(val.toString());
			 }
		}
		//FileOperation.filePutContents(jsonString, "./json/kehuan3.json");
	}

	public static void movieDetailFind() throws IOException, SQLException {
		String sql = "SELECT movie_name, movie_detail FROM movietop250";
		// 获取数据库表中的记录
		ResultSet ResultSet = CreateIndexOfDb.getResult(sql);
		HashMap<String, Integer> count = new HashMap();
		count.put("country", 0);
		count.put("director", 0);
		count.put("year", 0);
		count.put("type", 0);
		count.put("actor", 0);
		// 读取ResultSet并添加索引
		while(ResultSet.next()) {
			String str = ResultSet.getString("movie_detail");
			SplitDetail.str = str;
//			System.out.println(ResultSet.getString("movie_name"));	
//			System.out.println("    国别   " + SplitDetail.findCountry());
//			System.out.println("    导演  " + SplitDetail.findDirector());
//			System.out.println("    年份  " + SplitDetail.findYear());
//			System.out.println("    演员  " + SplitDetail.findActor());
//			System.out.println("    类型  " + SplitDetail.findType());
			if(SplitDetail.findCountry().equals("")) {
				int num = count.get("country");
				num += 1;
				count.put("country", num);
			}
			if(SplitDetail.findDirector().equals("")) {
				int num = count.get("director");
				num += 1;
				count.put("director", num);
			}
			if(SplitDetail.findYear().equals("")) {
				int num = count.get("year");
				num += 1;
				count.put("year", num);
			}
			if(SplitDetail.findActor().equals("")) {
				int num = count.get("actor");
				num += 1;
				count.put("actor", num);
			}
			if(SplitDetail.findType().equals("")) {
				int num = count.get("type");
				num += 1;
				count.put("type", num);
			}
		}
		System.out.println("国别错误率: " + count.get("country") );
		System.out.println("演员错误率: " + count.get("actor") );
		System.out.println("年份错误率: " + count.get("year") );
		System.out.println("导演错误率: " + count.get("director") );
		System.out.println("类型错误率: " + count.get("type") );
	}

}
