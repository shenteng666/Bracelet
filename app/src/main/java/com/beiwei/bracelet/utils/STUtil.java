package com.beiwei.bracelet.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.dialog.HistoryListDialog;
import com.beiwei.bracelet.model.History;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.fragment.adapter.HistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class STUtil {

    public static STUtil st = new STUtil();
    private SPUtil sp=SPUtil.instance;
    private static RequestQueue queue;
    private static String language;
    private static String url;
    private static ArrayList<History> historyList;
    /**
     * 获取选择的用户的历史记录
     */
    public static void getHistoryInfo(Context context, Member m) {
        //获取系统语言
        language = GetInfoUtil.getLanguage();
        queue = Volley.newRequestQueue(context);
        url = GetInfoUtil.SERVICE_URL + "/app_user/getUserListLog";
        StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    JSONObject json = new JSONObject(res);
                    if ("ok".equals(json.getString("state"))) {
                        JSONArray array = json.getJSONArray("obj");
                        historyList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            History history = new History(obj.getInt("isWear"), obj.getString("temperature")
                                    , obj.getString("time"), obj.getString("date"));
                            historyList.add(history);
                        }
                        if (historyList.size() > 0) {
                            //获取历史记录弹窗
                            HistoryListDialog historyListDialog = DialogUtil.getHistoryListDialog(context);
                            //获取listView
                            ListView listView = historyListDialog.findViewById(R.id.history_info_listview);
                            //调用适配器
                            HistoryAdapter adapter = new HistoryAdapter(context, historyList);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(context, "暂无测温记录", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "请求超时，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("mac", m.getDevmac());
                map.put("lang", language);
                return map;
            }
        };
        queue.add(str);
    }

    //将一个字符串字母转换成大写
    public static String getConvert(String str) {
        String res = str.toUpperCase();
        return res;
    }
}