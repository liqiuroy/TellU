package utils;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TableLayoutUtils {
	public static void addView(View v,TableLayout t,int columnNum){
		if(t.getChildCount()<=0){
			t.addView(new TableRow(t.getContext()));
		}
		TableRow row = (TableRow)t.getChildAt(t.getChildCount()-1);
		if(row.getChildCount()==columnNum){
			TableRow newRow = new TableRow(t.getContext());
			t.addView(newRow);
		}
		row = (TableRow)t.getChildAt(t.getChildCount()-1);
		row.addView(v);
	}
}
