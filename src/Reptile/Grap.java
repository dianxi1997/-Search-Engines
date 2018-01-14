package Reptile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.*;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.util.ParserException;
@SuppressWarnings("deprecation")

/**
 * 网页爬取类
 * @author Administrator
 *
 */
public class Grap {
	private String url; // 目标URL
	private String content = ""; // 爬取下来的内容
	/*
	 * 初始化的url
	 * @param String url
	 */
	public Grap(String url) {
		this.url= url;
	}
	/**
	 * 爬取网页
	 * @return String
	 */
	public String getPage() {
		StringBuilder pageBuffer = new StringBuilder();
		try {
			URL pageURL = new URL(this.url); // 获得URL的引用，建立第一次连接
			BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // 打开连接，读入数据到管道中		
			String line;
			while((line = reader.readLine()) != null) { // 读取数据
				pageBuffer.append(line);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageBuffer.toString(); // 返回内容
	}
	/**
	 * 用httpClient来爬取网页内容
	 * @return
	 */
	@SuppressWarnings("resource")
	public String getPageWithHttpClient() {
		@SuppressWarnings({ "deprecation", "resource" })
		HttpClient httpclient = new DefaultHttpClient(); // 打开浏览器
		HttpGet httpget = new HttpGet(url); // 输入URL
		Map<String, String> headers = new HashMap<String, String>(); // 初始头信息
		headers.put("accept", "text/html;charset=utf-8");
		headers.put("connection", "Keep-Alive");
		headers.put("Accept-Encoding", "gzip"); 
		headers.put("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	    if (headers != null) {  // 发送头信息
	        Set<String> keys = headers.keySet();  
	        for (java.util.Iterator<String> i = keys.iterator(); i.hasNext();) {  
	            String key = (String) i.next();  
	            httpget.addHeader(key, headers.get(key));  
	        } 	        
	    } 
		HttpResponse response;
		try {
			response = httpclient.execute(httpget); // 按下回车
			response.getHeaders("charset");
			HttpEntity entity = response.getEntity(); // 获取响应体的主内容
			/* 检查gzip压缩 */
			 if (entity != null) {
                 org.apache.http.Header ceheader = entity.getContentEncoding();
                 if (ceheader != null) {
                     HeaderElement[] codecs = ceheader.getElements();
                     for (int i = 0; i < codecs.length; i++) {
                         if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                             response.setEntity(
                                     new GzipDecompressingEntity(response.getEntity())
                                );
                             break;
                         }
                     }
                 }
             }
			entity = response.getEntity();
			this.content = EntityUtils.toString(entity, "utf-8"); // 设置编码
			EntityUtils.consume(entity); // 关闭响应
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown(); // 关闭浏览器
		}
		return this.content;
	}
	public HashMap<String, String> getHref() {
		/**
		  	<a\b                                        #匹配a标签的开始
			[^>]+                                      #匹配a标签中href之前的内容
			\bhref="([^"]*)"                       #匹配href的值，并将匹配内容捕获到分组1当中
			[^>]*>                                    #匹配a标签中href之后的内容
			([\s\S]*?)                                  #匹配a标签的value，并捕获到分组2当中，?表示懒惰匹配
			</a>                                       #匹配a标签的结束
		 */
		HashMap<String, String> linkHash = new HashMap<String, String>(); // 存放标签的hash列表
		String regex = "<a\\b[^>]+\\bhref=\"([^\"]*)\"[^>]*>([\\s\\S]*?)</a>"; // 匹配a标签的正则表达式
		Pattern pattern = Pattern.compile(regex); // 模式字符串转化成正则模式
		if(this.content != "") { // 找到的内容不为空
			Matcher matcher = pattern.matcher(this.content); //匹配
	        while(matcher.find()){
	            String link = matcher.group(1).trim(); // 找到href里面的链接
	            String title = matcher.group(2).trim(); // 找到a标签里面的内容
	            if(!link.startsWith("http") && !link.startsWith("https")){ // 相对路径与绝对路径的匹配	               	
	                if(link.startsWith("/") || link.startsWith("?")) // 相对路径 或者带有参数
	                    link = this.url + link;
	                else if(link.startsWith("./"))
	                    link = this.url + link.substring(1);   
	                else {
	                	link = "";
	                }
	            }
	            if(!link.equals(""))
	            	linkHash.put(title, link); // 存入hash表中
	        }			
		}
        return linkHash;
	}
	public void getHrefWidthHtmlParser() {		
		if(this.content == "") {
			this.echo("没有内容可以爬取");
		} else {
			Parser parser = null;
			try {
				parser = new Parser(this.content);
				parser.setEncoding("utf-8");				
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	/**
	 * 发送请求带头信息
	 * @param headers
	 * @return
	 */
	public String getPageWithHttpClient(Map<String, String> headers) {
		@SuppressWarnings("resource")
		HttpClient httpclient = new DefaultHttpClient(); // 打开浏览器
		HttpGet httpGet = new HttpGet(url); // 输入URL
	    if (headers != null) {  // 发送头信息
	        Set<String> keys = headers.keySet();  
	        for (java.util.Iterator<String> i = keys.iterator(); i.hasNext();) {  
	            String key = (String) i.next();  
	            httpGet.addHeader(key, headers.get(key));  
	        } 	        
	    } 
	    HttpResponse response;
		try {
			response = httpclient.execute(httpGet); // 按下回车
			HttpEntity entity = response.getEntity(); // 获取响应体的主内容
			/* 11.8 设置编码 */
			String charset = null;
			if(entity.getContentType() != null) {
				HeaderElement values[] = entity.getContentType().getElements();
				if(values.length > 0) {
					NameValuePair param = values[0].getParameterByName("charset");
					if(param != null)
						charset = param.getValue(); // 获取编码
				}
			}
			charset = charset == null ? "UTF-8" : charset;	
			/* 检查gzip压缩 */
			 if (entity != null) {
                org.apache.http.Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                    new GzipDecompressingEntity(response.getEntity())
                               );
                            break;
                        }
                    }
                }
            }
			entity = response.getEntity();
			this.content = EntityUtils.toString(entity, charset); // 设置编码
			EntityUtils.consume(entity); // 关闭响应
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown(); // 关闭浏览器
		}
		return this.content;
	}
	public void echo(String str) {
		System.out.println(str);
	}
	
}
