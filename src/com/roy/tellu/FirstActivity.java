package com.roy.tellu;

import object.JsonObject;
import utils.SystemUtil;
import activity.InitActivity;
import data.I;

public class FirstActivity extends InitActivity{

	@Override
	protected void init() {
		super.init();
		
		String str = this.baseDao.getStr("select count(1) from tab_cust");
		if("1".equals(str)){
			this.to(Activity01.class);
			return;
		}
		try {
			JsonObject jsonObject = this.http.getJson(I.INFO,new String[]{SystemUtil.getIMEI(this)});
			if(jsonObject.isSuccess()){
				JsonObject json = jsonObject.getDataObject();
				if(json!=null){
					this.baseDao.execute("insert into tab_cust(cust_id,nickname,sex) values(?,?,?)",new String[]{json.get("custId"),json.get("nickname"),json.get("sex")});
					this.to(Activity01.class);
					return;
				}else{
					this.to(Activity00.class);
					return;
				}
			}else{
				this.alert("´íÎó",jsonObject.errMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
