package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.json.JSONArray;

import index.SearchOfDb;

/**
 * Servlet implementation class MovieController
 */
public class MovieController extends HttpServlet {

	private static final long serialVersionUID = 1L;
    
	private static final String fuzzySimility = " ~0.5";

	private static final String  phraseFlag = "^";
	
	private static final String phraseSloop = " ~1";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * termQuery
	 * BooleanQuery    done 
	 * RangeQuery      done
	 * PrefixQuery     done
	 * PhraseQuery     新开一个页面写短语检索以^结尾
	 * FuzzyQuery      多字段中加上 work ~0.5 done
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		String action = request.getParameter("action");
		SearchOfDb search = new SearchOfDb();
		JSONArray jsonArray = null;
		/* 简单检索 */
		if(action.equals("getSimple")) {
			try {
				String value = request.getParameter("value");
				String term = request.getParameter("term");
				String lastValue = value;
				if(term != null) {
					// 排出短语检索的影响
					if(term.endsWith("^")) {
						int endIndex = term.lastIndexOf("^");
						term = term.substring(0, endIndex);
					}					
					// 前缀检索
					lastValue = term + "*"; 
					jsonArray = search.searchMovieBySimpleKeyOutOneField(request.getParameter("field"), lastValue);
				} else {
					// 短语检索
					lastValue = phraseCheck(value);
					jsonArray = search.searchMovieBySimpleKey(request.getParameter("field"), lastValue);
				}							
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		/* 高级检索 */
		if(action.equals("getComplex")) {
			// 总数量
			int total = Integer.parseInt(request.getParameter("txt_total"));
			
			// 单字段检索
			if(total == 1) {
				;
			} else {
				;
			}
			
			ArrayList<String> fields = new ArrayList<String>();
			ArrayList<String> values =   new ArrayList<String>();
			ArrayList<BooleanClause.Occur> relations =  new ArrayList<BooleanClause.Occur>();
			
			// 组内逻辑判断
			HashMap flag = new HashMap();
			flag.put("field_and", " AND ");
			flag.put("field_or", " OR ");
			flag.put("field_not", " NOT ");
			
			// 组间逻辑判断
			HashMap reFlag = new HashMap();
			reFlag.put("and", BooleanClause.Occur.MUST);
			reFlag.put("or", BooleanClause.Occur.SHOULD);
			reFlag.put("not", BooleanClause.Occur.MUST_NOT);
			
			relations.add(BooleanClause.Occur.MUST);
			// 遍历传进来的请求参数
			for(int i = 1; i <= total; i++) {
				// 有字段之间的逻辑关系存在
				if(i != 1) {
					relations.add( (Occur) reFlag.get(request.getParameter("txt_"+i+"_logic")));
					System.out.println("字段之间的逻辑关系为: " + reFlag.get(request.getParameter("txt_"+i+"_logic")));
				}
				// 第一个值
				String t1 = request.getParameter("txt_"+i+"_value1").trim();
				// 第二个值
				String t2 = request.getParameter("txt_"+i+"_value2").trim();
				// 值与值之间的关系
				String relation = (String) flag.get(request.getParameter("txt_"+i+"_relation"));
               	// 判断是模糊检索还是准确检索 测试的时候发现special前面少加了_
               	String fuzzyFlag = request.getParameter("txt_"+i+"_special");
				String value = "";
                // 只有一个字段有值的时候
				if(t1.equals("") || t2.equals("")) {
					// 字段全部为空
                	if(t1.equals("") && t2.equals("")) {
                		relations.remove(relations.size()-1);
                		continue;
                	}
                	if(!t1.equals("")) {
                		value = fuzzyCheck(t1, fuzzyFlag);
                	} else if(!t2.equals("")) {
                		value = fuzzyCheck(t2, fuzzyFlag);
                	}
                // 两个字段都有值的时候拼接成
                } else {
                	value = fuzzyCheck(t1, fuzzyFlag) + relation + fuzzyCheck(t2, fuzzyFlag);
                }
				// 检索的字段
				String field = request.getParameter("txt_"+i+"_field");
				fields.add(field);
				// 检索的值
				values.add(value);
			}
			// 处理区间查询
			String start = request.getParameter("year_from");
			String end = request.getParameter("year_to");
			String result = this.stringsCombine(start, end, "year");
			if(!result.equals("")) {
				fields.add("year");
				values.add(result);
				relations.add(BooleanClause.Occur.MUST);
			}
			try {
				String[] lastFields = fields.toArray(new String[fields.size()]);
				String[] lastValues = values.toArray(new String[values.size()]);
				BooleanClause.Occur[] lastRelations = relations.toArray(new BooleanClause.Occur[relations.size()]);
				for(int j = 0; j < fields.size(); j++) {
					System.out.println("检索字段:" + fields.get(j));
				}
				for(int k = 0; k < values.size(); k++) {
					System.out.println("        检索式:" + values.get(k));
				}
				for(int l = 0; l < relations.size(); l++) {
					System.out.println("                   逻辑:" + relations.get(l));
				}
				jsonArray = search.searchMovieByMultiFields(lastFields, lastValues,  lastRelations);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		PrintWriter out = response.getWriter();
		out.print(jsonArray.toString());
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}
	protected String stringsCombine(String t1, String t2, String flag) {
		String value = "";
        // 只有一个字段有值的时候
		if(t1.equals("") || t2.equals("")) {
			// 字段全部为空
        	if(t1.equals("") && t2.equals("")) {
        		return value;
        	}
        	if(!t1.equals("")) {
        		value = t1;
        	} else if(!t2.equals("")) {
        		value = t2;
        	}
        // 两个字段都有值的时候拼接成
        } else {
        	if(flag.equals("year")) {
        		value = 
        				Integer.parseInt(t1) > Integer.parseInt(t2)  ? 
        						"[" + t2 +" TO " + t1 + "]" : "[" + t1 +" TO " + t2 + "]";
        				
        	} else if(flag.equals("txt")) {
        		;
        	}
        }
		return value;
	}
	/**
	 * 判断模糊检索
	 * @param t
	 * @param flag
	 * @return
	 */
	protected String fuzzyCheck(String t, String flag) {
		return flag.equals("%") ? t + fuzzySimility : phraseCheck(t);
	}
	/**
	 * 判断短语检索
	 * @param t
	 * @return
	 */
	protected String phraseCheck(String t) {
		if(t.endsWith(phraseFlag)) {
			t = t.substring(0, t.lastIndexOf(phraseFlag));
			t = "\"" + t + "\"" + phraseSloop;
			System.out.println(t);
		}
		return t;		
	}

}
