package com.mzj.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存数据到文件的工具类
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {

	private Context context;
	public SharedPreferencesUtils(Context context){
		this.context = context;
	}

	public void putData(String key,String value){
		SharedPreferences mySharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putString(key, value);
		//提交当前数据
		editor.commit();
	}
	public void putData(String key,int value){
		SharedPreferences mySharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putInt(key, value);
		//提交当前数据
		editor.commit();
	}

	public void putData(String key,boolean value){
		SharedPreferences mySharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putBoolean(key, value);
		//提交当前数据
		editor.commit();
	}
	public void putData(String key,float value){
		SharedPreferences mySharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putFloat(key, value);
		//提交当前数据
		editor.commit();
	}
	public void putData(String key,Long value){
		SharedPreferences mySharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putLong(key, value);
		//提交当前数据
		editor.commit();
	}
	public String getData(String key){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		String value =sharedPreferences.getString(key, "");
		return value;
	}
	public String getString(String key){
		return getString( key, "");
	}
	public String getString(String key,String value){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		return sharedPreferences.getString(key, "");
	}
	public int getInt(String key){
		return getInt(key, 0);
	}
	public int getInt(String key,int value){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		return sharedPreferences.getInt(key, value);

	}
	public boolean getBoole(String key){
		return getBoole( key, false);
	}
	public boolean getBoole(String key,boolean value){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		return sharedPreferences.getBoolean(key, value);
	}
	public float getFloat(String key){
		return getFloat(key, 0);
	}
	public float getFloat(String key,float values){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		return sharedPreferences.getFloat(key, values);
	}
	public Long getLong(String key){
		return getLong(key, (long) 0);
	}
	public Long getLong(String key,Long value){
		SharedPreferences sharedPreferences= context.getSharedPreferences("userdata",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值

		return sharedPreferences.getLong(key, value);
	}
}
