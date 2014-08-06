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
 * ����ΪListView���첽���ݼ���
 * @author Administrator
 *
 */
public class DataAdapter extends BaseAdapter {
	/**
	 * ������ݼ����Ƿ���ϵ�״̬
	 * ����첽���ݼ�����ɣ��ڸ�Map�л��ж�Ӧ�ļ�¼
	 * ͨ���жϸ�Map�����޼�¼��������ʱˢ��UI
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer,String> readyMap = new HashMap<Integer,String>();
	
	/**
	 * ������Դ:http�򱾵����ݿ�
	 */
	private String type;
	
	/**
	 * �����ԴΪhttp����ֶ�Ϊ����url
	 * �����ԴΪ���ݿ�����ֶ�ΪSQL���
	 */
	private String what;
	private String[] params;
	
	/**
	 * ���ָ����idStr�ֶΣ���getItemId�����᷵�ض�Ӧ�����и��ֶ�����ֵ
	 */
	private String idStr;
	
	private LayoutInflater mInflater;
	
	/**
	 * UI����
	 */
	private int mResource;
	
	/**
	 * UI���ӳ���ϵ
	 * from�д�������ֶ���
	 * to�д����Ҫ���Ŀؼ�ID
	 */
	private String[] from;
	private int[] to;
	
	/**
	 * ��ǰҳ��
	 */
	private int page = 0;
	/**
	 * һҳ������Ŀ
	 */
	private int num = 20;
	
	/**
	 * ������ֶε���true,��ֹͣ�������ݼ���
	 */
	private boolean over = false;
	
	/**
	 * �������
	 */
	private List<JsonObject> data = new ArrayList<JsonObject>();
	private Handler handler;
	
	public static String HTTP = "http";
	public static String SQL = "sql";
	
	private DataValue dataValue;
	private ViewBind viewbind;
	
	/**
	 * �����������һ�У��Զ�������һҳ����
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
	 * ��ʾ��һҳ������
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
     * �÷���������SimpleAdapter
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
     * ����UI����
     * @param resource
     * @return
     */
    public DataAdapter setLayout(int resource){
    	this.mResource = resource;
    	return this;
    }
    
    /**
     * ����������Դ��http��sql
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
     * ����������UI��ӳ���ϵ
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
     * ������������������
     * @return
     */
    private DataAdapter prototype(){
    	return this;
    }
    
    public DataAdapter(Context context){
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * �첽��ȡ����
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
	 * �첽��������ʱ������ˢ��
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
						 * ������ݼ��سɹ�����DataAdapter.readyMap���м�¼
						 */
						bo = DataAdapter.readyMap.remove(msg.what)!=null;
						if(bo || System.currentTimeMillis()-t>10000){
							break;
						}
					}
					if(bo){
						prototype().notifyDataSetChanged();
						//System.out.println("ˢ�³ɹ�");
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
