package com.beiwei.bracelet.utils;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beiwei.bracelet.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class GetInfoUtil {
    //服务器地址
    public static final String SERVICE_URL="http:\\192.168.5.138:81";
    private static String lanauge="";
    /**
     * 获取安卓系统的语言
     */
    public static String getLanguage(){
        Locale locale = Locale.getDefault();
        lanauge = locale.getLanguage();
//        String country = locale.getCountry();
        if(lanauge.equals("ja")){
            lanauge="jp";
        }
        return lanauge;
    }


}
