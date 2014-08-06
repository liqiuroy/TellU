package com.roy.tellu.util;

import dao.BaseDao;

public class F {
	public static String getSex(){
		BaseDao dao = new BaseDao();
		String sex = dao.getStr("select sex from tab_cust limit 1");
		return sex;
	}
	
	public static String getCustId(){
		BaseDao dao = new BaseDao();
		String custId = dao.getStr("select cust_id from tab_cust limit 1");
		return custId;
	}
	
	public static String[] fmtSec(int second){
		int min = second/60;
		int sec = second%60;
		return new String[]{min<10?"0"+min:min+"",sec<10?"0"+sec:sec+""};
	}
	
	public static String toMin(int second){
		String[] ss = fmtSec(second);
		return ss[0]+":"+ss[1];
	}
}
