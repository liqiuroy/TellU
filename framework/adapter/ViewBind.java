package adapter;

import object.JsonObject;
import android.view.View;

public interface ViewBind {
	/**
	 * �������false,��ϵͳ����Ĭ�ϵİ�ģʽ
	 * @param json
	 * @param parentView
	 * @param columnName
	 * @return
	 */
	public boolean bind(JsonObject json,View parentView,String columnName) throws Exception;
}
