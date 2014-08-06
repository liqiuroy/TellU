package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import object.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONUtil {
	public static JsonObject toJsonObject(String str) throws JSONException{
		return new JsonObject(str);
	}
	
	public static JSONObject toJson(String str) throws JSONException{
		return new JSONObject(str);
	}
	
	public static JSONArray toJsonArray(String str) throws JSONException{
		return new JSONArray(str);
	}
	
	public static List<Map<String,Object>> array2Maps(JSONArray arry) throws JSONException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<arry.length();i++){
			Map<String,Object> map = new HashMap<String,Object>();
			JSONObject obj = arry.getJSONObject(i);
			Iterator it = obj.keys();
			while(it.hasNext()){
				String key = (String)it.next();
				map.put(key, obj.getString(key));
			}
		}
		return list;
	}
}
