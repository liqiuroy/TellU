package dao;

import java.util.ArrayList;
import java.util.List;

import object.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class BaseDao {
	public static SQLiteDatabase db;
	
	/**
	 * ��ȡ�����ַ���
	 * @param sql
	 * @return
	 */
	public String getStr(String sql){
		return this.getStr(sql,null);
	}
	
	/**
	 * ��ȡ�����ַ�����prepared��ʽ
	 * @param sql
	 * @return
	 */
	public String getStr(String sql,String[] params){
		List<String[]> list = this.getStrsList(sql, params);
		if(list.size()==0)
			return null;
		else
			return list.get(0)[0];
	}
	
	/**
	 * ִ��select���
	 * ÿһ�н��������װΪһ��String[]�������±��Ӧselect�ֶ�˳��
	 * @param sql
	 * @return
	 */
	public List<String[]> getStrsList(String sql){
		return getStrsList(sql,null);
	}
	
	/**
	 * ִ��select���,prepared��ʽ
	 * ÿһ�н��������װΪһ��String[]�������±��Ӧselect�ֶ�˳��
	 * @param sql
	 * @return
	 */
	public List<String[]> getStrsList(String sql,String[] params){
		List<String[]> list = new ArrayList<String[]>();
		Cursor c;
		if(params!=null)
			c = db.rawQuery(sql, params); 
		else
			c = db.rawQuery(sql,new String[]{}); 
		int count = c.getColumnCount();
		while(c.moveToNext()){
			String[] _strs = new String[count];
			for(int i=0;i<count;i++){
				_strs[i] = c.getString(i);
			}
			list.add(_strs);
		}
		c.close();
		return list.size()==0?null:list;
	}
	
	public List<JsonObject> getJsons(String sql) throws JSONException{
		return this.getJsons(sql,null);
	}
	
	public List<JsonObject> getJsons(String sql,String[] params) throws JSONException{
		List<JsonObject> list = new ArrayList<JsonObject>();
		Cursor c;
		if(params!=null)
			c = db.rawQuery(sql, params); 
		else
			c = db.rawQuery(sql,new String[]{});
		String[] names = c.getColumnNames();
		while(c.moveToNext()){
			JSONObject json = new JSONObject();
			for(String name : names){
				json.put(name,c.getString(c.getColumnIndex(name)));
			}
			list.add(new JsonObject(json));
		}
		return list.size()==0?null:list;
	}
	
	/**
	 * ִ��select֮��Ĳ���:update delete create drop ...
	 * prepared��ʽ
	 * @param sql
	 * @param params
	 */
	public void execute(String sql,String[] params){
		if(params!=null)
			db.execSQL(sql,params);
		else
			db.execSQL(sql);
	}
	
	/**
	 * ִ��select֮��Ĳ���:update delete create drop ...
	 * @param sql
	 * @param params
	 */
	public void execute(String sql){
		execute(sql,null);
	}
	
	public BaseDao(){
	}
}
