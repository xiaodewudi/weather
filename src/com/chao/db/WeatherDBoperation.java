package com.chao.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chao.model.City;
import com.chao.model.Country;
import com.chao.model.Province;

public class WeatherDBoperation {
	private static final String DBname = "weather.db";// Êý¾Ý¿âÃû³Æ
	private static final int version = 1;
	private static WeatherDBoperation weatherDBo;
	private SQLiteDatabase db;
	private static final String key="b992808ccbb541a0b8693a45e04f232b";

	private WeatherDBoperation(Context context) {
		WeatherDB weatherDB = new WeatherDB(context, DBname, null, version);
		db = weatherDB.getWritableDatabase();
	}

	public synchronized static WeatherDBoperation getInstance(Context context) {
		if (weatherDBo == null)
			weatherDBo = new WeatherDBoperation(context);
		return weatherDBo;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues v = new ContentValues();
			v.put("province_name", province.getProvinceName());
			v.put("province_code", province.getProvinceCode());
			db.insert("Province", null, v);
		}
	}

	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		Cursor cur = db.query("Province", null, null, null, null, null, null);
		if (cur.moveToFirst()) {
			do {
				Province p = new Province();
				p.setId(cur.getInt(cur.getColumnIndex("id")));
				p.setProvinceName(cur.getString(cur
						.getColumnIndex("province_name")));
				p.setProvinceCode(cur.getString(cur
						.getColumnIndex("province_code")));
				list.add(p);
			} while (cur.moveToNext());
		}
		if (cur != null)
			cur.close();
		return list;
	}

	public void saveCity(City c) {
		if (c != null) {
			ContentValues v = new ContentValues();
			v.put("city_name", c.getCityName());
			v.put("city_code", c.getCityCode());
			v.put("province_id", c.getProvinceId());
			db.insert("City", null, v);
		}
	}

	public List<City> loadCity(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cur = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cur.moveToFirst()) {
			do {
				City c = new City();
				c.setId(cur.getInt(cur.getColumnIndex("id")));
				c.setCityName(cur.getString(cur.getColumnIndex("city_name")));
				c.setCityCode(cur.getString(cur.getColumnIndex("city_code")));
				c.setProvinceId(provinceId);
				list.add(c);
			} while (cur.moveToNext());
		}
		if (cur != null)
			cur.close();
		return list;
	}

	public void saveCountry(Country c) {
		if (c != null) {
			ContentValues v = new ContentValues();
			v.put("country_name", c.getCountryName());
			v.put("country_code", c.getCountryCode());
			v.put("city_id", c.getCityId());
			db.insert("Country", null, v);
		}
	}

	public List<Country> loadCountry(int cityId) {
		List<Country> list = new ArrayList<Country>();
		Cursor cur = db.query("Country", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cur.moveToFirst()) {
			do {
				Country c = new Country();
				c.setId(cur.getInt(cur.getColumnIndex("id")));
				c.setCountryName(cur.getString(cur
						.getColumnIndex("country_name")));
				c.setCountryCode(cur.getString(cur
						.getColumnIndex("country_code")));
				c.setCityId(cityId);
				list.add(c);
			} while (cur.moveToNext());
		}
		if (cur != null)
			cur.close();
		return list;
	}
}
