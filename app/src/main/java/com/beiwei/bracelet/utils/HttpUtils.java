package com.beiwei.bracelet.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.model.callback.CallBack;

import java.util.Map;

public class HttpUtils {
    private static RequestQueue queue;
    public static void submitPost(String url, final Map<String, String> para, final CallBack callBack) {
        Log.i("info","请求地址:"+url);
        queue = getQueue();
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.i("info","数据返回:"+result);
                if(result.equals("")){
                    callBack.fail("数据请求失败");
                }else{
                    callBack.success(result);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("info","连接请求失败");
                callBack.fail("网络请求失败");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return para;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(1000*10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public static RequestQueue getQueue(){
        if(queue == null){
            queue = Volley.newRequestQueue(AppContext.getInstance().getApplicationContext());
        }
        return queue;
    }
}
