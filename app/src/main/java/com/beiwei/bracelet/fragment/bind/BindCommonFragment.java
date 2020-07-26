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
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.activity.BindListActivity;
import com.beiwei.bracelet.dialog.BindDialogFail;
import com.beiwei.bracelet.dialog.BindDialogIdentify;
import com.beiwei.bracelet.dialog.BindDialogSuccess;
import com.beiwei.bracelet.fragment.adapter.BindListViewAdapter;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.model.callback.CallBack;
import com.beiwei.bracelet.utils.Constants;
import com.beiwei.bracelet.utils.DialogUtil;
import com.beiwei.bracelet.utils.GetInfoUtil;
import com.beiwei.bracelet.utils.HttpUtils;
import com.beiwei.bracelet.utils.SPUtil;
import com.beiwei.bracelet.utils.STUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * fragment父级，扫描蓝牙，绑定，
 * 继承了最上级Fragment
 */
public class BindCommonFragment extends Fragment {
    //蓝牙
    public BluetoothAdapter bAdapter;
    public BluetoothManager bManager;
    //请求接口参数
    public RequestQueue queue;
    public String language;
    public String url;
    public Long uid;//用户id
    public String mac;//设备mac地址
    public Map<String, String> map;
    //定时任务
    public Timer timer;
    public TimerTask task;
    public int count;
    //组件
    public BindDialogIdentify dialog_identify;//识别弹窗
    public BindDialogSuccess dialog_success;//失败弹窗
    public BindDialogFail dialog_fail;//成功视图
    public Button btn_sure,dialog_btn_retry;//扫描成功绑定设备按钮
    public TextView ms,code;
    //当前frament使用
    private Context context;
    private BindListViewAdapter bind_list_adpter;
    public ListView  bind_listview;
    private List<Member> memberList;
    private SPUtil sp=SPUtil.instance;


    public void init(Context ct,BindListViewAdapter adapter, ListView listView){
        this.bind_listview=listView;
        this.bind_list_adpter=adapter;
        //获取蓝牙的组件对象
        this.bAdapter= BindListActivity.bla.bluetoothAdapter;
        this.bManager=BindListActivity.bla.bluetoothManager;
        this.context=ct;
    }


    public BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (rssi > -35  && device.getAddress() != null) {
                stopTimer();
                bAdapter.stopLeScan(mLeScanCallback);
                String address = device.getAddress();
                mac = STUtil.getConvert(address);
                //关闭识别弹窗
                dialog_identify.dismiss();
                //打开识别成功弹窗
                dialog_success= DialogUtil.getIdentifySuccess(context);
                //获取组件
                code = (TextView) dialog_success.findViewById(R.id.dialog_mac_code);
                btn_sure = dialog_success.findViewById(R.id.dialig_btn_sure);
                //获取系统语言
                language = GetInfoUtil.getLanguage();
                //1.将mac地址渲染到弹窗上，
                code.setText(mac);
                //2调用接口，绑定设备
                btn_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        url =Constants.HOST + "/app_user/bind";
                        map = new HashMap<String, String>();
                        map.put("mac", mac);
                        map.put("uid", uid.toString());
                        map.put("lang", language);
                        HttpUtils.submitPost(url, map, new CallBack() {
                            @Override
                            public <T> void success(T response) {
                                try {
                                    JSONObject json = new JSONObject((String) response);
                                    if ("ok".equals(json.getString("state"))) {
                                        //关闭成功弹框，提示信息
                                        dialog_success.dismiss();
                                        //传入通讯变更时用的数据
                                        refresh();
                                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void fail(String msg) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else {
                Log.i("info","lost:name:"+device.getName()+",蓝牙设备rssi======="+rssi);
            }
        }
    };

    /**
     * 倒计时
     */
    public void startTimer(){
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

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what<=0){//小于0，检测失败
                stopTimer();
                bAdapter.stopLeScan(mLeScanCallback);
                //打开失败弹窗
                dialog_fail=DialogUtil.getIdentifyFail(context);
                dialog_btn_retry =(Button)dialog_fail.findViewById(R.id.dialog_btn_retry);
                //绑定重试 按钮
                dialog_btn_retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //开始扫描,倒计时开始,关闭识别失败弹窗，
                        bAdapter.startLeScan(mLeScanCallback);
                        dialog_identify.show();
                        startTimer();
                        dialog_fail.dismiss();
                    }
                });
                dialog_identify.dismiss();
            }else{
                ms.setText("("+count+"s)");
            }
        }
    };

    public void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void refresh(){
        url= Constants.HOST +"/app_user/getUserList";
        map =new HashMap<String, String>();
        map.put("pid", sp.getPid(context).toString());
        map.put("lang",language);
        HttpUtils.submitPost(url, map, new CallBack() {
            @Override
            public <T> void success(T response) {
                try {
                    JSONObject json = new JSONObject((String) response);
                    if("ok".equals(json.getString("state"))){
                        JSONArray array = json.getJSONArray("obj");
                        memberList= new ArrayList<Member>();
                        for (int i=0;i<array.length();i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            Member member = new Member(jsonObject.getLong("id"),jsonObject.getString("name")
                                    ,jsonObject.getInt("sex"),jsonObject.getString("code")
                                    ,jsonObject.getString("pname"),jsonObject.getString("time")
                                    ,jsonObject.getString("date"),jsonObject.getDouble("temperature")
                                    ,jsonObject.getInt("isWear"),jsonObject.getString("devmac")
                                    ,jsonObject.getInt("battery"));
                            memberList.add(member);
                        }
                        //传入通讯变更时用的数据
                        EventBus.getDefault().post(memberList);
                        //移除布局初始布局文件
                    }else{
                        Toast.makeText(context,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void fail(String msg) {
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
