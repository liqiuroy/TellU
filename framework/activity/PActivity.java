package activity;

import http.MyHttpClient;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import object.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import utils.JSONUtil;
import adapter.DataAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.roy.tellu.R;

import dao.BaseDao;
import data.Data;


/**
 * �ó��������ԭ�е�Activity������̳�
 * �����װ��һЩ���õķ������Լ�����ؼ��Զ�ע�빦��
 * @author Administrator
 *
 */
public abstract class PActivity extends Activity implements OnClickListener,OnTouchListener,OnScrollListener,
OnItemClickListener,OnItemLongClickListener,OnTabChangeListener,OnCheckedChangeListener{
	/**
	 * http�������󣬷�װ��get��post���ļ��ϴ�����
	 */
	protected MyHttpClient http = new MyHttpClient();
	
	protected static BaseDao baseDao;
	
	/**
	 * ��������
	 */
	protected ImageView leftbutton;
	protected ImageView rightbutton;
	protected TextView title;
	
	/**
	 * ��Ƶ
	 */
	protected MediaRecorder mRecorder;
	protected MediaPlayer mPlayer;
	
	/**
	 * ��¼ListView��ǰitem�±�
	 */
	protected int currentIndex;
	
	/**
	 * ��������
	 */
	private Bundle params;
	
	/**
	 * �÷�����Ҫ��������д������ԭ�е�onCreate
	 * @throws Exception
	 */
	abstract protected void init() throws Exception;
	
	/**
	 * ��Ҫ��������д
	 * @return ��Ӧ��layout
	 */
	abstract protected int getLayout();
	
	/**
	 * ������Ҫ��������д������ֵ��Ϊ�Զ���ȡview��IDǰ׺
	 * ���磺layout�����ļ���ĳButton��IDΪ xx_btn
	 * �Զ���ȡ���������ַ�ʽ
	 * ��ʽһ:
	 * private Button xx_btn;
	 * ....
	 * getDomain(){return null;}
	 * 
	 * ��ʽ��:
	 * private Button btn;
	 * ...
	 * getDomain(){return "xx_";}
	 * @return
	 */
	abstract protected String getDomain();
	
	/**
	 * �÷������ܱ�������д�����������дinit()���ﵽԭ�е�Ŀ��
	 * �÷������������²�����
	 * 1���������������ؼ�(pinit1)
	 * 2���Զ�ע��ؼ������ؼ���������������layout��ID��ͬ(pinit2)
	 * 3��Ϊÿ�����ഴ�����ݿ��������
	 * 4�������Զ����¼�(init)
	 * 5��ͳһ���񲢴���init�쳣
	 */
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		try {
			pinit1();
			pinit2();
			pinit3();
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �������������ؼ�
	 * ������ͳһ���ָ�ʽ:��ť  ����   �Ұ�ť
	 */
	private void pinit1() {
		this.leftbutton = (ImageView)this.findViewById(R.id.leftbutton);
		this.title = (TextView)this.findViewById(R.id.title);
		this.rightbutton = (ImageView)this.findViewById(R.id.rightbutton);
		if(this.leftbutton!=null) this.leftbutton.setOnClickListener(this);
		if(this.rightbutton!=null) this.rightbutton.setOnClickListener(this);
	}
	
	/**
	 * �Է����Զ�ע��淶�Ŀؼ�����ע��
	 * �÷�����ʵ�ֲ�ȡ��Java����ԭ��
	 * ����˷����ظ���findViewById(...)����
	 * @throws Exception
	 */
	private void pinit2() throws Exception{
		String d = getDomain()==null?"":getDomain();
		Class clzz = this.getClass();
		Field[] fields = clzz.getDeclaredFields();
		for(Field f : fields){
			//if(f.getDeclaringClass().isAssignableFrom(View.class)){
				int v = r(d+f.getName());
				if(v!=0){
					Object obj = this.findViewById(v);
					if(obj!=null){
						f.setAccessible(true);
						f.set(this,obj);
					}
				}
			//}
		}
	}
	
	/**
	 * �������ݿ��������
	 * �淶ͳһ�����ݿ����
	 */
	private void pinit3(){
		if(baseDao==null){
			baseDao = new BaseDao();
		}
	}
	
	/**
	 * �ж�R.id���Ƿ��иñ���
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	private static int r(String fieldName) throws Exception{
		if(Data.cacheMap.get(fieldName)!=null){
			Field f = (Field)Data.cacheMap.get(fieldName);
			return f.getInt(null);
		}else{
			return 0;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.init, menu);
		return true;
	}
	
	/**--------------¼�����  ��ʼ----------------**/
	/**
	 * ��Ƶ�ļ�¼��
	 * @param isBegin=true ��ʼ¼��  isBegin=false ֹͣ¼��
	 * @param fileName ��Ƶ�ļ�������ļ���
	 */
	protected void onRecording(boolean isBegin,String fileName){
		if(isBegin)
			this.startRecording(fileName);
		else
			this.stopRecording();
	}
	
	private void startRecording(String fileName){
		File file = new File(fileName);
		mRecorder = new MediaRecorder();  
        //������ԴΪMicphone  
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
        //���÷�װ��ʽ  
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);  
        mRecorder.setOutputFile(file.getAbsolutePath());  
        //���ñ����ʽ  
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
  
        try {  
            mRecorder.prepare();  
        } catch (IOException e) {  
            Log.e(this.getClass().getName(), "prepare() failed");  
        }  
        mRecorder.start();  
	}
	
	private void stopRecording() {  
        mRecorder.stop();  
        mRecorder.release();  
        mRecorder = null;  
    }  
	
	/**
	 * ������Ƶ
	 * @param start=true ��ʼ���� start=false ֹͣ����
	 * @param fileName �����ļ���·��
	 */
	protected void onPlay(boolean start,String fileName) {  
        if (start) {  
            startPlaying(fileName);  
        } else {  
            stopPlaying();  
        }  
    }  
  
    private void startPlaying(String fileName) {  
        mPlayer = new MediaPlayer();  
        try {
            mPlayer.setDataSource(fileName);  
            mPlayer.prepare();  
            mPlayer.start();  
        } catch (IOException e) {  
            Log.e(this.getClass().getName(), "prepare() failed");  
        }  
    }  
  
    private void stopPlaying() {  
    	if(mPlayer==null) return;
        mPlayer.release();  
        mPlayer = null;  
    }  
	/**--------------¼�����  ����----------------**/
    
    /**--------------JSON��� ��ʼ---------------**/
    /**
     * �ַ���תJsonObject
     * JsonObjectΪԭJSONObject�Ĵ�����
     * @param str
     * @return
     */
    protected JsonObject toJson(String str){
    	try {
			return JSONUtil.toJsonObject(str);
		} catch (JSONException e) {
			
		}
    	return null;
    }
    /**--------------JSON��� ����---------------**/
    
    /**--------------ҳ����ת���  ��ʼ---------------**/
    protected void to(Class to,Bundle b){
    	Intent intent = new Intent();
    	if(b!=null) intent.putExtras(b);
		intent.setClass(this, to);
		startActivity(intent);
    }
    
    protected void to(Class to){
    	this.to(to,this.params);
    }
    
    protected Bundle addParam(String key,String val){
    	if(this.params==null){
    		this.params = new Bundle();
    	}
    	this.params.putString(key, val);
    	return this.params;
    }
    
    protected Bundle addParam(JsonObject json){
    	JSONObject realJson = json.prototype();
    	Iterator it = realJson.keys();
    	while(it.hasNext()){
    		String c = (String)it.next();
    		try {
				this.addParam(c,json.get(c));
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	return this.params;
    }
    
    protected String getParam(String key){
    	return this.getIntent().getExtras().getString(key);
    }
    /**--------------ҳ����ת��� ����---------------**/
    
    /**
     * ��activityֹͣʱ�Զ��ͷ���Ƶ��Դ������Ѿ�ռ�ã�
     * �÷������ܱ�������д
     */
    @Override  
    protected final void onPause() {  
        super.onPause();  
        //Activity��ͣʱ�ͷ�¼���Ͳ��Ŷ���  
        if (mRecorder != null) {  
            mRecorder.release();  
            mRecorder = null;  
        }  
  
        if (mPlayer != null) {  
            mPlayer.release();  
            mPlayer = null;  
        }
        myPause();
    }

    /**
     * ������д�÷��������ԭ�е�onPause()
     */
	protected void myPause() {
		
	}  

	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дtouch()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final boolean onTouch(View view, MotionEvent event) {
		try{
			touch(view,event);
		}catch(Exception e){
			e.printStackTrace();
			errAlert(e);
		}
		return false;
	}
	
	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дclick()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final void onClick(View arg0) {
		try {
			click(arg0);
		}catch (Exception e) {
			e.printStackTrace();
			errAlert(e);
		}
	}

	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дscrollStateChanged()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final void onScrollStateChanged(AbsListView view, int scrollState) {
		try {
			if(getAdapter()!=null && currentIndex==getAdapter().getCount()){
				getAdapter().nextPage();
			}
			scrollStateChanged(view,scrollState);
		}catch (Exception e) {
			e.printStackTrace();
			errAlert(e);
		}
	}

	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дscroll()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		try {
			if(getAdapter()!=null)
				currentIndex = firstVisibleItem + visibleItemCount;
			scroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
		}catch (Exception e) {
			e.printStackTrace();
			errAlert(e);
		}
	}

	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дitemLongClick()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		try{
			itemLongClick(parent,view,position,id);
		}catch(Exception e){
			e.printStackTrace();
			errAlert(e);
		}
		return false;
	}

	protected void itemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	/**
	 * �÷������ܱ�������д
	 * ����ֻ��ͨ����дitemClick()������ʵ�ֶ�Ӧ����
	 * �÷�����׽��������������쳣����ִ��ͳһ���쳣������Ϊ
	 */
	@Override
	public final void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try{
			itemClick(parent,view,position,id);
		}catch(Exception e){
			e.printStackTrace();
			errAlert(e);
		}
	}

	protected void itemClick(AdapterView<?> parent, View view, int position,
			long id) throws Exception{
	}

	/**
	 * ���������д�˷����������ض�Ӧ��Adapter����Ĭ�Ͽ����Զ���ҳ���ع���
	 * @return
	 */
	protected DataAdapter getAdapter(){
		return null;
	}
	
	/**
	 * �÷����������д���ԭ�е�onScrollStateChanged�¼�
	 * ������д�÷���ʱ������Ҫ���쳣���в����Ա����е�������쳣��ͳһ�Ĵ���
	 * @param view
	 * @throws Exception
	 */
	protected void scrollStateChanged(AbsListView view, int scrollState)  throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * �÷����������д���ԭ�е�onScroll�¼�
	 * ������д�÷���ʱ������Ҫ���쳣���в����Ա����е�������쳣��ͳһ�Ĵ���
	 * @param view
	 * @throws Exception
	 */
	protected void scroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)  throws Exception {
		
	}

	/**
	 * �÷����������д���ԭ�е�onTouch�¼�
	 * ������д�÷���ʱ������Ҫ���쳣���в����Ա����е�������쳣��ͳһ�Ĵ���
	 * @param view
	 * @throws Exception
	 */
	protected void touch(View view, MotionEvent event) throws Exception{
		
	}
	
	/**
	 * �÷����������д���ԭ�е�onClick�¼�
	 * ������д�÷���ʱ������Ҫ���쳣���в����Ա����е�������쳣��ͳһ�Ĵ���
	 * @param view
	 * @throws Exception
	 */
	protected void click(View view) throws Exception{
		
	}
	
	/**
	 * �÷������ܱ�������д��
	 * ����ͨ����дtabChangedL���ﵽͬ����Ч��
	 */
	@Override
	public final void onTabChanged(String arg0) {
		try{
			tabChanged(arg0);
		}catch(Exception e){
			e.printStackTrace();
			errAlert(e);
		}
	}

	/**
	 * ���ԭ�е�onTabChanged
	 * @param tabId
	 */
	protected void tabChanged(String tabId) throws Exception{
		
	}

	@Override
	public final void onCheckedChanged(RadioGroup group, int checkedId) {
		try{
			checkedChanged(group,checkedId);
		}catch(Exception e){
			e.printStackTrace();
			errAlert(e);
		}
	}

	protected void checkedChanged(RadioGroup group, int checkedId) {
		
	}

	/**
	 * ��ȡͳһ���쳣��ʾ��Ϣ
	 * @param exceptionClassName
	 * @return
	 */
	private String errMsg(String exceptionClassName){
		String msg = Data.errMsgs.getProperty(exceptionClassName);
		if(msg==null) msg = Data.errMsgs.getProperty("other");
		return msg;
	}
	
	/**
	 * ��ʾ��
	 * @param title
	 * @param content
	 */
	protected void alert(String title,String content){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle(title); 
		builder.setPositiveButton("ȷ��",null);
		builder.setMessage(content); 
		builder.show(); 
	}
	
	/**
	 * �ر������
	 */
	protected void closeKeyborad(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
	
	/**
	 * �쳣����ʱ������ʾ��
	 * @param e
	 */
	private void errAlert(Exception e){
		alert("����",errMsg(e.getClass().getName()));
	}
}
