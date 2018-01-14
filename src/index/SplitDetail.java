package index;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用正则表达式解释电影的详情
 *  国别错误率: 0
 *  演员错误率: 7
 *	年份错误率: 0
 *	导演错误率: 7
 *	类型错误率: 0
 * @author Administrator
 *
 */
public class SplitDetail {
	public static String str = null;
	/**
	 * 找到导演
	 * @return
	 */
	public static String findDirector() {
		String director = "";
		String pattern = "(导演: )(.*)(?)(主演: )";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if(m.find()){
			// 去掉多余的字符
			director = m.group(2).replace("&nbsp;", "");
		}
		return director;
	}
	/**
	 * 找到演员
	 * @return
	 */
	public static String findActor() {
		String actor = "";
		//String pattern = "(主演: )(.*)(?)(\\d{4})";
		String pattern = "(主演: )(.*)(?)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if(m.find()){
			// 去掉多余的字符
			actor = m.group(2).
						replace("&nbsp;", "").
						replace("...", "").
						replace("/", "").
						replace("&amp;", "").
						replace("nbsp;", "")
						
						;
		}
		return actor;
	}
	/**
	 * 找到国别
	 * @return 
	 */
	public static String findCountry() {
		String country = "";
		String pattern = "(&nbsp;/&nbsp;)(.*)(?)(nbsp;/&nbsp;)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if(m.find()){
			// 去掉多余的字符
			country = m.group(2).
								replace("&nbsp;", "").
								replace("&amp", "").
								replace("&", "").
								replace(";", "")
								;
		}
		return country;
	}
	/**
	 * 找到年份
	 * @return
	 */
	public static String findYear() {
		String year = "";
		String pattern = "(.*)(\\d{4})";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if(m.find()){
			// 去掉多余的字符
			year = m.group(2);
		}
		return year;
	}
	/**
	 * 找到类型
	 * @return
	 */
	public static String findType() {
		String type = "";
		String pattern = "(nbsp;/&nbsp;)(.*)$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if(m.find()){
			// 去掉多余的字符
			type = m.group(2);//.replace("&amp;", "");//.replace("&nbsp;", "").replace("...", "").replace("/", "");
			int index = type.indexOf("/&nbsp");
			type = type.substring(index+7);
		}
		return type;
	}
}
