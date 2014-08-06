package data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.os.Environment;

public final class Data {
	//数据库名称
	public static String DB_NAME = "tellu.db";
	//文件根目录
	public static String FILE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tellu/";
	//音频文件格式
	public static String AMR = ".amr";
	
	//存放R.id中的变量映射
	public static Map<String,Object> cacheMap = new HashMap<String,Object>();
	
	//发生异常时的提示信息，在InitActivity中初始化
	public final static Properties errMsgs = new Properties();
	
	//公共变量存放控件，方便跨activity的参数传递
	public static Map<String,Object> PARAMS = new HashMap<String,Object>();
}
