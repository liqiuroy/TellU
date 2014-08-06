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
 * 该抽象类代替原有的Activity供子类继承
 * 该类封装了一些常用的方法，以及界面控件自动注入功能
 * @author Administrator
 *
 */
public abstract class PActivity extends Activity implements OnClickListener,OnTouchListener,OnScrollListener,
OnItemClickListener,OnItemLongClickListener,OnTabChangeListener,OnCheckedChangeListener{
	/**
	 * http交互对象，封装了get、post、文件上传功能
	 */
	protected MyHttpClient http = new MyHttpClient();
	
	protected static BaseDao baseDao;
	
	/**
	 * 顶部布局
	 */
	protected ImageView leftbutton;
	protected ImageView rightbutton;
	protected TextView title;
	
	/**
	 * 音频
	 */
	protected MediaRecorder mRecorder;
	protected MediaPlayer mPlayer;
	
	/**
	 * 记录ListView当前item下标
	 */
	protected int currentIndex;
	
	/**
	 * 参数传递
	 */
	private Bundle params;
	
	/**
	 * 该方法需要被子类重写，代替原有的onCreate
	 * @throws Exception
	 */
	abstract protected void init() throws Exception;
	
	/**
	 * 需要被子类重写
	 * @return 对应的layout
	 */
	abstract protected int getLayout();
	
	/**
	 * 该类需要被子类重写，返回值作为自动获取view的ID前缀
	 * 例如：layout配置文件中某Button的ID为 xx_btn
	 * 自动获取有如下两种方式
	 * 方式一:
	 * private Button xx_btn;
	 * ....
	 * getDomain(){return null;}
	 * 
	 * 方式二:
	 * private Button btn;
	 * ...
	 * getDomain(){return "xx_";}
	 * @return
	 */
	abstract protected String getDomain();
	
	/**
	 * 该方法不能被子类重写，子类可以重写init()来达到原有的目的
	 * 该方法中做了如下操作：
	 * 1、处理顶部标题栏控件(pinit1)
	 * 2、自动注入控件，但控件变量命名必须与layout中ID相同(pinit2)
	 * 3、为每个子类创建数据库操作对象
	 * 4、子类自定义事件(init)
	 * 5、统一捕获并处理init异常
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
	 * 处理顶部标题栏控件
	 * 标题栏统一布局格式:左按钮  标题   右按钮
	 */
	private void pinit1() {
		this.leftbutton = (ImageView)this.findViewById(R.id.leftbutton);
		this.title = (TextView)this.findViewById(R.id.title);
		this.rightbutton = (ImageView)this.findViewById(R.id.rightbutton);
		if(this.leftbutton!=null) this.leftbutton.setOnClickListener(this);
		if(this.rightbutton!=null) this.rightbutton.setOnClickListener(this);
	}
	
	/**
	 * 对符合自动注入规范的控件进行注入
	 * 该方法的实现采取的Java反射原理
	 * 免除了繁琐重复的findViewById(...)操作
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
	 * 创建数据库操作对象
	 * 规范统一的数据库操作
	 */
	private void pinit3(){
		if(baseDao==null){
			baseDao = new BaseDao();
		}
	}
	
	/**
	 * 判断R.id中是否有该变量
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
	
	/**--------------录音组件  开始----------------**/
	/**
	 * 音频文件录制
	 * @param isBegin=true 开始录制  isBegin=false 停止录制
	 * @param fileName 音频文件保存的文件名
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
        //设置音源为Micphone  
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
        //设置封装格式  
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);  
        mRecorder.setOutputFile(file.getAbsolutePath());  
        //设置编码格式  
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
	 * 播放音频
	 * @param start=true 开始播放 start=false 停止播放
	 * @param fileName 播放文件的路径
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
	/**--------------录音组件  结束----------------**/
    
    /**--------------JSON组件 开始---------------**/
    /**
     * 字符串转JsonObject
     * JsonObject为原JSONObject的代理类
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
    /**--------------JSON组件 结束---------------**/
    
    /**--------------页面跳转组件  开始---------------**/
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
    /**--------------页面跳转组件 结束---------------**/
    
    /**
     * 但activity停止时自动释放音频资源（如果已经占用）
     * 该方法不能被子类重写
     */
    @Override  
    protected final void onPause() {  
        super.onPause();  
        //Activity暂停时释放录音和播放对象  
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
     * 子类重写该方法，替代原有的onPause()
     */
	protected void myPause() {
		
	}  

	/**
	 * 该方法不能被子类重写
	 * 子类只能通过重写touch()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 该方法不能被子类重写
	 * 子类只能通过重写click()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 该方法不能被子类重写
	 * 子类只能通过重写scrollStateChanged()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 该方法不能被子类重写
	 * 子类只能通过重写scroll()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 该方法不能被子类重写
	 * 子类只能通过重写itemLongClick()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 该方法不能被子类重写
	 * 子类只能通过重写itemClick()方法来实现对应功能
	 * 该方法捕捉所有子类产生的异常，并执行统一的异常处理行为
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
	 * 如果子类重写此方法，并返回对应的Adapter，则默认开启自动分页加载功能
	 * @return
	 */
	protected DataAdapter getAdapter(){
		return null;
	}
	
	/**
	 * 该方法在子类中代替原有的onScrollStateChanged事件
	 * 子类重写该方法时尽量不要对异常进行捕获，以便所有的子类对异常有统一的处理
	 * @param view
	 * @throws Exception
	 */
	protected void scrollStateChanged(AbsListView view, int scrollState)  throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 该方法在子类中代替原有的onScroll事件
	 * 子类重写该方法时尽量不要对异常进行捕获，以便所有的子类对异常有统一的处理
	 * @param view
	 * @throws Exception
	 */
	protected void scroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)  throws Exception {
		
	}

	/**
	 * 该方法在子类中代替原有的onTouch事件
	 * 子类重写该方法时尽量不要对异常进行捕获，以便所有的子类对异常有统一的处理
	 * @param view
	 * @throws Exception
	 */
	protected void touch(View view, MotionEvent event) throws Exception{
		
	}
	
	/**
	 * 该方法在子类中代替原有的onClick事件
	 * 子类重写该方法时尽量不要对异常进行捕获，以便所有的子类对异常有统一的处理
	 * @param view
	 * @throws Exception
	 */
	protected void click(View view) throws Exception{
		
	}
	
	/**
	 * 该方法不能被子类重写，
	 * 可以通过重写tabChangedL来达到同样的效果
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
	 * 替代原有的onTabChanged
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
	 * 获取统一的异常提示信息
	 * @param exceptionClassName
	 * @return
	 */
	private String errMsg(String exceptionClassName){
		String msg = Data.errMsgs.getProperty(exceptionClassName);
		if(msg==null) msg = Data.errMsgs.getProperty("other");
		return msg;
	}
	
	/**
	 * 提示框
	 * @param title
	 * @param content
	 */
	protected void alert(String title,String content){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle(title); 
		builder.setPositiveButton("确定",null);
		builder.setMessage(content); 
		builder.show(); 
	}
	
	/**
	 * 关闭软键盘
	 */
	protected void closeKeyborad(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
	
	/**
	 * 异常发生时弹出提示框
	 * @param e
	 */
	private void errAlert(Exception e){
		alert("错误",errMsg(e.getClass().getName()));
	}
}
