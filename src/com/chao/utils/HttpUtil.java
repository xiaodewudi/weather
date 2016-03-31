package com.chao.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
	new Thread(new Runnable() {
		
		@Override
		public void run() {
		HttpURLConnection conn=null;
		try {
			URL url=new URL(address);
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(8000);
			conn.setConnectTimeout(8000);
			InputStream in=conn.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(in));
			StringBuilder sb=new StringBuilder();
			String line;
			while((line=br.readLine())!=null){
				sb.append(line);
			}
			if(listener!=null){
				listener.onfinish(sb.toString());
			}
		} catch (Exception e) {
			if(listener!=null){
				listener.onError(e);
			}
		}finally{
			if(conn!=null){
				conn.disconnect();
			}
		}
		}
	}).start();
}
}
