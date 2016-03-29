package com.chao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDB extends SQLiteOpenHelper {


	public WeatherDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create_province="create table if not exist Province(id integer primary key autoincrement,province_name text,province_code text)";	
		String create_city="create table if not exist City(id integer primary key autoincrement,city_name text,city_code text,province_id integer)";	
		String create_country="create table if not exist Country(id integer primary key autoincrement,country_name text,country_code text,city_id integer)";
		db.execSQL(create_province);
		db.execSQL(create_city);
		db.execSQL(create_country);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
