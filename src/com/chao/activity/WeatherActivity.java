package com.chao.activity;

import com.chao.utils.HttpCallbackListener;
import com.chao.utils.HttpUtil;
import com.chao.utils.Utility;
import com.chao.weather.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
private LinearLayout weatherInfoLayout;
private TextView cityNameText,publishText,weatherDespText,temp1Text,temp2Text,currentDateText;
	@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.weather);
	weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
	cityNameText=(TextView)findViewById(R.id.city_name);
	publishText=(TextView)findViewById(R.id.public_text);
	weatherDespText=(TextView)findViewById(R.id.weather_desp);
	temp1Text=(TextView)findViewById(R.id.temp1);
	temp2Text=(TextView)findViewById(R.id.temp2);
	currentDateText=(TextView)findViewById(R.id.current_data);
	String countryCode=getIntent().getStringExtra("country_code");
	if(!TextUtils.isEmpty(countryCode)){
		publishText.setText("同步中...");
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		queryWeatherCode(countryCode);
	}else{
		showWeather();
	}
}
	private void queryWeatherCode(String countryCode){
		String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		queryFromServer(address, "countryCode");
	}
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onfinish(final String response) {
				if("countryCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array=response.split("\\|");
						if(array!=null&array.length==2){
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
						showWeather();	
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
					publishText.setText("同步失败");	
					}
				});
			}
		});
	}
	private void showWeather() {
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(sp.getString("city_name", ""));
		temp1Text.setText(sp.getString("temp1", ""));
		temp2Text.setText(sp.getString("temp2", ""));
		weatherDespText.setText(sp.getString("weather_desp", ""));
		publishText.setText("今天"+sp.getString("publish_time", "")+"发布");
		currentDateText.setText(sp.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
