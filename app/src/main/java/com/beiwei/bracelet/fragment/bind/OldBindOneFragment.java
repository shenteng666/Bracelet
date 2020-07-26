package com.beiwei.bracelet.fragment.bind;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.activity.BindListActivity;
import com.beiwei.bracelet.dialog.BindDialogFail;
import com.beiwei.bracelet.dialog.BindDialogIdentify;
import com.beiwei.bracelet.dialog.BindDialogSuccess;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.fragment.adapter.BindListViewAdapter;
import com.beiwei.bracelet.utils.DialogUtil;
import com.beiwei.bracelet.utils.GetInfoUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OldBindOneFragment extends Fragment {
    //蓝牙
    private BluetoothAdapter bAdapter;
    private BluetoothManager bManager;
    //请求接口参数
    private RequestQueue queue;
    private String language;
    private String url;
    private Long uid;//用户id
    private String mac;//设备mac地址
    //定时任务
    private Timer timer;
    private TimerTask task;
    private int count;
    //组件
    private BindDialogIdentify dialog_identify;//识别弹窗
    private BindDialogSuccess dialog_success;//失败弹窗
    private BindDialogFail dialog_fail;//成功视图
    private Button btn_sure,dialog_btn_retry;//扫描成功绑定设备按钮
    private TextView ms,code;
    //当前frament使用
    private List<Member> objList=new ArrayList();
    private ListView listView;
    private BindListViewAdapter adapter;
    private View v;
    private Context context;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (rssi > -35  && device.getAddress() != null) {
                stopTimer();
                bAdapter.stopLeScan(mLeScanCallback);
                String address = device.getAddress();
                mac = getConvert(address);
                //关闭识别弹窗
                dialog_identify.dismiss();
                //打开识别成功弹窗
                dialog_success=DialogUtil.getIdentifySuccess(context);
                //将mac地址渲染到弹窗上，
                code = (TextView) dialog_success.findViewById(R.id.dialog_mac_code);
                code.setText(mac);
                //1.获取组件
                btn_sure = dialog_success.findViewById(R.id.dialig_btn_sure);
                //2调用接口
                btn_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取系统语言
                        language = GetInfoUtil.getLanguage();
                        queue = Volley.newRequestQueue(context);
                        url = GetInfoUtil.SERVICE_URL + "/app_user/bind";
                        StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {
                                try {
                                    JSONObject json = new JSONObject(res);
                                    if ("ok".equals(json.getString("state"))) {
                                        //关闭成功弹框，提示信息
                                        dialog_success.dismiss();
                                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                map.put("mac", mac);
                                map.put("uid", uid.toString());
                                map.put("lang", language);
                                return map;
                            }
                        };
                        queue.add(str);
                    }
                });
            }else {
                Log.i("info","lost:name:"+device.getName()+",蓝牙设备rssi======="+rssi);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //赋值给全局变量
        this.context=getActivity();
        //注册通讯
        EventBus.getDefault().register(this);

        v = inflater.inflate(R.layout.fragment_one_bind, container, false);

        getMemberList();

        //适配器
        adapter=new BindListViewAdapter(getActivity(),objList,mListener);
        adapter.notifyData(objList);
        listView = (ListView) v.findViewById(R.id.bind_expense);
        listView.setAdapter(adapter);
        return v;
    }

    /*
     *响应通讯调用的方法
     */
    @Subscribe
    public void onEvent(List<Member> memberList) {
        this.objList=memberList;
        adapter=new BindListViewAdapter(getActivity(),objList,mListener);
        adapter.notifyData(objList);
        listView = (ListView) v.findViewById(R.id.bind_expense);
        listView.setAdapter(adapter);
    }

    //初始获取成员信息
    public void getMemberList(){
        this.objList= BindListActivity.memberList;
    }

    /**
     * 实现类，响应按钮点击事件
     */
    private BindListViewAdapter.MyClickListener mListener = new BindListViewAdapter.MyClickListener() {
        @Override
        public void myOnClick(int index, View view) {
            Member member = objList.get(index);
            uid=member.getId();
            if (member!=null){
                //获取蓝牙的组件对象
                bAdapter=BindListActivity.bla.bluetoothAdapter;
                bManager=BindListActivity.bla.bluetoothManager;
                //打开识别设备弹出框
                dialog_identify= DialogUtil.getBindIdentify(getActivity());
                ms = dialog_identify.findViewById(R.id.seconds);
                Log.i("info","开始扫描");
                //开始扫描，并进行倒计时
                bAdapter.startLeScan(mLeScanCallback);
                startTimer();
            }else{
                Toast.makeText(context, "获取用户信息失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 倒计时
     */
    private void startTimer(){
        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        count=10;
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(count--);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what<=0){//小于0，检测失败
                stopTimer();
                bAdapter.stopLeScan(mLeScanCallback);
                dialog_identify.dismiss();
                //打开时白弹窗
                dialog_fail=DialogUtil.getIdentifyFail(context);
                dialog_btn_retry =(Button)dialog_fail.findViewById(R.id.dialog_btn_retry);
                //绑定重试 按钮
                dialog_btn_retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //开始扫描,倒计时开始,关闭，
                        bAdapter.startLeScan(mLeScanCallback);
                        dialog_identify.show();
                        startTimer();
                        dialog_fail.dismiss();
                    }
                });

            }else{
                ms.setText("("+count+"s)");
            }
        }
    };

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //将一个字符串字母转换成大写
    private String getConvert(String str) {
        String res = str.toUpperCase();
        return res;
    }

}
