package com.beiwei.bracelet.fragment.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beiwei.bracelet.R;
import com.beiwei.bracelet.model.History;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private List<History> list;
    private Context context;

    public HistoryAdapter(Context context, List<History> items){
        this.context = context;
        this.list = items;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int index) {
        return list.get(index);
    }
    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        HistoryAdapter.ViewHolder holder;
        if (view==null){
            view = View.inflate(context, R.layout.history_info_item, null);
            holder = new HistoryAdapter.ViewHolder();
            holder.iswear= view.findViewById(R.id.is_wear);
            holder.date = view.findViewById(R.id.history_date);
            holder.time = view.findViewById(R.id.history_time);
            holder.wd = view.findViewById(R.id.history_wd);
            view.setTag(holder);
        }else{
            holder = (HistoryAdapter.ViewHolder) view.getTag();
        }
        History history = (History) getItem(index);
        if(history.getIsWear()==0){
            holder.iswear.setText("未佩戴");
        }else if(history.getIsWear()==1){
            holder.iswear.setText("已佩戴");
        }else if(history.getIsWear()==2){
            holder.iswear.setText("已离线");
        }
        holder.date.setText(history.getDate());
        holder.time.setText(history.getTime());
        holder.wd.setText(history.getTemperature()+"℃");
        return view;
    }

    class ViewHolder{
        TextView iswear;
        TextView date;
        TextView time;
        TextView wd;
    }
}