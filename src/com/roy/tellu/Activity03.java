package com.roy.tellu;

import java.io.IOException;
import java.net.URL;

import object.JsonObject;
import utils.StringUtil;
import activity.PActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import data.I;

public class Activity03 extends PActivity {
	private LinearLayout imgs;
	private TextView content,tlt,act;
	private ImageView voice;
	private JsonObject subject;
	private boolean isPlay = false;

	@Override
	protected void init() throws Exception {
		this.title.setText(this.getParam("title"));
		String subjectId = this.getParam("subjectId");
		JsonObject obj = this.http.getJson(I.GET_SUBJECT,new String[]{subjectId});
		if(obj.isSuccess()){
			this.subject = obj.getDataObject();
			this.addParam(this.subject);
		}else{
			this.alert("提示","网络不给力");
			return;
		}
		
		this.tlt.setText(this.subject.get("title"));
		this.act.setText(this.subject.get("nickname"));
		
		String imgUrls = this.subject.get("imgs");
		if(!StringUtil.isEmpty(imgUrls)){
			if(imgUrls.indexOf(",")>0){
				for(String s : imgUrls.split(",")){
					this.imgs.addView(this.createImg(this.subject.get("imgRoot")+s));
				}
			}else{
				this.imgs.addView(this.createImg(this.subject.get("imgRoot")+imgUrls));
			}
		}
		
		if(this.subject.get("type").equals("w")){
			this.content.setText(this.subject.get("content"));
		}else{
			this.voice.setVisibility(View.VISIBLE);
			this.voice.setOnClickListener(this);
		}
	}

	private ImageView createImg(String url) throws IOException{
		ImageView img = new ImageView(this);
		img.setAdjustViewBounds(true);
		img.setMaxWidth(getWindowManager().getDefaultDisplay().getWidth());
		img.setMaxHeight(getWindowManager().getDefaultDisplay().getHeight());
		img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		URL picUrl = new URL(url);  
		Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());   
		img.setImageBitmap(pngBM);
		return img;
	}
	
	@Override
	protected void click(View view) throws Exception {
		if (view.getId() == this.leftbutton.getId()) {
			this.to(Activity01.class);
		} else if (view.getId() == this.rightbutton.getId()) {
			this.to(Activity05.class);
		} else if(view.getId()==this.voice.getId()){
			this.isPlay = !this.isPlay;
			if(this.isPlay){
				this.voice.setImageResource(R.drawable.pause_big);
				this.onPlay(true,this.subject.get("fileUrl"));
			}else{
				this.voice.setImageResource(R.drawable.play_big);
				this.onPlay(false,null);
			}
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.activity03;
	}

	@Override
	protected String getDomain() {
		return "a3_";
	}

}
