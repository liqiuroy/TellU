package data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.os.Environment;

public final class Data {
	//���ݿ�����
	public static String DB_NAME = "tellu.db";
	//�ļ���Ŀ¼
	public static String FILE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tellu/";
	//��Ƶ�ļ���ʽ
	public static String AMR = ".amr";
	
	//���R.id�еı���ӳ��
	public static Map<String,Object> cacheMap = new HashMap<String,Object>();
	
	//�����쳣ʱ����ʾ��Ϣ����InitActivity�г�ʼ��
	public final static Properties errMsgs = new Properties();
	
	//����������ſؼ��������activity�Ĳ�������
	public static Map<String,Object> PARAMS = new HashMap<String,Object>();
}
