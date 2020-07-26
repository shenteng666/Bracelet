package com.beiwei.bracelet.fragment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.R;

import java.util.List;

public class InfoListViewAdapter extends BaseAdapter {
    private List<Member> list;
    private Context context;

    public InfoListViewAdapter(Context context, List<Member> list){
        this.context = context;
        this.list = list;
    }

    public void notifyData(List<Member> memberList){
        if(this.list != null){
            this.list=null;
            this.list = memberList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view==null){
            view = View.inflate(context, R.layout.fragment_list_item, null);
            holder = new ViewHolder();
            holder.list_num=(TextView) view.findViewById(R.id.list_num);
            holder.list_sname=(TextView) view.findViewById(R.id.list_sname);
            holder.list_sex=(TextView) view.findViewById(R.id.list_sex);
            holder.list_date=(TextView) view.findViewById(R.id.list_date);
            holder.list_classnum=(TextView) view.findViewById(R.id.list_classnum);
            holder.list_time=(TextView) view.findViewById(R.id.list_time);
            holder.list_wd=(TextView) view.findViewById(R.id.list_wd);
            holder.list_isd=(TextView) view.findViewById(R.id.list_isd);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Member member = list.get(position);
        holder.list_num.setText(member.getCode());
        holder.list_sname.setText(member.getName());
        if (member.getSex()==1){
            holder.list_sex.setText("男");
        }else if(member.getSex()==2){
            holder.list_sex.setText("女");
        }else{
            holder.list_sex.setText("未知");
        }
        holder.list_date.setText(member.getData());
        holder.list_classnum.setText(member.getPname());
        holder.list_time.setText(member.getTime());
        if(member.getTemperature()>37.2){
            holder.list_wd.setText(member.getTemperature()+"℃");
            holder.list_wd.setTextColor(Color.RED);
        }else{
            holder.list_wd.setText(member.getTemperature()+"℃");
        }

        if (member.getIsWear()==0){
            holder.list_isd.setText("未佩戴");
            holder.list_isd.setTextColor(Color.RED);
        }else if(member.getIsWear()==1){
            holder.list_isd.setText("已佩戴");
        }else if(member.getIsWear()==2){
            holder.list_isd.setText("已离线");
        }
        return view;
    }

    class ViewHolder{
        TextView list_num;
        TextView list_sname;
        TextView list_sex;
        TextView list_date;
        TextView list_classnum;
        TextView list_time;
        TextView list_wd;
        TextView list_isd;
    }
}
