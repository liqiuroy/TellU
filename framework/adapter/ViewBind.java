package adapter;

import object.JsonObject;
import android.view.View;

public interface ViewBind {
	/**
	 * 如果返回false,则系统启用默认的绑定模式
	 * @param json
	 * @param parentView
	 * @param columnName
	 * @return
	 */
	public boolean bind(JsonObject json,View parentView,String columnName) throws Exception;
}
