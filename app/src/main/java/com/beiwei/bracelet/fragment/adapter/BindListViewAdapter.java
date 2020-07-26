package com.beiwei.bracelet.fragment.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.beiwei.bracelet.dialog.BindDialogIdentify;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.R;

import java.util.List;

/**
 * 自定义Adapter,
 */
public class BindListViewAdapter extends BaseAdapter {
    private List<Member> list;
    private Context context;
    private MyClickListener mListener;

    public BindListViewAdapter(Context context, List<Member> list,MyClickListener listener){
        this.context = context;
        this.list = list;
        mListener=listener;
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
    public Object getItem(int index) {
        return list.get(index);
    }
    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view==null){
            view = View.inflate(context, R.layout.fragment_bind_item, null);
            holder = new ViewHolder();
            holder.bind_num = view.findViewById(R.id.num);
            holder.bind_sname = view.findViewById(R.id.sname);
            holder.bind_sid = view.findViewById(R.id.sid);
            holder.bind_classn = view.findViewById(R.id.classn);
            holder.btn = view.findViewById(R.id.btn);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        //赋值
        Member p = list.get(index);
        holder.bind_num.setText(p.getCode());
        holder.bind_sname.setText(p.getName());
        holder.bind_sid.setText(p.getDevmac());
        holder.bind_classn.setText(p.getPname());
        int type = p.getIsWear();
        if (type==0){
            holder.btn.setText("绑定设备");
        }else{
            holder.btn.setText("更换");
        }
        holder.btn.setOnClickListener(mListener);
        holder.btn.setTag(index);
//        //	利用回调的原理，来对按钮进行监听
//        holder.btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context ct = v.getContext();
//                //扫描蓝牙
//                STUtil.st.ScanBluetooth(v.getContext(),p.getId());
//            }
//        });
        return view;
    }

    class ViewHolder{
        TextView bind_num;
        TextView bind_sname;
        TextView bind_sid;
        TextView bind_classn;
        Button btn;
    }
    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener implements View.OnClickListener {
        /**
         * 基类的onClick方法
         */
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }
        public abstract void myOnClick(int position, View v);
    }


}
