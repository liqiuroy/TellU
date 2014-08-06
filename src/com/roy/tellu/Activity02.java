package com.roy.tellu;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import object.JsonObject;
import utils.FileUtil;
import utils.StringUtil;
import utils.SystemUtil;
import utils.TableLayoutUtils;
import activity.PActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.roy.bean.KeyBean;
import com.roy.tellu.util.F;
import com.roy.tellu.util.LayoutUtil;

import custview.Ximage;
import custview.Xtab;
import data.Data;
import data.I;

public class Activity02 extends PActivity implements Runnable{
	private TableLayout imgTab;
	private ImageView stateimg,select_photo;
	private TextView msg;
	private EditText stitle,text;
	private LinearLayout yuyin,wenzi;
	private Xtab xtab;
	private Button submit;
	private int totalSec;
	
	private boolean begin=false;
	private List<String> filePaths = new ArrayList<String>();
	private final List<KeyBean> keyBeans = new ArrayList<KeyBean>();
	private boolean isWenZi = true;
	
	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message message) {
			msg.setText(F.toMin(message.what));
			super.handleMessage(message);
		}
		
	};
	
	@Override
	protected void init() throws Exception {
		this.title.setText("说说我的故事");
		this.stateimg.setOnClickListener(this);
		this.select_photo.setOnClickListener(this);
		this.xtab.setOnClickListener(this);
		this.xtab.setOnTouchListener(this);
		this.submit.setOnClickListener(this);
		Thread thread = new Thread(this);
		thread.start();
	}

	private void clearLocalData(){
		this.baseDao.execute("delete from tab_copy");
	}
	
	@Override
	protected void click(View view) throws Exception {
		//返回
		if(view.getId()==this.leftbutton.getId()){
			clearLocalData();
			this.finish();
		}
		else if(view.getId()==this.select_photo.getId()){
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
            this.startActivityForResult(intent, 1);  
		}
		//发送
		else if(view.getId()==this.submit.getId()){
			if(StringUtil.isEmpty(this.stitle.getText().toString())){
				this.alert("提示","请填写标题");
				return;
			}
			
			String targetPath = null;
			String contentMsg = null;
			if(this.isWenZi==false){
				//将所有片段整合进一个大文件提交给服务器
				if(this.totalSec==0){
					this.alert("提示","您还没有录音哦");
					return;
				}
				targetPath = FileUtil.getNewFilePath(Data.AMR);
				FileUtil.amrMergerAndDel(targetPath,this.filePaths);
			}else{
				contentMsg = this.text.getText().toString();
				if(contentMsg.trim().length()<15){
					this.alert("提示","文字内容要大于15个字");
					return;
				}
			}
			//上传内容
			JsonObject object = this.http.getJson(I.SAVE_SUBJECT, new String[]{contentMsg,targetPath,F.getCustId(),this.stitle.getText().toString(),totalSec+""});
			if(!object.isSuccess()){
				this.alert("错误", object.errMsg());
				return;
			}
			String uid = object.getDataString();
			//上传图片
			for(KeyBean kb :this.keyBeans){
				JsonObject obj = this.http.getJson(I.SAVE_IMG,new String[]{uid,kb.getKey()});
				if(obj.isSuccess()){
					
				}
			}
			object = this.http.getJson(I.GET_SUBJECT,new String[]{uid});
			if(!object.isSuccess()){
				this.alert("错误", object.errMsg());
				return;
			}
			clearLocalData();
			this.addParam(object.getDataObject());
			this.to(Activity03.class);
		}
		else if(view.getId()==this.stateimg.getId()){
			if(!this.begin){
				doStart();
			}else{
				doStop();
			}
			this.begin = !this.begin;
		}
		else if(view.getId()==this.xtab.getId()){
			this.alert("",this.xtab.getMenuName());
		}
	}
	
	@Override
	protected void touch(View view, MotionEvent event) throws Exception {
		if(view.getId()==this.xtab.getId()){
			if(event.getAction()==MotionEvent.ACTION_UP){
				this.yuyin.setVisibility(View.INVISIBLE);
				this.wenzi.setVisibility(View.INVISIBLE);
				if(this.xtab.getMenuName().equals("文字新闻")){
					this.isWenZi = true;
					this.wenzi.setVisibility(View.VISIBLE);
				}else{
					this.isWenZi = false;
					this.yuyin.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  
		if (requestCode== 1 && resultCode == RESULT_OK) {  
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();  
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
                Ximage img = new Ximage(this);
                img.setImage(bitmap);
                img.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						TableRow view = (TableRow)v.getParent();
						view.removeView(v);
						rmKeyBean(v);
						rebuildTable();
					}
                });
                this.keyBeans.add(new KeyBean(SystemUtil.getImgPath(this, uri),img));
                LayoutUtil.addXImage(img,this.imgTab, 4);
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            }  
        }  
	}

	//重新绘制TableLayout
	protected void rebuildTable() {
		int c = this.imgTab.getChildCount();
		for(int i=0;i<c;i++){
			TableRow v = (TableRow)this.imgTab.getChildAt(i);
			v.removeAllViews();
		}
		this.imgTab.removeAllViews();
		for(KeyBean kv : this.keyBeans){
			TableLayoutUtils.addView(kv.getView(),this.imgTab, 4);
		}
		TableLayoutUtils.addView(this.select_photo,this.imgTab, 4);
	}

	private void rmKeyBean(View v) {
		for(KeyBean kb : this.keyBeans){
			if(v.equals(kb.getView())){
				this.keyBeans.remove(kb);
			}
		}
	}

	private void doStop() {
		this.onRecording(false, null);
		this.stateimg.setImageDrawable(this.getResources().getDrawable(R.drawable.play_big));
	}

	private void doStart() {
		String path = FileUtil.getNewFilePath(Data.AMR);
		this.filePaths.add(path);
		this.onRecording(true, path);
		this.stateimg.setImageDrawable(this.getResources().getDrawable(R.drawable.pause_big));
	}

	@Override
	protected int getLayout() {
		return R.layout.activity02;
	}

	@Override
	protected String getDomain() {
		return "a2_";
	}

	@Override
	public void run() {
		while(true){
			if(this.begin){
				totalSec++;
				System.out.println("totalSec-->"+totalSec);
				Message msg = this.handler.obtainMessage();
				msg.what = totalSec;
				msg.sendToTarget();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
