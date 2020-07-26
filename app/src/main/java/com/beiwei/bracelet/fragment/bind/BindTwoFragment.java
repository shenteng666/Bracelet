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

import com.beiwei.bracelet.activity.BindListActivity;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.fragment.adapter.BindListViewAdapter;
import com.beiwei.bracelet.utils.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BindTwoFragment extends BindCommonFragment {

    private List<Member> objList;
    private  List<Member>  unBindList=new ArrayList<>();;//未绑定设备人员集合
    private ListView listView;
    private BindListViewAdapter adapter;
    private Context context;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context=getActivity();
        //注册通讯
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_two_unbind, container, false);

        getMemberList();

        setAdapterInfo(objList,unBindList);
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
//        unBindList=new ArrayList<>();

        setAdapterInfo(objList,unBindList);
    }

    //初始获取成员信息
    public void getMemberList(){
        this.objList= BindListActivity.memberList;
//        unBindList=new ArrayList<>();
    }

    public void setAdapterInfo(List<Member> objList1,List<Member> unBindList1){
        if (unBindList1.size()>0){
            unBindList1.clear();
        }
        //获取集合中未绑定的设备
        for (int i=0;i<objList1.size();i++){
            Member member = objList1.get(i);
            if (member.getIsWear()==0){
                unBindList1.add(member);
            }
        }
        adapter=new BindListViewAdapter(getActivity(),unBindList1,mListener);
        adapter.notifyData(unBindList1);
        listView = (ListView) v.findViewById(R.id.bind_expense);
        listView.setAdapter(adapter);
    }

    /**
     * 实现类，响应按钮点击事件
     */
    private BindListViewAdapter.MyClickListener mListener = new BindListViewAdapter.MyClickListener() {
        @Override
        public void myOnClick(int index, View view) {
            Member member = unBindList.get(index);
            uid=member.getId();
            if (member!=null){
                //获取蓝牙的组件对象
//                bAdapter=BindListActivity.bla.bluetoothAdapter;
//                bManager=BindListActivity.bla.bluetoothManager;
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
