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

public class ThreeFragment extends InfoCommonFragment {

    private List<Member> objList;
    private  List<Member> removeList=new ArrayList<>();;//摘除人员集合
    private ListView listView;
    private InfoListViewAdapter adapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        v = inflater.inflate(R.layout.fragment_three_remove, container, false);

        getMemberList();

        setAdapterInfo(objList,removeList);

        return v;
    }

    /*
     *响应通讯调用的方法
     */
    @Subscribe
    public void onEvent(List<Member> memberList) {
        this.objList=memberList;
//        removeList=new ArrayList<>();
        setAdapterInfo(objList,removeList);
    }

    public void getMemberList(){
        this.objList= InfoListActivity.memberList;
//        removeList=new ArrayList<>();
    }

    public void setAdapterInfo(List<Member> objList1,List<Member> removeList1){
        if (removeList1.size()>0){
            removeList1.clear();
        }
        //判断是否佩戴
        for (int i=0;i<objList1.size();i++){
            Member member = objList1.get(i);
            if (member.getIsWear()==0){
                removeList1.add(member);
            }
        }
        adapter = new InfoListViewAdapter(getActivity(), removeList1);
        adapter.notifyData(removeList1);
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