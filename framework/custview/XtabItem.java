package custview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roy.tellu.R;

public class XtabItem extends LinearLayout{
	private TextView text;
	
	public XtabItem(Context context) {
		this(context,null);
	}

	public XtabItem(Context context, AttributeSet attrs) {
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.xtabitem, this, true);
		text = (TextView)this.findViewById(R.id.text);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.X);
		if(ta.hasValue(R.styleable.X_text)){
			this.text.setText(ta.getString(R.styleable.X_text));
		}
		if(ta.hasValue(R.styleable.X_text_color)){
			this.text.setTextColor(ta.getColor(R.styleable.X_text_color,0));
		}
		ta.recycle();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction()==ev.ACTION_DOWN)
			return true;
		else
			return super.onInterceptTouchEvent(ev);
	}

	public void setText(String str){
		this.text.setText(str);
	}
}
