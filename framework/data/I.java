package data;

/**
 * 该类存放应用中所有的的接口地址，
 * 接口中的占位符：#s--字符串    #f--文件流
 * 接口格式：<提交方式:带占位符的url> 例如: POST:/someurl?paramName1=#s&paramName2=#f
 * 缺省提交方式为get 例如: GET:/someurl?paramName1=#s&paramName2=#s 或 /someurl?paramName1=#s&paramName2=#s
 * 
 * @author Administrator
 * 注意：get提交类型接口不能带有 #f 文件流型参数
 */
public final class I {
	/**
	 * 接口地址根目录
	 */
	private final static String ROOT = "http://192.168.1.103:8080/remote";
	public final static String INFO = "/tellu01.action?operate=info&imei=#s";
	public final static String SAVE_SUBJECT = "POST:/tellu01.action?operate=subject&content=#s&file=#f&custId=#s&title=#s&second=#s";
	public final static String REGIST = "/tellu01.action?operate=regist&imei=#s&sex=#s";
	public final static String GET_NEW = "/tellu01.action?operate=getNew";
	public final static String GET_HOT = "/tellu01.action?operate=getHot";
	public final static String GET_REPLY = "/tellu01.action?operate=getReply&subjectId=#s";
	public final static String REPLY_TEXT = "/tellu01.action?operate=reply&custId=#s&subjectId=#s&text=#s";
	public final static String REPLY_FILE = "POST:/tellu01.action?operate=reply&custId=#s&subjectId=#s&file=#f&second=#s";
	public final static String GOODBAD = "/tellu01.action?operate=goodbad&subjectId=#s&goodOrBad=#s";
	public final static String EMAIL_LIST = "/tellu01.action?operate=emails&custId=#s";
	public final static String EMAIL_DETAIL = "/tellu01.action?operate=emailDetail&emailUid=#s";
	public final static String SAVE_IMG = "POST:/tellu01.action?operate=saveImg&subjectId=#s&file=#f";
	public final static String GET_SUBJECT = "/tellu01.action?operate=getSubject&subjectId=#s";
	
	/**
	 * 获取http访问的真实Url
	 */
	public static String i(String url){
		return ROOT+url;
	}
}
