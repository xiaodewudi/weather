package com.chao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.chao.db.WeatherDBoperation;
import com.chao.model.City;
import com.chao.model.Country;
import com.chao.model.Province;

public class Utility {
	public static boolean handleProvincesResponse(
			WeatherDBoperation weatherDBo, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String c : allProvinces) {
					String[] array = c.split("\\|");
					Province province = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					weatherDBo.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean handleCitiesResponse(WeatherDBoperation weatherDBo,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					weatherDBo.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean handleCountriesResponse(
			WeatherDBoperation weatherDBo, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountries = response.split(",");
			if (allCountries != null && allCountries.length > 0) {
				for (String c : allCountries) {
					Country country = new Country();
					String[] array = c.split("\\|");
					country.setCountryName(array[1]);
					country.setCountryCode(array[0]);
					country.setCityId(cityId);
					weatherDBo.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}

	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject job = new JSONObject(response);
			JSONObject weatherInfo = job.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyƒÍM‘¬d»’", Locale.CHINA);
		SharedPreferences.Editor ed = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		ed.putBoolean("city_selected", true);
		ed.putString("city_name", cityName);
		ed.putString("weather_code", weatherCode);
		ed.putString("temp1", temp1);
		ed.putString("temp2", temp2);
		ed.putString("weather_desp", weatherDesp);
		ed.putString("publish_time", publishTime);
		ed.putString("current_date", sdf.format(new Date()));
		ed.commit();
	}
}
