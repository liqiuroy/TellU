package adapter;

import http.MyHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.JsonObject;

import org.json.JSONException;

import utils.JSONUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import dao.BaseDao;


/**
 * 该类为ListView做异步数据加载
 * @author Administrator
 *
 */
public class DataAdapter extends BaseAdapter {
	/**
	 * 存放数据加载是否完毕的状态
	 * 如果异步数据加载完成，在该Map中会有对应的记录
	 * 通过判断该Map中有无记录来决定何时刷新UI
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer,String> readyMap = new HashMap<Integer,String>();
	
	/**
	 * 数据来源:http或本地数据库
	 */
	private String type;
	
	/**
	 * 如果来源为http则此字段为访问url
	 * 如果来源为数据库则此字段为SQL语句
	 */
	private String what;
	private String[] params;
	
	/**
	 * 如果指定了idStr字段，则getItemId方法会返回对应数据中该字段名的值
	 */
	private String idStr;
	
	private LayoutInflater mInflater;
	
	/**
	 * UI布局
	 */
	private int mResource;
	
	/**
	 * UI填充映射关系
	 * from中存放数据字段名
	 * to中存放需要填充的控件ID
	 */
	private String[] from;
	private int[] to;
	
	/**
	 * 当前页码
	 */
	private int page = 0;
	/**
	 * 一页加载数目
	 */
	private int num = 20;
	
	/**
	 * 如果该字段等于true,则停止继续数据加载
	 */
	private boolean over = false;
	
	/**
	 * 存放数据
	 */
	private List<JsonObject> data = new ArrayList<JsonObject>();
	private Handler handler;
	
	public static String HTTP = "http";
	public static String SQL = "sql";
	
	private DataValue dataValue;
	private ViewBind viewbind;
	
	/**
	 * 但滑动到最后一行，自动加载下一页数据
	 * @param handler
	 * @throws Exception
	 */
	public DataAdapter nextPage() throws Exception{
		this.page++;
		this.getData(this.getHandler());
		return this;
	}
	
	public DataAdapter reload() throws Exception{
		this.getData(this.getHandler());
		return this;
	}
	
	/**
	 * 显示第一页的数据
	 * @throws Exception 
	 */
	public DataAdapter firstPage() throws Exception{
		this.page = 1;
		this.over = false;
		this.data.clear();
		this.getData(this.getHandler());
		return this;
	}
	
	@Override
	public int getCount() {
		return data==null?0:data.size();
	}

	@Override
	public JsonObject getItem(int inx) {
		if(data==null || inx>=getCount())
			return null;
		else
			return data.get(inx);
	}

