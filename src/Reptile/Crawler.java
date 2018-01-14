package Reptile;

import java.util.HashMap;
import java.util.Set;

public class Crawler {
	private int num; // 爬取的条数
	/**
	 * 初始化unvisited队列
	 * @param seeds
	 */
	private void initSeeds(String[] seeds) {
		for(int i=0; i < seeds.length; i++)
			LinkQueue.addUnvisitedUrl(seeds[i]);
	}
	/**
	 * 爬取的网页的数量
	 * @param i
	 */
	public Crawler(int i) {
		this.num = i;
	}
	/**
	 * 爬取
	 * @param seeds
	 */
	public void crawling(String[] seeds) {
        //初始化URL队列
        initSeeds(seeds);
        int count=0;
        //循环条件:待抓取的链接不为空抓取的网页最多num条
        while (!LinkQueue.isUnVisitedUrlIsEmpty() && LinkQueue.getVisitedUrlNum() <= num) {

            System.out.println("count:"+(++count));
            //出队列
            String visitURL = (String) LinkQueue.unVisitedUrlDeQueue();	            
            LinkQueue.addVisitedUrl(visitURL);
            //爬取
            Grap grap = new Grap(visitURL);
            String content = grap.getPageWithHttpClient();
            // 存入文件中
            FileOperation.filePutContents(content, "grap"+count+".html");
            // 获取超链接
    		HashMap<String, String> linkHash = grap.getHref();
    		// 遍历超链接集合
    		Set<String> keys = linkHash.keySet();
    		// 未访问的入列
    		for(String key : keys) {
    			grap.echo(linkHash.get(key));
    			LinkQueue.addUnvisitedUrl(linkHash.get(key));
    		}

        }
	 }
}
