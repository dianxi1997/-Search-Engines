package Reptile;

import org.htmlparser.filters.*;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Grap grap = new Grap("http://www.rwqzcq.com");
		//String content = grap.getPage();
//		String contentWithHttpClient = grap.getPageWithHttpClient();
//		HashMap<String, String> linkHash = grap.getHref();
//		Set<String> keys = linkHash.keySet();
//		for(String key : keys) {
//			grap.echo(linkHash.get(key));
//		}
		//FileOperation file = new FileOperation();
		//file.filePutContents(contentWithHttpClient, "./grap1.html");
        Crawler myCrawler = new Crawler(15); // 6张网页 
        myCrawler.crawling(new String[]{"https://movie.douban.com/top250"});
	}

}
