package utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static boolean isEmpty(String str){
		if(str==null || str.trim().length()==0)
			return true;
		else
			return false;
	}
	
	public static String uid(){
		return UUID.randomUUID().toString();
	}
	
	public static boolean isInt(String str){
		if(str==null) return false;
		Pattern p = Pattern.compile("^[0-9]*$",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(str);
	    return m.matches();
	}
	
	public static int toInt(String str){
		if(isInt(str)){
			return Integer.parseInt(str,10);
		}
		return 0;
	}
}
