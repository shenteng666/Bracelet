package com.beiwei.bracelet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;


public class SPUtil {
	public static String APP_NAME = "SHOUHUAN";
	public static final SPUtil instance = new SPUtil();

	public void setId(Context context,Long uid){
		SharedPreferences settings = context.getSharedPreferences(APP_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("uid", uid);
		editor.commit();
	}
		
	public Long getId(Context context){
		SharedPreferences settings = context.getSharedPreferences(
					SPUtil.APP_NAME, 0);
		return settings.getLong("uid",0L);
	}

	public void setName(Context context,String name){
		SharedPreferences settings = context.getSharedPreferences(APP_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("username", name);
		editor.commit();
	}

	public void setPid(Context context,Long pid){
		SharedPreferences settings = context.getSharedPreferences(APP_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("pid", pid);
		editor.commit();
	}
	public Map<String, ?> getAll(Context context){
		SharedPreferences settings = context.getSharedPreferences(
				SPUtil.APP_NAME, 0);
		return settings.getAll();
	}
	public Long getPid(Context context){
		SharedPreferences settings = context.getSharedPreferences(
				SPUtil.APP_NAME, 0);
		return settings.getLong("pid",0L);
	}

	public String getName(Context context){
		SharedPreferences settings = context.getSharedPreferences(
				SPUtil.APP_NAME, 0);
		return settings.getString("username", "");
	}

	public void clearAll(Context context){
		SharedPreferences settings = context.getSharedPreferences(
				SPUtil.APP_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("uid");
		editor.remove("username");
		editor.remove("pid");
		editor.commit();
	}


}
