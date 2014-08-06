package com.roy.tellu;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import object.JsonObject;
import utils.FileUtil;
import utils.StringUtil;
import activity.PActivity;
import adapter.DataAdapter;
import adapter.ViewBind;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roy.tellu.util.F;

import data.Data;
import data.I;

public class Activity05 extends PActivity {
	private DataAdapter dataAdapter;
	private ImageView speackbtn;
	private Button voice,editsend;
	private EditText edit;
	private LinearLayout editspace;
	private ListView listView;
	private boolean isEdit = true;
	
	private String subjectId;
	private String filePath;
	private long s,e;

	@Override
	protected void init() throws Exception {
		this.title.setText("ÆÀÂÛ");
		this.subjectId = this.getParam("subjectId");
		this.speackbtn.setOnClickListener(this);
		this.editsend.setOnClickListener(this);
		this.voice.setOnTouchListener(this);
		this.listView.setOnScrollListener(this);
		
		this.dataAdapter = new DataAdapter(this);
		this.dataAdapter.setLayout(R.layout.activity05_item);
		this.dataAdapter.setUIMapping(new String[]{"nickname","createDate","text"}, new int[]{R.id.a5_nickname,R.id.a5_date,R.id.a5_text});
		this.dataAdapter.setDataSource(I.GET_REPLY, new String[]{this.subjectId}, DataAdapter.HTTP);
		this.dataAdapter.setViewbind(new ViewBind(){
			@Override
			public boolean bind(final JsonObject json, View parentView,
					String columnName) throws Exception {
				if("text".equals(columnName)){
					String isFile = json.get("isFile");
					if("true".equals(isFile)){
						parentView.findViewById(R.id.a5_text).setVisibility(View.INVISIBLE);
						parentView.findViewById(R.id.a5_voice).setVisibility(View.VISIBLE);
						((TextView)parentView.findViewById(R.id.a5_voice_second)).setText(F.toMin(StringUtil.toInt(json.get("second"))));
						parentView.findViewById(R.id.a5_voice_second).setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View arg0) {
								try {
									onPlay(true, json.get("fileUrl"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
						return true;
					}
				}
				return false;
			}
			
		});
		this.listView.setAdapter(this.dataAdapter);
		this.dataAdapter.firstPage();
		this.closeKeyborad();
	}

	@Override
	protected void touch(View view, MotionEvent event) throws Exception {
		if(view.getId()==this.voice.getId()){
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				filePath = FileUtil.getNewFilePath(Data.AMR);
				this.s = System.currentTimeMillis();
				this.onRecording(true, filePath);
			}
			else if(event.getAction()==MotionEvent.ACTION_UP){
				this.onRecording(false, null);
				this.e = System.currentTimeMillis();
				sendVoice();
				this.dataAdapter.firstPage();
			}
		}
	}
	
	private void sendVoice() throws ClientProtocolException, IOException {
		JsonObject rst = this.http.getJson(I.REPLY_FILE,new String[]{F.getCustId(),this.getParam("subjectId"),this.filePath,(int)Math.floor((this.e-this.s)/1000)+""});
		if(rst.isSuccess()){
			this.edit.setText("");
		}else{
			this.alert("´íÎó",rst.errMsg());
		}
	}

	@Override
	protected void click(View view) throws Exception {
		if (view.getId() == this.leftbutton.getId()) {
			this.finish();
		} 
		else if(view.getId()==this.speackbtn.getId()){
			if(this.isEdit){
				this.speackbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.edit));
				this.editspace.setVisibility(View.INVISIBLE);
				this.voice.setVisibility(View.VISIBLE);
			}else{
				this.speackbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.voice));
				this.editspace.setVisibility(View.VISIBLE);
				this.voice.setVisibility(View.INVISIBLE);
			}
			this.isEdit = !this.isEdit;
		}
		else if(view.getId()==this.editsend.getId()){
			sendText();
			this.dataAdapter.firstPage();
		}
	}

	private void sendText() throws Exception {
		String content = this.edit.getText().toString();
		if(!StringUtil.isEmpty(content)){
			JsonObject rst = this.http.getJson(I.REPLY_TEXT,new String[]{F.getCustId(),this.getParam("subjectId"),content});
			if(rst.isSuccess()){
				this.edit.setText("");
				this.closeKeyborad();
				//this.da.firstPage();
			}else{
				this.alert("´íÎó",rst.errMsg());
			}
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.activity05;
	}

	@Override
	protected String getDomain() {
		return "a5_";
	}

}
