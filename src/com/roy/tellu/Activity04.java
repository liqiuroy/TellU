package com.roy.tellu;

import activity.PActivity;
import adapter.DataAdapter;
import android.view.View;
import android.widget.ListView;

public class Activity04 extends PActivity {
	private ListView emails;
	private DataAdapter da;
	@Override
	protected void init() throws Exception {
		this.title.setText("” œ‰");
	}

	@Override
	protected void click(View view) throws Exception {
		if(view.getId()==this.leftbutton.getId()){
			this.finish();
		}
		else if(view.getId()==this.rightbutton.getId()){
			this.to(Activity02.class);
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.activity04;
	}

	@Override
	protected String getDomain() {
		return "a4_";
	}

}
