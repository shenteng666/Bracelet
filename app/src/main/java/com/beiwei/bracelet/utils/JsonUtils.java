package com.beiwei.bracelet.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtils {

	public static String getJson(Map<String,Object> map){
		JSONObject job = new JSONObject();
		try {
			for (String key : map.keySet()){
				job.put(key,map.get(key));
			}
		}catch (JSONException e){
			return null;
		}
		return job.toString();
	}

	public static String getJsonByMoney(Map<String,Object> map,Map<Integer,Object> map1){
		JSONObject job = new JSONObject();
		try {
			for (String key : map.keySet()){
				job.put(key,map.get(key));
			}
			JSONArray ja = new JSONArray();
			for (Integer key : map1.keySet()){
				JSONObject jj = new JSONObject();
				//jj.put(""+key,map1.get(key));
				jj.put("t",key);
				jj.put("m",map1.get(key));
				ja.put(jj);
			}
			job.put("arr",ja);
		}catch (JSONException e){
			return null;
		}
		return job.toString();
	}
	
}
