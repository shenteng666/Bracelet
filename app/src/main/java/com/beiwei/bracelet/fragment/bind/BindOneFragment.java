package com.beiwei.bracelet.fragment.bind;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.beiwei.bracelet.R;
import com.beiwei.bracelet.activity.BindListActivity;
import com.beiwei.bracelet.fragment.adapter.BindListViewAdapter;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.utils.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BindOneFragment extends BindCommonFragment {
    //当前frament使用
    private List<Member> objList=new ArrayList();
    private ListView listView;
    private BindListViewAdapter adapter;
    private View v;
    private Context context;


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

        //初始化父类的context
        init(context,adapter,listView);
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
//                bAdapter=BindListActivity.bla.bluetoothAdapter;
//                bManager=BindListActivity.bla.bluetoothManager;
                //进行扫描前，在判断一次蓝牙是否开启
                if(!bAdapter.isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1);
                    return;
                }else{
                    //打开识别设备弹出框
                    dialog_identify= DialogUtil.getBindIdentify(getActivity());
                    ms = dialog_identify.findViewById(R.id.seconds);
                    Log.i("info","开始扫描");
                    //开始扫描，并进行倒计时
                    bAdapter.startLeScan(mLeScanCallback);
                    startTimer();
                }
            }else{
                Toast.makeText(context, "获取用户信息失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
