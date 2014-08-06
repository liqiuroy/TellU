package adapter;

import object.JsonObject;

public interface DataValue {
	Object getValue(String columnName,String columnValue,JsonObject json);
}
