package data;

/**
 * ������Ӧ�������еĵĽӿڵ�ַ��
 * �ӿ��е�ռλ����#s--�ַ���    #f--�ļ���
 * �ӿڸ�ʽ��<�ύ��ʽ:��ռλ����url> ����: POST:/someurl?paramName1=#s&paramName2=#f
 * ȱʡ�ύ��ʽΪget ����: GET:/someurl?paramName1=#s&paramName2=#s �� /someurl?paramName1=#s&paramName2=#s
 * 
 * @author Administrator
 * ע�⣺get�ύ���ͽӿڲ��ܴ��� #f �ļ����Ͳ���
 */
public final class I {
	/**
	 * �ӿڵ�ַ��Ŀ¼
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
	 * ��ȡhttp���ʵ���ʵUrl
	 */
	public static String i(String url){
		return ROOT+url;
	}
}
