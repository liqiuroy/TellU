package custview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.roy.tellu.R;

public class Ximage extends RelativeLayout{
	private ImageView del;
	private ImageView src;
	
	public Ximage(Context context) {
		this(context,null);
	}
	public Ximage(Context context, AttributeSet attrs) {
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.ximage, this, true);
		del = (ImageView)this.findViewById(R.id.del);
		src = (ImageView)this.findViewById(R.id.src);
		if(attrs!=null){
			TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.X);
			int src_img = ta.getResourceId(R.styleable.X_src, R.drawable.photo);
			this.src.setImageResource(src_img);
			ta.recycle();
		}
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		this.del.setOnClickListener(l);
	}
	
	public void setImage(Bitmap bm){
		this.src.setImageBitmap(bm);
	}
}
