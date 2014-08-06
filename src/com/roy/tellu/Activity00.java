package com.roy.tellu;

import object.JsonObject;
import utils.SystemUtil;
import activity.PActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import data.I;

public class Activity00 extends PActivity {
	private RadioGroup sex;
	private RadioButton radio_man,radio_woman;
	private Button button;
	
	private String s;
	@Override
	protected void init() throws Exception {
		this.button.setOnClickListener(this);
		this.sex.setOnCheckedChangeListener(this);
	}

	@Override
	protected void checkedChanged(RadioGroup group, int checkedId) {
		if(checkedId==this.radio_man.getId()){
			this.s = "m";
		}
		else if(checkedId==this.radio_woman.getId()){
			this.s = "f";
		}
	}

	@Override
	protected void click(View view) throws Exception {
		if(view.getId()==this.button.getId()){
			if(this.s==null){
				this.alert("提示","请选择您的性别");
				return;
			}
			JsonObject rst = this.http.getJson(I.REGIST,new String[]{SystemUtil.getIMEI(this),this.s});
			if(rst!=null && rst.isSuccess()){
				String custId = rst.getDataString();
				String count = this.baseDao.getStr("select count(1) from tab_cust");
				if("0".equals(count))
					this.baseDao.execute("insert into tab_cust(cust_id,sex) values(?,?)",new String[]{custId,this.s});
				this.to(Activity01.class);
			}else{
				throw new org.apache.http.client.ClientProtocolException("网络异常");
			}
			
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.activity00;
	}

	@Override
	protected String getDomain() {
		return "activity00_";
	}
}
