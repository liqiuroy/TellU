package http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import object.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import utils.JSONUtil;
import utils.StringUtil;
import data.I;

/**
 * �����װ��http�Ľ���������get��post���ļ��ϴ���֧�ֶ��ļ���
 * @author Administrator
 *
 */
public class MyHttpClient {
	private static String PREFIX = "--", LINEND = "\r\n",MULTIPART_FROM_DATA = "multipart/form-data",
	CHARSET = "UTF-8",POST = "POST:",GET = "GET:",string = "#s",file = "#f";
	
	/**
	 * ���ýӿڲ���ȡ�ַ������͵ķ���ֵ
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String get(String url, String[] params) throws ClientProtocolException, IOException{
		//�жϸýӿ��Ƿ����post����
		boolean isPost = isPost(url);
		url = clear(url);
		if(isPost){
			return doPost(url, params);
		}else{
			return doGet(url,params);
		}
	}
	
	public JsonObject getJson(String url,String[] params) throws ClientProtocolException, IOException{
		String rst = get(url,params);
		try {
			return JSONUtil.toJsonObject(rst);
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * post��ʽ�ύ
	 * @param url
	 * @param params
	 * @return
	 */
	private static String doPost(String url, String[] params){
		//�����ӿ�
		Object[] objs = parsePostUrl(url,params);
		String BOUNDARY = StringUtil.uid();
		StringBuilder rs = new StringBuilder();
		DataOutputStream outStream = null;
		HttpURLConnection conn = null;
		try{
			/*
			 * post�����ʼ��
			 */
			URL uri = new URL(I.i((String)objs[0]));
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(50 * 1000); // ������ʱ��
			conn.setDoInput(true);// ��������
			conn.setDoOutput(true);// �������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", CHARSET);
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			
			/*
			 * �����ͨ���ַ������Ͳ���
			 */
			StringBuilder sb = new StringBuilder();
			for(String[] ss : (List<String[]>)objs[1]){
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ ss[0] + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(ss[1]);
				sb.append(LINEND);
			}
			
			outStream = new DataOutputStream(conn
					.getOutputStream());
			outStream.write(sb.toString().getBytes());
			
			/*
			 * �ύ�ļ������Ͳ���
			 */
			InputStream in = null;
			if(((List)objs[2]).size()>0){
				for(Object[] aobj : (List<Object[]>)objs[2]){
					if(!((File)aobj[1]).exists()){
						continue;
					}
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\""+(String)aobj[0]+"\"; filename=\""
									+ ((File)aobj[1]).getName() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());
	
					InputStream is = new FileInputStream((File)aobj[1]);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
	
					is.close();
					outStream.write(LINEND.getBytes());
				}
			}
			
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			int res = conn.getResponseCode();
			if (res == 200) {
				String readLine;
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
	               rs.append(readLine).append("\n");
	            }
				responseReader.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(outStream!=null){
				try {
					outStream.close();
				} catch (IOException e) {
				}
			}
			if(conn!=null){
				conn.disconnect();
			}
		}
		return rs.toString();
	}

	/**
	 * ����post�ӿ�
	 * @param url �ӿ�
	 * @param params �ӿڲ���
	 * @return ����Ϊ3��Object���飺
	 * [0]���ύ��url��ַ��String��
	 * [1]: �ַ����������,����Ϊ2���ַ�������List,�ַ��������ʽΪ["name","value"]
	 * [2]: �ļ������������,����Ϊ2��Object����List,Object�����ʽΪ["name",File]
	 */
	private static Object[] parsePostUrl(String url, String[] params) {
		Object[] objs = new Object[3];
		List<String[]> commonParams = new ArrayList<String[]>();
		List<Object[]> fileParams = new ArrayList<Object[]>();
		
		String[] strs = url.split("\\u003F");//�����ָ�
		objs[0] = strs[0];//url
		
		String[] ps = strs[1].split("&");
		int j = 0;
		for(int i=0;i<ps.length;i++){
			String[] pv = ps[i].split("=");
			if(params[j]==null){
				params[j]="";
			}
			if(string.equals(pv[1])){//�ò���Ϊ�ַ���
				commonParams.add(new String[]{pv[0],params[j++]});
			}else if(file.equals(pv[1])){//�ò���Ϊ�ļ�����
				if(params[j].trim().length()==0){
					j++;
					continue;
				}
				fileParams.add(new Object[]{pv[0],new File(params[j++])});
			}else{
				commonParams.add(new String[]{pv[0],pv[1]});
			}
		}
		objs[1]=commonParams;
		objs[2]=fileParams;
		return objs;
	}

	/**
	 * get�ύ
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static String doGet(String url,String[] params) throws ClientProtocolException, IOException{
		String realUrl = I.i(parseGetUrl(url,params));
		HttpClient httpclient = new DefaultHttpClient();  
		HttpGet httpGet = new HttpGet(realUrl);
		HttpResponse response = httpclient.execute(httpGet);
		if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}
	
	private static boolean isPost(String url){
		return url.indexOf(POST)==0;
	}
	
	/**
	 * ��ȡʵ�ʵĽӿ�url��ʽ
	 * @param url
	 * @return
	 */
	private static String clear(String url){
		if(url.indexOf(POST)==0){
			return url.substring(POST.length());
		}
		if(url.indexOf(GET)==0){
			return url.substring(GET.length());
		}
		return url;
	}
	
	/**
	 * ����get�ύUrl
	 * @param url
	 * @param params
	 * @return
	 */
	private static String parseGetUrl(String url, String[] params) {
		int i = 0;
		while(url.indexOf(string)>=0){
			if((i+1)>params.length)//��������������ռλ������������֮���ռλ���滻Ϊ���ַ���
				url = url.replaceFirst(string, "");
			else
				url = url.replaceFirst(string,params[i]);
			i++;
		}
		return url;
	}
}
