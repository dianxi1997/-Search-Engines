package Reptile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件操作类
 * @author Administrator
 *
 */
public class FileOperation {
	private InputStream input = null;
	private OutputStream output = null;
	/**
	 * 把文本存入文件中
	 * @param content
	 * @param filename
	 * @return
	 */
	@SuppressWarnings("resource")
	public static void filePutContents(String content, String filename) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			bw.write(content); // 写入文件内容
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
