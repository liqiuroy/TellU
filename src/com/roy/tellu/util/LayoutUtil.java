package com.roy.tellu.util;

import utils.TableLayoutUtils;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import custview.Ximage;

public class LayoutUtil {
	public static void addXImage(Ximage x,TableLayout t,int columnNum){
		TableRow row = (TableRow)t.getChildAt(t.getChildCount()-1);
		View view = row.getChildAt(row.getChildCount()-1);
		row.removeView(view);
		TableLayoutUtils.addView(x, t, columnNum);
		TableLayoutUtils.addView(view, t, columnNum);
	}
}
