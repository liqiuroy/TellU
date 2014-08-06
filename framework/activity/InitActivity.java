package activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import utils.StringUtil;
import android.content.Context;

import com.roy.tellu.R;

import dao.BaseDao;
import data.Data;


/**
 * ����Activity��ִ��ȫ�ֵĳ�ʼ������
 * @author Administrator
 *
 */
public abstract class InitActivity extends PActivity{
	@Override
	protected void init() {
		try {
			/*
			 * ���ݿ��ʼ��
			 */
			openDb();
			initDb();
			
			/*
			 * ���R.idӳ��
			 */
			reflectR();
			
			/*
			 * ����Ӧ��Ŀ¼
			 */
			sdcard();
			
			/*
			 * ��ʼ���쳣��Ϣ
			 */
			propertiesErrMsg();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void propertiesErrMsg() throws IOException{
		Data.errMsgs.load(this.getResources().openRawResource(R.raw.err));
	}
	
	private void initDb() throws IOException{
		InputStream input = this.getResources().openRawResource(R.raw.sql);
		byte[] bs = new byte[input.available()];
		input.read(bs);
		input.close();
		String sqls = new String(bs,"UTF-8");
		System.out.println(sqls);
		String[] sql = sqls.split(";");
		for(String s : sql){
			if(!StringUtil.isEmpty(s)){
				this.baseDao.execute(s);
			}
		}
	}
	
	private void openDb(){
		if(BaseDao.db==null){
			BaseDao.db = this.openOrCreateDatabase(Data.DB_NAME, Context.MODE_PRIVATE, null);
		}
	}
	
	private void sdcard(){
		File rootDir = new File(Data.FILE_ROOT);
		rootDir.mkdirs();
	}
	
	private void reflectR(){
		Class rzz = R.id.class;
		for(Field f : rzz.getDeclaredFields()){
			Data.cacheMap.put(f.getName(), f);
		}
	}
	
	@Override
	protected int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.main_init;
	}

	@Override
	protected String getDomain() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
