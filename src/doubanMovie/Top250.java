package doubanMovie;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.parserapplications.filterbuilder.Filter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import Reptile.*;
public class Top250 {
	private String rootURL = "https://movie.douban.com/top250"; // 根路径
	private int start = 0; // 起始位置
	private String content = ""; // 页面内容
	private Parser parser = null; // htmlparser
	public Top250() {
		this.initParser();
	}
	public Top250(String rootURL) {
		this.rootURL = rootURL;
		this.initParser();		
	}
	private void initParser() {
		try {
			this.parser = new Parser(this.rootURL);
			this.parser.setEncoding("UTF-8");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Top250 grap() {
		Grap grap = new Grap(this.rootURL); // 初始连接
		this.content = grap.getPageWithHttpClient(); // 获得内容		
		return this;
	}
	/**
	 * 获得标签内文本
	 * @param filter
	 * @return
	 */
	public String[] getText(NodeFilter filter) {
		String[] target = null;
		try {
			NodeList list = this.parser.parse(filter);
			if(list.size() == 0) return target;
			target = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	        	
	        	target[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return target;
	}
	/**
	 * 获得属性文本
	 * @param filter
	 * @param attr
	 * @return
	 */
	public String[] getText(NodeFilter filter, String attr) {
		String[] target = null;
		try {
			NodeList list = this.parser.parse(filter);
			if(list.size() == 0) return target;
			target = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.getAttribute(attr); // 获得属性	        	
	        	target[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return target;
	}
	/**
	 * 找到豆瓣电影详情页
	 * @return
	 */
	public String[] getDbDetailUrl() {
		String[] dbDetailUrls = null;
		try {
			AndFilter filter = new AndFilter(
									new TagNameFilter("A"),
									new HasParentFilter(
											new AndFilter(
													new TagNameFilter("div"),	
													new HasAttributeFilter("class", "pic")								
													)
											)	
									);
			this.parser.reset();
		    NodeList list = this.parser.parse(filter);
		    dbDetailUrls = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.getAttribute("href"); // 获得属性	        	
	        	dbDetailUrls[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return dbDetailUrls;
	}	
	/**
	 * 获取电影详情
	 * @return
	 */
	public String[] movieDetail() {
		/* 过滤器声明 */
		AndFilter filter = 	new AndFilter( 
						new AndFilter(
								new TagNameFilter("P"),
								new HasAttributeFilter("class", "")
						),						
						new HasParentFilter(new AndFilter(
								new TagNameFilter("div"),	
								new HasAttributeFilter("class", "bd")	
								))
					);
		String[] movieDetail = null;
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);			
			movieDetail = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	        	
	        	movieDetail[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movieDetail;		
	}
	/**
	 * 图像的URL
	 * @return
	 */
	public String[] imgSrc() {
		AndFilter filter = new AndFilter(
				new TagNameFilter("img"),
				new HasParentFilter(new AndFilter(
						new TagNameFilter("a"),
						new HasParentFilter(new AndFilter(
								new TagNameFilter("div"),
								new HasAttributeFilter("class", "pic")
							)
						)
					))
				);
		String[] img = null;
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);
			img = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// org.htmlparser.Node node = list.elementAt(i); // 关键的关键
				TagNode tag = (TagNode) list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
				String dbDetailUrl = tag.getAttribute("src");
				; // 获得标签内的内容
				img[i] = dbDetailUrl;
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return img;
	}
	/**
	 * 评分
	 * @return
	 */
	public String[] score() {
		AndFilter filter = 
				new AndFilter(
						new TagNameFilter("span"),
						new HasAttributeFilter("class", "rating_num")
				);	
		String[] score = null;
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);
			score = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	        	
	        	score[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return score;		
	}
	/**
	 * 评价的统计
	 * @return
	 */
	public String[] CountNum() {
		/**
		 * span 标签 
		 * 没有属性
		 * 父节点hi dic.star
		 */
		AndFilter filter = new AndFilter( 
				new TagNameFilter("span"),						
				new HasParentFilter(new AndFilter(
					new TagNameFilter("div"),	
					new HasAttributeFilter("class", "star")	
				))
			);
		String[] count = null;
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);
			count = new String[list.size()/4];
			int index = 0;
		    for(int i=0; i<list.size();i++){
	        	if((i + 1) % 4 != 0) continue; // 不是最后一个就不加
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	        	
	        	count[index++] = dbDetailUrl; // 先赋值 后增加
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return count;
	}
	/**
	 * 获取inq
	 * @return
	 */
	public String[] inq() {
		String[] inq = null;
		AndFilter filter = 	new AndFilter( 
				new TagNameFilter("SPAN"),
				new HasAttributeFilter("class", "inq")
				);
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);
			inq = new String[list.size()];
		    for(int i=0; i<list.size();i++){
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	        	
	        	inq[i] = dbDetailUrl;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return inq;			
	}
	/**
	 * 电影名称
	 * @return
	 */
	public String[] movieName() {
		String[] movieName = null;
		AndFilter filter = new AndFilter( 
				new TagNameFilter("span"),
				new  HasAttributeFilter("class", "title")				
					);
		try {
			this.parser.reset();
			NodeList list = this.parser.parse(filter);
			movieName = new String[25];
			int index = 0;
		    for(int i=0; i<list.size();i++){		    	
	        	//org.htmlparser.Node node = list.elementAt(i); // 关键的关键
	        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
	        	String dbDetailUrl = tag.toPlainTextString(); // 获得标签内的内容	
	        	if(tag.toPlainTextString().startsWith("&nbsp")) {  // 只取中文名字
	        		continue;
	        	} else {
	        		//movieName. = dbDetailUrl;
	        		movieName[index++] = dbDetailUrl;
	        	}	        	
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return movieName;
	}
}