	@Override
	public long getItemId(int id) {
		if(getCount()==0) 
			return -1;
		if(idStr!=null){
			try {
				return Integer.parseInt(data.get(id).get(idStr));
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}else
			return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
		try {
			view = createViewFromResource(position, convertView, parent, mResource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) throws Exception {
        View v;
        /*if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }*/
        v = mInflater.inflate(resource, parent, false);
        bindView(position, v);

        return v;
    }
	
    /**
     * 该方法来自于SimpleAdapter
     * @param position
     * @param view
     * @throws JSONException
     */
    private void bindView(int position, View view) throws Exception {
        final JsonObject dataSet = this.data.get(position);
        if (dataSet == null) {
            return;
        }

        final String[] from = this.from;
        final int[] to = this.to;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = getValue(dataSet,from[i]);
                
                if(this.viewbind==null || this.viewbind.bind(dataSet, view, from[i])==false){
                	defaultBind(v,data);
                }
            }
        }
    }
    
    private void defaultBind(View v,Object data){
        String text = data == null ? "" : data.toString();
        if (text == null) {
            text = "";
        }
    	if (v instanceof Checkable) {
            if (data instanceof Boolean) {
                ((Checkable) v).setChecked((Boolean) data);
            } else if (v instanceof TextView) {
                setViewText((TextView) v, text);
            } else {
                throw new IllegalStateException(v.getClass().getName() +
                        " should be bound to a Boolean, not a " +
                        (data == null ? "<unknown type>" : data.getClass()));
            }
        } else if (v instanceof TextView) {
            setViewText((TextView) v, text);
        } else if (v instanceof ImageView) {
            if (data instanceof Integer) {
                setViewImage((ImageView) v, (Integer) data);                            
            } else {
                setViewImage((ImageView) v, text);
            }
        } else if (v instanceof Button){
        	((Button)v).setText(text);
        }else {
            throw new IllegalStateException(v.getClass().getName() + " is not a " +
                    " view that can be bounds by this SimpleAdapter");
        }
    }
    
    private Object getValue(JsonObject json,String column) throws JSONException{
    	if(this.dataValue==null)
    		return json.get(column);
    	else{
    		Object rs = this.dataValue.getValue(column, json.get(column),json);
    		return rs==null?json.get(column):rs;
    	}
    }
    
    public void setViewText(TextView v, String text) {
        v.setText(text);
    }
    
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }
    
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }
    
    /**
     * 设置UI布局
     * @param resource
     * @return
     */
    public DataAdapter setLayout(int resource){
    	this.mResource = resource;
    	return this;
    }
    
    /**
     * 设置数据来源，http或sql
     * @param what
     * @param params
     * @param type
     * @return
     */
    public DataAdapter setDataSource(String what,String[] params,String type){
    	this.setWhat(what);
    	this.setParams(params);
    	this.setType(type);
    	return this;
    }
    
    /**
     * 设置数据与UI的映射关系
     * @param from
     * @param to
     * @return
     */
    public DataAdapter setUIMapping(String[] from, int[] to){
    	this.from=from;
		this.to = to;
		return this;
    }
    
    /**
     * 返回自身，用于匿名类
     * @return
     */
    private DataAdapter prototype(){
    	return this;
    }
    
    public DataAdapter(Context context){
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 异步获取数据
     * @param handler
     * @throws Exception
     */
	private void getData(final Handler handler) throws Exception {
		if(this.isOver()) return;
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					List<JsonObject> list = null;
					if(HTTP.equals(getType())){
						MyHttpClient http = new MyHttpClient();
						String rs = http.get(getWhat()+"&page="+getPage()+"&num="+getNum(),getParams());
						JsonObject json = JSONUtil.toJsonObject(rs);
						if(json.isSuccess()){
							list = json.getDataArray();
							
						}
					}else if(SQL.equals(getType())){
						BaseDao dao = new BaseDao();
						list = dao.getJsons("select * from ("+getWhat()+") _x limit "+(getPage()-1)*getNum()+","+getNum(),getParams());
					}
					if(list.size()<getNum()){
						setOver(true);
					}
					data.addAll(list);
					DataAdapter.readyMap.put(getHashCode(), "");
					Message msg = handler.obtainMessage();  
	                msg.what = getHashCode();  
	                msg.sendToTarget();  
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}).start();
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String getIdStr() {
		return idStr;
	}

	public DataAdapter setIdStr(String idStr) {
		this.idStr = idStr;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPage() {
		return page;
	}

	public DataAdapter setPage(int page) {
		this.page = page;
		return this;
	}

	public int getNum() {
		return num;
	}

	public DataAdapter setNum(int num) {
		this.num = num;
		return this;
	}

	public int getHashCode(){
		return this.hashCode();
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}
	
	/**
	 * 异步加载数据时的自身刷新
	 * @return
	 */
	@SuppressLint("HandlerLeak")
	private Handler getHandler(){
		if(this.handler==null){
			this.handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					boolean bo = false;
					long t = System.currentTimeMillis();
					while(bo==false){
						System.out.println("handle----------------");
						/*
						 * 如果数据加载成功，则DataAdapter.readyMap会有记录
						 */
						bo = DataAdapter.readyMap.remove(msg.what)!=null;
						if(bo || System.currentTimeMillis()-t>10000){
							break;
						}
					}
					if(bo){
						prototype().notifyDataSetChanged();
						//System.out.println("刷新成功");
					}
				}
				
			};
		}
		return this.handler;
	}

	public DataAdapter setDataValue(DataValue dataValue) {
		this.dataValue = dataValue;
		return this;
	}

	public DataAdapter setViewbind(ViewBind viewbind) {
		this.viewbind = viewbind;
		return this;
	}
}
