package com.beiwei.bracelet.fragment.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.beiwei.bracelet.activity.InfoListActivity;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.fragment.adapter.InfoListViewAdapter;
import com.beiwei.bracelet.utils.STUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FourFragment extends InfoCommonFragment {
    private List<Member> objList;
    private List<Member> batteryList=new ArrayList<>();;//低电量集合
    private ListView listView;
    private InfoListViewAdapter adapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_four_low, container, false);

        getMemberList();

        setAdapterInfo(objList,batteryList);
        return v;
    }
    /*
     *响应通讯调用的方法
     */
    @Subscribe
    public void onEvent(List<Member> memberList) {
        this.objList=memberList;
//        batteryList=new ArrayList<>();
        setAdapterInfo(objList,batteryList);

    }
    public void getMemberList(){
        this.objList= InfoListActivity.memberList;
//        batteryList=new ArrayList<>();
    }

    public void setAdapterInfo(List<Member> objList1,List<Member> batteryList1){
        if (batteryList1.size()>0){
            batteryList1.clear();
        }
        //判断集合中每个人员的电量
        for (int i=0;i<objList1.size();i++) {
            Member member = objList1.get(i);
            if (member.getBattery() < 30) {
                batteryList1.add(member);
            }
        }
        adapter = new InfoListViewAdapter(getActivity(), batteryList1);
        adapter.notifyData(batteryList1);
        listView = (ListView) v.findViewById(R.id.lv_expense);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member item = (Member) parent.getItemAtPosition(position);
//                STUtil.getHistoryInfo(getActivity(),item);
                getHistoryInfo(getActivity(),item);
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}