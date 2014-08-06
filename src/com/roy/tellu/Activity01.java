package com.roy.tellu;

import com.roy.tellu.util.F;

import object.JsonObject;
import utils.StringUtil;
import activity.PActivity;
import adapter.DataAdapter;
import adapter.DataValue;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import data.I;

public class Activity01 extends PActivity {
	private DataAdapter da,groupDa;
	private ImageButton leftbtn,rightbtn;
	private TextView headname;
	private ListView listview,groupListview;
	private LinearLayout head;
	
	private PopupWindow  popWindow;
	private int x,y;
	
	@Override
	protected void init() throws Exception {
		this.headname.setText("最新");
		this.head.setOnClickListener(this);
		this.rightbtn.setOnClickListener(this);
		this.leftbtn.setOnClickListener(this);
		
		this.listview.setOnScrollListener(this);
		this.listview.setOnItemClickListener(this);
		newList();
	}
	
	private void newList() throws Exception{
		if(this.listview.getAdapter()==null){
			initList();
		}else{
			this.da.setDataSource(I.GET_NEW, new String[]{}, DataAdapter.HTTP);
			this.da.firstPage();
		}
	}
	
	private void hotList() throws Exception{
		if(this.listview.getAdapter()==null){
			initList();
		}else{
			this.da.setDataSource(I.GET_HOT, new String[]{}, DataAdapter.HTTP);
			this.da.firstPage();
		}
	}
	
	private void initList() throws Exception{
		this.da = new DataAdapter(this)
		.setLayout(R.layout.activity01_item)
		.setDataSource(I.GET_NEW, new String[]{}, DataAdapter.HTTP)
		.setUIMapping(new String[] {"sex","title","nickname"},new int[] { R.id.activity01_seximg,R.id.activity01_titletext,R.id.activity01_nickname})
		.setDataValue(new DataValue(){
			@Override
			public Object getValue(String columnName, String columnValue,JsonObject obj) {
				if("sex".equals(columnName)){
					if("m".equals(columnValue))
						return R.drawable.male;
					else if("f".equals(columnValue))
						return R.drawable.female;
				}
				else if("title".equals(columnName)){
					if(columnValue.length()>15){
						return columnValue.subSequence(0,15)+"...";
					}else{
						return columnValue;
					}
				}
				else if("second".equals(columnName)){
					return F.toMin(StringUtil.toInt(columnValue));
				}
				return null;
			}
			
		});
		this.listview.setAdapter(da.nextPage());
	}
	
	@Override
	protected void itemClick(AdapterView<?> parent, View view, int position,
			long id) throws Exception {
		//speaking button
		if(parent.getId()==this.listview.getId()){
			JsonObject json = this.da.getItem(position);
			/*this.addParam("fileUrl",json.get("fileUrl"));
			this.addParam("sec",json.get("second"));
			this.addParam("sex",json.get("sex"));
			this.addParam("fileId", json.get("fileId"));
			this.addParam("subjectId", json.get("subjectId"));
			this.addParam("nickname", json.get("nickname"));
			this.addParam("title", json.get("title"));*/
			this.addParam(json);
			this.to(Activity03.class);
		}
		//菜单
		else if(parent.getId()==this.groupListview.getId()){
			this.popWindow.dismiss();
			JsonObject obj = this.groupDa.getItem(position);
			String newTitle = obj.get("group_name");
			if(this.headname.getText().toString().trim().equals(newTitle.trim())){
				return;
			}
			if("最新".equals(newTitle.trim())){
				newList();
			}
			else if("最热".equals(newTitle.trim())){
				hotList();
			}
			this.headname.setText(newTitle.trim());
			
		}
	}

	@Override
	protected void click(View view) throws Exception {
		if(view.getId()==this.rightbtn.getId()){
			this.to(Activity02.class);
		}
		else if(view.getId()==this.leftbtn.getId()){
			this.to(Activity04.class);
		}
		else if(view.getId()==this.head.getId()){
			if(this.popWindow==null || !this.popWindow.isShowing())
				openPop(view);
		}
	}

	/*private void closePop() {
		this.popWindow.dismiss();
		this.headimg.setImageDrawable(this.getResources().getDrawable(R.drawable.down));
	}*/

	private void openPop(View v) throws Exception {
		if(this.popWindow==null){
			View view = this.getLayoutInflater().inflate(R.layout.activity01_group, null);
			
			groupListview = (ListView)view.findViewById(R.id.lvGroup);
			this.groupDa = new DataAdapter(this).setLayout(R.layout.activity01_group_item)
					.setDataSource("select group_name from tab_group", new String[]{}, DataAdapter.SQL)
					.setUIMapping(new String[]{"group_name"}, new int[]{R.id.groupItem});
			groupListview.setAdapter(this.groupDa.nextPage());
			groupListview.setOnItemClickListener(this);
			
			this.popWindow = new PopupWindow(view,150,150);
			
			this.x = (this.head.getWidth()-this.popWindow.getWidth())/2;

			this.popWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		//this.popWindow.setOutsideTouchable(true);  
		this.popWindow.setFocusable(true);
		this.popWindow.showAsDropDown(v, this.x,this.y);
		//this.headimg.setImageDrawable(this.getResources().getDrawable(R.drawable.up));
	}

	@Override
	protected int getLayout() {
		return R.layout.activity01;
	}

	@Override
	protected String getDomain() {
		return "activity01_";
	}
}
