package com.chao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.db.WeatherDBoperation;
import com.chao.model.City;
import com.chao.model.Country;
import com.chao.model.Province;
import com.chao.utils.HttpCallbackListener;
import com.chao.utils.HttpUtil;
import com.chao.utils.Utility;
import com.chao.weather.R;

public class ChooseAreaActivity extends Activity {
	public static final int level_province = 0;
	public static final int level_city = 1;
	public static final int level_country = 2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDBoperation weatherDBo;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		if(sp.getBoolean("city_selected", false)){
			Intent it=new Intent(this,WeatherActivity.class);
			startActivity(it);
			finish();
		}
		progressDialog = new ProgressDialog(this);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		weatherDBo = WeatherDBoperation.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (currentLevel == level_province) {
					selectedProvince = provinceList.get(arg2);
					queryCities();
				} else if (currentLevel == level_city) {
					selectedCity = cityList.get(arg2);
					queryCountries();
				}else if(currentLevel==level_country){
					String countryCode=countryList.get(arg2).getCountryCode();
					Intent it=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					it.putExtra("country_code", countryCode);
					startActivity(it);
					finish();
				}
			}

		});
		queryProvinces();
	}

	private void queryProvinces() {
		provinceList = weatherDBo.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province p : provinceList) {
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = level_province;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCities() {
		cityList = weatherDBo.loadCity(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City c : cityList) {
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = level_city;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCountries() {
		countryList = weatherDBo.loadCountry(selectedCity.getId());
		if (countryList.size() > 0) {
			dataList.clear();
			for (Country c : countryList) {
				dataList.add(c.getCountryName());
			}
			adapter.notifyDataSetChanged();
			titleText.setText(selectedCity.getCityName());
			listView.setSelection(0);
			currentLevel = level_country;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onfinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(weatherDBo,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(weatherDBo, response,
							selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleCountriesResponse(weatherDBo,
							response, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type))
								queryProvinces();
							else if ("city".equals(type))
								queryCities();
							else if ("country".equals(type))
								queryCountries();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(getApplicationContext(), "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (progressDialog != null) {
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(currentLevel==level_country)
			queryCities();
		else if(currentLevel==level_city)
			queryProvinces();
		else 
			finish();
	}
}
