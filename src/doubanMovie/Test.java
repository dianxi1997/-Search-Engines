package doubanMovie;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.swing.text.Document;
import javax.xml.crypto.dsig.spec.XPathType.Filter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import Reptile.*;
import db.DBManager;
import db.DataTable;

import org.jcp.*;

public class Test {
	
	public static void main(String[] args) {
		try {
			Test.main2();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 爬取豆瓣网的100条高评分电影信息，并从盘多多网站上找到他们各自的播放链接
	 * 电影的信息存到XML文件中
	 * 涉及到的字段有 电影名称 电影图片链接 电影详情链接 电影主演 评分 评价人数 经典台词 百度云盘链接
	 * @param args
	 * @throws ParserException
	 */
	public static void main2() throws ParserException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // 创建DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 创建Document
            org.w3c.dom.Document document = builder.newDocument();
            // 设置XML声明中standalone为yes，即没有dtd和schema作为该XML的说明文档，且不显示该属性
             document.setXmlStandalone(true);             
            // 创建根节点
            org.w3c.dom.Element root = ((org.w3c.dom.Document) document).createElement("root");
            // 遍历电影类并生成xml文件
            List<Map<String, String>> movieList = Test.getDouban();
    		for (int i = 0; i < movieList.size(); i++) {
    			/* 获得每场电影信息 */
    			Map<String, String> temp = movieList.get(i);
    			/* 创建电影的节点 */
    			org.w3c.dom.Element movie = ((org.w3c.dom.Document) document).createElement("movie");
    			/* 遍历HashMap获得每个电影的详细信息 */
    	        Set<String> keys = temp.keySet();  
    	        for(String key : keys) {
    	        	/* 创建子节点并追加到movie节点中 */
    	            org.w3c.dom.Element name = ((org.w3c.dom.Document) document).createElement(key);
    	            ((org.w3c.dom.Node) name).setTextContent(temp.get(key));
    	            movie.appendChild(name);
    	        }
    	        /* 将这个电影作为一个子节点追加到root节点之下 */
    	        root.appendChild(movie);
    		}        
            // 将根节点添加到Document下
            ((org.w3c.dom.Node) document).appendChild(root);
            /* 生成XML文件 */
            // 创建TransformerFactory对象
            TransformerFactory tff = TransformerFactory.newInstance();
            // 创建Transformer对象
            Transformer tf = tff.newTransformer();
            // 设置输出数据时换行
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            // 使用Transformer的transform()方法将DOM树转换成XML
            tf.transform(new DOMSource((org.w3c.dom.Node) document), new StreamResult(new File("./movieThirdPart.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//		Top250 doubanTop = new Top250("https://movie.douban.com/top250?start=0");
//		String[] name = doubanTop.movieName(); // 电影名
//		String[] inq = doubanTop.inq(); // 电影经典台词
//		String[] CountNum = doubanTop.CountNum(); // 评价人数
//		String[] score = doubanTop.score(); // 评分
//		String[] imgSrc = doubanTop.imgSrc(); // 图片链接
//		String[] movieDetail = doubanTop.movieDetail(); // 电影详情
//		String[] getDbDetailUrl = doubanTop.getDbDetailUrl(); // 电影详情URL
//		
//		String[] name = douban.getDbDetailUrl();
		//System.out.println(getDbDetailUrl.length);
//		for(String n : name)
//			System.out.println(n);
		//System.out.println(detail.length);
		//System.out.println(Test.getBaiduyunDisk(""));
		//Grap grap = new Grap("http://www.panduoduo.net/r/34904949");
		//System.out.println(grap.getPageWithHttpClient());
		//Grap grap = new Grap("https://movie.douban.	com/top250");
		//String content = grap.getPageWithHttpClient();
		//Parser parser = Parser.createParser(content, "UTF-8");

		//HtmlPage page = new HtmlPage(parser);
		//parser.visitAllNodesWith(page);
		//NodeList nodelist = page.getBody();
		//NodeFilter filter = new TagNameFilter("DIV");
		
		//nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		//for(int i=0; i<nodelist.size();i++){
			//LinkTag link =  (LinkTag) nodelist.elementAt(i);
			//System.out.println(link.getAttribute("class"));
		//}
//		Parser parser = new Parser("https://movie.douban.com/top250");
//		parser.setEncoding("UTF-8");
		//AndFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "item"));
		//这样返回的list里面包含网页中的所有节点
//		AndFilter filter = new AndFilter( // 获取电影的详情连接
//						new TagNameFilter("A"),
//						new HasParentFilter(new AndFilter(
//								new TagNameFilter("div"),	
//								new HasAttributeFilter("class", "pic")								
//								
//						))
//						
//			);
//		AndFilter filter = new AndFilter(
//				new TagNameFilter("img"),
//				new HasParentFilter(new AndFilter(
//						new TagNameFilter("a"),
//						new HasParentFilter(new AndFilter(
//								new TagNameFilter("div"),
//								new HasAttributeFilter("class", "pic")
//							))
//					))
//				);
//		AndFilter filter = 	new AndFilter(
//				new AndFilter(
//						new TagNameFilter("P"),
//						new HasAttributeFilter("class", "")
//				),						
//				new HasParentFilter(new AndFilter(
//						new TagNameFilter("div"),	
//						new HasAttributeFilter("class", "bd")	
//						))
//			);
//		AndFilter filter = 
//				new AndFilter(
//						new TagNameFilter("span"),
//						new HasAttributeFilter("class", "rating_num")
//				);	
//		HasAttributeFilter spanFilter = new HasAttributeFilter("class");
//
//		AndFilter filter = 	new AndFilter( 
//				new AndFilter(
//						new TagNameFilter("SPAN"),
//						new HasSiblingFilter(
//								new AndFilter(
//										new TagNameFilter("SPAN"),
//										new HasAttributeFilter("property", "v:average")
//									)
//						)													
//				),						
//				new HasParentFilter(new AndFilter(
//						new TagNameFilter("div"),	
//						new HasAttributeFilter("class", "star")	
//						))
//			);
//		AndFilter filter = 	new AndFilter(
//					new TagNameFilter("DIV"),
//					new HasAttributeFilter("class", "star")						
//				);
//		AndFilter filter = new AndFilter(
//				new AndFilter(
//						new TagNameFilter("span"),
//						new  HasAttributeFilter("class", "title")
//						),				
//				new HasParentFilter(
//						new AndFilter(
//								new TagNameFilter("div"),
//								new HasAttributeFilter("class", "hd")
//								)
//						)
//				);
//		AndFilter filter = new AndFilter( 
//				new TagNameFilter("span"),						
//				new HasParentFilter(new AndFilter(
//					new TagNameFilter("div"),	
//					new HasAttributeFilter("class", "star")	
//				))
//			);
		/*
		 * org.htmlparser.nodes.TextNode cannot be cast to org.htmlparser.nodes.TagNode
		 * child.toHtml() 不显示
		 */
		  // String[] dbDetailUrls = new String[]{};

//		  // String[] dbDetailUrls = new String[list.size()];
//		   for(int i=0; i<list.size();i++){
//        	//
//			///if((i + 1) % 4 != 0) continue;
//        	TagNode tag = (TagNode)list.elementAt(i); // 返回一个node对象并转化成一个TagNode对象
//        	//String dbDetailUrl = tag.getAttribute("href"); // 获得属性
//        	String dbDetailUrl = tag.getAttribute("src"); // 获得属性
//        	System.out.println(dbDetailUrl);
//		   }
        	//org.htmlparser.Node last = tag.getLastChild();
        	//org.htmlparser.Node last = tag.getLastChild();
        	//NodeList child = tag.getChildren();
 //       	NodeList child = tag.getChildren();
        	//for(int m = 0; m < child.size(); m++) {
        		//TagNode l = (TagNode) list.elementAt(m); // 关键的关键
        		//System.out.println(tag);
        		//NodeList ch = l.getChildren();
        		//System.out.println(ch.size());
        		//for(int f = 0; f < ch.size(); f++) {
        			//org.htmlparser.Node p = ch.elementAt(7);
        			//System.out.println(p.toPlainTextString());	
        		//}
        		
        		//break;
//        	}
        	//TagNode l = (TagNode) list.elementAt(4);
        	//System.out.println(l.toPlainTextString());
        	//break;
        	//System.out.println();
        	//String dbDetailUrl = last.toPlainTextString(); // 获取标签中的内容
        	//System.out.println(dbDetailUrl);
        	//break;
        	//dbDetailUrls[i] = dbDetailUrl;
        	//org.htmlparser.Node child = tag.getFirstChild();
        	//System.out.println(child.toHtml());
        	//org.htmlparser.Node first = tag.getFirstChild();
        	//Node first = tag.getFirstChild(); // 子类不能向父类转化
        	//System.out.println(first.getText());
        	//org.htmlparser.Node first = node.getFirstChild();
        	//TagNode tag = new TagNode(); // 新建一个标签对象
        	//tag.setText(first.toHtml()); // 载入这个标签对象
        	//System.out.println(tag.getAttribute("href"));
        	//NodeList childNode = node.getChildren(); // 获取所有子节点
        	//for(int j = 0; j < childNode.size(); j++) {
        		//org.htmlparser.Node temp = childNode.elementAt(j);
        		//temp.
        		//System.out.println(temp.toHtml());
        	//}
        	//break;
        	//System.out.println(childNode.size());
        	//
//        	Parser parserTemp = Parser.createParser(node.toHtml(), "UTF-8");
//        	AndFilter tempFilter = new AndFilter(new TagNameFilter("div"), new  HasAttributeFilter("class", "hd"));
//        	NodeList tempList = parserTemp.parse(tempFilter);
//        	for(int j = 0; j < tempList.size(); j++) {
//        		org.htmlparser.Node tempNode = tempList.elementAt(j); // 关键的关键
//        		
//        	}
        	
       // }
    
	//}
	
	public static void withWholePart() {
		Parser parser;
		try {
			parser = new Parser("https://movie.douban.com/top250?start=100");
			parser.setEncoding("UTF-8");
			NodeFilter filter = new AndFilter(
						new TagNameFilter("div"),
						new HasAttributeFilter("class", "bd")
					);
		    NodeList list = parser.parse(filter);
		    for(int i = 1; i < list.size(); i++) {
		    	TagNode node = (TagNode) list.elementAt(i);
		    	
//		    	org.htmlparser.Node node = list.elementAt(i); // 关键的关键
		    	NodeList children =node.getChildren();;
		    	for(int j = 0; j < children.size(); j++) {
		    		org.htmlparser.Node item = children.elementAt(j); // 关键的关键

		    		
		    		//System.out.println(item);
		    		//break;
		    	}
		    	break;
		    }
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	
	/**
	 * Referer:http://www.panduoduo.net/s/name/%E7%9B%98%E5%A4%9A%E5%A4%9A
	   Upgrade-Insecure-Requests:1
	   User-Agent:Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36
	 * @param name
	 * @return
	 */
	public static String getBaiduyunDisk(String name) {
		String url = null;
		String orgName = name;
		try {
			name = URLEncoder.encode(name,"UTF-8"); // 对中文编码
			/* 发送带参数的get请求来获取网页内容 */
			Grap grap = new Grap("http://www.panduoduo.net/s/name/" + name);
			Map<String, String> headers = new HashMap<String, String>(); // 初始头信息
			headers.put("accept", "text/html");
			//headers.put("Accept-Encoding", "gzip"); // 可以接受压缩类型
			headers.put("connection", "Keep-Alive");
			headers.put("User-Agent", 
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
//			headers.put("user-agent",
//	                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			String content = grap.getPageWithHttpClient(headers);
//			System.out.println("第一次爬取的网页是" + content);
			/* 使用htmlParser来解析内容 */
			Parser parser = Parser.createParser(content, "utf-8"); // 实例化一个新的parser类
			NodeFilter filter = new AndFilter(
						new TagNameFilter("a"),
						new HasParentFilter(
								new TagNameFilter("h3")
								)
					);	
			//parser.reset();
			NodeList list = parser.parse(filter);
			if(list.size() == 0) {
				System.out.println(orgName + " 找不到电影列表页");
				System.out.println(content);
			} else {
				TagNode node = (TagNode) list.elementAt(0); // 找到第一个连接
				String href = node.getAttribute("href");
//				System.out.println("下一页是" + href);
				href = "http://www.panduoduo.net" + href; // 拼接下一个字符串 不是 +=
//				System.out.println("下一次请求的链接是" + href);
//				/* 发送下一次的http请求  可以递归*/
				grap = new Grap(href);
				content = grap.getPageWithHttpClient(); // 使用不同的页面防止出现乱码
//				System.out.println("第二次爬取的网页是" + content);
				parser = Parser.createParser(content, "utf-8"); // 重新构造HTMLparser
				filter = new AndFilter(
							new TagNameFilter("a"),
							new HasAttributeFilter("class", "dbutton2")
						);
				//parser.reset();
				list = parser.parse(filter);
//				System.out.println(content);
				if(list.size() == 0) {
					System.out.println(orgName + " 找不到云链接");
				} else {
					node = (TagNode) list.elementAt(0); // 找到第一个连接
					href = node.getAttribute("href");
					//System.out.println("找到的百度云链接是" + href);
					url = href;
				}
			}				
			//System.out.println(content);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	public static String getBaiduyunLink(String rootUrl, String name) throws MalformedURLException {
		String urlNameString = "";
		try {
			name = URLEncoder.encode(name,"UTF-8");
			urlNameString = rootUrl +  name;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URL realUrl = new URL(urlNameString);
		URLConnection connection;
        String result = "";
        BufferedReader in = null;
		try {
			connection = realUrl.openConnection();
	        // 设置通用的请求属性
	        connection.setRequestProperty("accept", "*/*");
	        connection.setRequestProperty("connection", "Keep-Alive");
	        connection.setRequestProperty("user-agent",
	                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	        // 建立实际的连接
	        connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
           // System.out.println(result);
           
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	public static List<Map<String, String>> getDouban() {
		Top250 doubanTop = null;
		int count = 0;
		/* 电影集合类 */
		List<Map<String, String>> movieList = new LinkedList<Map<String, String>>();
		for(int flag = 7; flag < 10; flag++) {
//			/* 单个电影结合 */
//			Map<String, String> item = new HashMap<String, String>();
			String start = "start" + "=" + (25*flag);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 每个一秒爬一次
			doubanTop = new Top250("https://movie.douban.com/top250?" + start);
//			String[] detail = doubanTop.getText(new AndFilter(
//										new TagNameFilter("A"),
//										new HasParentFilter(
//												new AndFilter(
//														new TagNameFilter("div"),	
//														new HasAttributeFilter("class", "pic")								
//														)
//												)	
//										), "href");
			String[] name = doubanTop.movieName(); // 电影名
			String[] inq = doubanTop.inq(); // 电影经典台词
			String[] CountNum = doubanTop.CountNum(); // 评价人数
			String[] score = doubanTop.score(); // 评分
			String[] imgSrc = doubanTop.imgSrc(); // 图片链接
			String[] movieDetail = doubanTop.movieDetail(); // 电影详情
			String[] getDbDetailUrl = doubanTop.getDbDetailUrl(); // 电影详情URL
			if(
					name.length != 25 || 
					inq.length != 25 ||
					CountNum.length != 25 ||
					score.length != 25 ||
					imgSrc.length != 25 ||
					movieDetail.length != 25 ||
					getDbDetailUrl.length != 25
			   ) {
				System.out.println("出错");
				System.out.println("name" + name.length);
				System.out.println("inq" + inq.length);
				System.out.println("CountNum" + CountNum.length);
				System.out.println("score" + score.length);
				System.out.println("imgSrc" + imgSrc.length);
				System.out.println("getDbDetailUrl" + getDbDetailUrl.length);
				break; // 跳出循环
			} else {
				for(int i = 0; i < 25; i++) {
//					System.out.println("name" + name[i]);
//					System.out.println("inq" + inq[i]);
//					System.out.println("CountNum" + CountNum[i]);
//					System.out.println("score" + score[i]);
//					System.out.println("imgSrc" + imgSrc[i]);
//					System.out.println("getDbDetailUrl" + getDbDetailUrl[i]);
					/* 单个电影结合 */
					Map<String, String> item = new HashMap<String, String>();
					item.put("movie_name", name[i]);
					item.put("inq", inq[i]);
					item.put("count_num", CountNum[i]);
					item.put("score", score[i]);
					item.put("img_src", imgSrc[i]);
					item.put("movie_detail", movieDetail[i]);
					item.put("detail_url", getDbDetailUrl[i]);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String bdUrl = Test.getBaiduyunDisk(name[i]) == null ? "" : Test.getBaiduyunDisk(name[i]);
//					System.out.println("百度云链接" + bdUrl);
					item.put("play_url", bdUrl);
					movieList.add(item); // 增加到整个电影链表中
					
				 // 清空HashMap然后重现加入
				}				
			}		
		}
		return movieList;
	}

	    public static void createXMLByDOM(File dest) {
	        // 创建DocumentBuilderFactory
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        try {

	            // 创建DocumentBuilder
	            DocumentBuilder builder = factory.newDocumentBuilder();

	            // 创建Document
	            org.w3c.dom.Document document = builder.newDocument();

	            // 设置XML声明中standalone为yes，即没有dtd和schema作为该XML的说明文档，且不显示该属性
	            // document.setXmlStandalone(true);

	            // 创建根节点
	            org.w3c.dom.Element bookstore = ((org.w3c.dom.Document) document).createElement("bookstore");

	            // 创建子节点，并设置属性
	            org.w3c.dom.Element book = ((org.w3c.dom.Document) document).createElement("book");
	            book.setAttribute("id", "1");

	            // 为book添加子节点
	            org.w3c.dom.Element name = ((org.w3c.dom.Document) document).createElement("name");
	            ((org.w3c.dom.Node) name).setTextContent("安徒生童话");
	            book.appendChild(name);
	            org.w3c.dom.Element author = ((org.w3c.dom.Document) document).createElement("author");
	            author.setTextContent("安徒生");
	            book.appendChild(author);
	            org.w3c.dom.Element price = ((org.w3c.dom.Document) document).createElement("price");
	            ((org.w3c.dom.Node) price).setTextContent("49");
	            book.appendChild((org.w3c.dom.Node) price);

	            // 为根节点添加子节点
	            bookstore.appendChild(book);

	            // 将根节点添加到Document下
	            ((org.w3c.dom.Node) document).appendChild(bookstore);

	            /*
	             * 下面开始实现： 生成XML文件
	             */

	            // 创建TransformerFactory对象
	            TransformerFactory tff = TransformerFactory.newInstance();

	            // 创建Transformer对象
	            Transformer tf = tff.newTransformer();

	            // 设置输出数据时换行
	            tf.setOutputProperty(OutputKeys.INDENT, "yes");

	            // 使用Transformer的transform()方法将DOM树转换成XML
	            tf.transform(new DOMSource((org.w3c.dom.Node) document), new StreamResult(dest));

	        } catch (ParserConfigurationException e) {
	            e.printStackTrace();
	        } catch (TransformerConfigurationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (TransformerException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	    }

}
