package com.roy.bean;

import android.view.View;

public class KeyBean {
	private String key;
	private View view;
	
	public KeyBean(String key,View view){
		this.key = key;
		this.view = view;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null || !(o instanceof KeyBean)){
			return false;
		}else{
			return this.getKey().equals(((KeyBean)o).getKey());
		}
	}

	@Override
	public int hashCode() {
		return this.key.hashCode();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

}
