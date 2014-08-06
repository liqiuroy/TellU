package custview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.roy.tellu.R;

public class Xtab extends LinearLayout{
	private LinearLayout ll;
	private List<XtabItem> menus = new ArrayList<XtabItem>();
	private String currentMenuName;
	public Xtab(Context context) {
		this(context,null);
	}

	public Xtab(Context context, AttributeSet attrs) {
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.xtab, this, true);
		ll = (LinearLayout)this.findViewById(R.id.xtab_layout);
		if(attrs!=null){
			TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.X);
			if(ta.hasValue(R.styleable.X_text)){
				String names = ta.getString(R.styleable.X_text);
				if(names.indexOf(",")>0){
					String[] ss = names.split(",");
					for(int i=0;i<ss.length;i++){
						addMenu(ss[i]);
						if(i<(ss.length-1)){
							addSP();
						}
					}
				}
			}
			if(ta.hasValue(R.styleable.X_height) && ta.hasValue(R.styleable.X_width)){
				this.setLayoutParams(new LayoutParams(ta.getInt(R.styleable.X_width,LayoutParams.FILL_PARENT),
						ta.getInt(R.styleable.X_height,LayoutParams.FILL_PARENT)));
			}
			ta.recycle();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_DOWN)
			return super.onInterceptTouchEvent(ev);
		else
			return true;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		for(XtabItem v : this.menus){
			v.setOnClickListener(l);
		}
	}

	private void addSP() {
		ImageView img = new ImageView(this.getContext());
		img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
		img.setImageResource(R.drawable.sp_v);
		ll.addView(img);
	}

	private void addMenu(final String menuName){
		XtabItem it = new XtabItem(this.getContext());
		it.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
		it.setText(menuName);
		it.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==event.ACTION_DOWN)
					currentMenuName = menuName;
				return false;
			}
		});
		ll.addView(it);
		menus.add(it);
	}
	
	public String getMenuName(){
		return this.currentMenuName;
	}
}
