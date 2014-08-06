package object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ����ΪJSONObject�Ĵ����࣬ר�Ž������µ�Json��ʽ��
 * {state:"true/false",data:...,errMsg:"..."}
 * �ṩ�˳���json�����������������ܿ������ж���
 * @author Administrator
 *
 */
public class JsonObject{
	private static String TRUE = "true";
	private static String STATE = "state";
	private static String DATA = "data";
	private static String ERRMSG = "errMsg";
	
	private JSONObject json;
	
	/**
	 * ����JSONObject.getString(..)����
	 * ��NULLֵ���˴���
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public String get(String key) throws JSONException{
		if(json.has(key)){
			String s = json.getString(key);
			return s==null?"":s;
		}else{
			return "";
		}
	}
	
	/**
	 * �����������Ƿ�ɹ�
	 * @return
	 */
	public boolean isSuccess(){
		if(json.has(STATE)){
			try {
				return TRUE.equals(get(STATE));
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * ��ȡ���������صĴ�����Ϣ
	 * @return
	 */
	public String errMsg(){
		if(json.has(ERRMSG)){
			try {
				return this.get(ERRMSG);
			} catch (JSONException e) {
				e.printStackTrace();
				return "";
			}
		}else{
			return "";
		}
	}
	
	/**
	 * ��ȡ��������
	 * @return
	 */
	public JsonObject getDataObject(){
		if(json.has(DATA)){
			try {
				JSONObject obj = json.getJSONObject(DATA);
				if(obj==null) return null;
				return new JsonObject(obj);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public String getDataString(){
		if(json.has(DATA)){
			try {
				return this.get(DATA);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public List<JsonObject> getDataArray() throws JSONException{
		if(json.has(DATA)){
			JSONArray arry = json.getJSONArray(DATA);
			List<JsonObject> objs = new ArrayList<JsonObject>();
			for(int i=0;i<arry.length();i++){
				objs.add(new JsonObject(arry.getJSONObject(i)));
			}
			return objs;
		}
		return null;
	}
	
	public JSONObject prototype(){
		return this.json;
	}
	
	public JsonObject(String str) throws JSONException{
		json = new JSONObject(str);
	}
	
	public JsonObject(JSONObject obj){
		json = obj;
	}
	
	private JsonObject(){
		
	}
}
