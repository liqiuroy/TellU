package utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;

public class SystemUtil {
	/**
	 * get imei number
	 * @param activity
	 * @return
	 */
	public static String getIMEI(Activity activity){
		TelephonyManager tlm = (TelephonyManager)activity.getSystemService(Activity.TELEPHONY_SERVICE);
		System.out.println("imei-->"+tlm.getDeviceId());
		return tlm.getDeviceId();
	}
	
	public static String getImgPath(Activity activity,Uri uri){
		Cursor cursor = activity.getContentResolver().query(uri,new String[]{MediaStore.Images.Media.DATA}, null, null, null);
		cursor.moveToFirst();//将游标移到第一位
		int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();
		return imagePath;
	}
}
