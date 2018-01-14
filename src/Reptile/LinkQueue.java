package Reptile;

import java.util.HashSet;
import java.util.Set;

public class LinkQueue {
	private static Set visitedUrl = new HashSet(); // visited队列
	private static Queue unVisitedUrl = new Queue(); // 没有访问的队列
	public static Queue getUnVisitedUrl() {
		return unVisitedUrl;
	}
	public static Set getVisitedUrl() {
		return visitedUrl;
	}
	/**
	 * 添加url到visited队列中
	 * @param url
	 */
	public static void addVisitedUrl(String url) {
		visitedUrl.add(url);
	}
	/**
	 * 删除visited队列中的某个元素
	 * @param url
	 */
	public static void removeVisitedUrl(String url) {
		visitedUrl.remove(url);
	}
	/**
	 * 保证每个url只能访问一次
	 * 已经访问的URL队列中不能包好url
	 * 因为已经出队列了所未访问的URL队列中也不能包含
	 * @param url
	 */
	public static void addUnvisitedUrl(String url) {
		if( url != null &&
			!url.trim().equals("") &&
			!visitedUrl.contains(url) &&
			!unVisitedUrl.contains(url)
		   )
			unVisitedUrl.enQueue(url);
	}
	/**
	 * 获取已经访问的url的数量
	 * @return
	 */
	public static int getVisitedUrlNum() {
		return visitedUrl.size();
	}
	/**
	 * 判断unVisited队列是否为空
	 */
	public static boolean isUnVisitedUrlIsEmpty() {
		return unVisitedUrl.isQueueEmpty();
	}
	//未访问的URL出队列
    public static Object unVisitedUrlDeQueue(){
        return unVisitedUrl.deQueue();
    }
}
