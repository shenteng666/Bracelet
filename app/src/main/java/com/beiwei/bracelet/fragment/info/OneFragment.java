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

import java.util.List;



public class OneFragment extends InfoCommonFragment {

    private List<Member> objList;
    private ListView listView;
    private InfoListViewAdapter adapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        v = inflater.inflate(R.layout.fragment_one_all, container, false);

        getMemberList();

        adapter = new InfoListViewAdapter(getActivity(), objList);
        adapter.notifyData(objList);
        listView = (ListView) v.findViewById(R.id.lv_expense);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member item = (Member) parent.getItemAtPosition(position);
                STUtil.getHistoryInfo(getActivity(),item);
            }
        });
        return v;
    }
    /*
     *响应通讯调用的方法
     */
    @Subscribe
    public void onEvent(List<Member> memberList) {
        this.objList=memberList;
        adapter = new InfoListViewAdapter(getActivity(), objList);
        adapter.notifyData(objList);
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

    public void getMemberList(){
        this.objList= InfoListActivity.memberList;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}