package utils;

import java.lang.reflect.Method;

import android.widget.SimpleAdapter;

public class ThreadUtil {
	public static void go(final SimpleAdapter sa,final Object obj,final String mtdName){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Class clzz = obj.getClass();
				try {
					Method mtd = clzz.getMethod(mtdName);
					mtd.invoke(obj);
					sa.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
	}
}
