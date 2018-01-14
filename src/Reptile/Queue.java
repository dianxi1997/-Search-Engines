package Reptile;

import java.util.LinkedList;
/**
 * 队列类
 * @author Administrator
 *
 */
public class Queue {
	private LinkedList queueList = new LinkedList();
	/**
	 * 入队
	 * @param obj
	 */
	public void enQueue(Object obj) {
		queueList.addLast(obj);
	}
	/**
	 * 出队
	 * @return
	 */
	public Object deQueue() {
		return queueList.removeFirst();
	}
	/**
	 * 判断队空
	 * @return
	 */
	public boolean isQueueEmpty() {
		return queueList.isEmpty();
	}
	/**
	 * 判断队列是否包含某个元素
	 */
	public boolean contains(Object obj) {
		return queueList.contains(obj);
	}
}
