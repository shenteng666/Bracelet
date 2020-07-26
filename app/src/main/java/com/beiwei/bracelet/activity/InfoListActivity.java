package com.beiwei.bracelet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.fragment.info.FourFragment;
import com.beiwei.bracelet.fragment.info.OneFragment;
import com.beiwei.bracelet.fragment.info.ThreeFragment;
import com.beiwei.bracelet.fragment.info.TwoFragment;
import com.beiwei.bracelet.model.callback.CallBack;
import com.beiwei.bracelet.utils.Constants;
import com.beiwei.bracelet.utils.GetInfoUtil;
import com.beiwei.bracelet.utils.HttpUtils;
import com.beiwei.bracelet.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InfoListActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayout,info_list;
    private TextView all_info,fever_info,remove_info,low_battery_info,back_view;
    private Spinner spinner;
    private Button search_btn;
    private ViewPager vp;
    private OneFragment oneFragment;
    private TwoFragment twoFragment;
    private ThreeFragment threeFragment;
    private FourFragment fourFragment;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    private SPUtil sp=SPUtil.instance;
    private RequestQueue queue;
    private String language;
    private String url;
    private Long pid;//查询的渠道id
    private Map<String, String> map;
    private ArrayList<String> spinnerList = new ArrayList<String>();//创建数组列表 用来存放以后要显示的内容
    private  Map<String,Long> id_map=new HashMap<>();;//根据班级名称存储 id
    public static ArrayList<Member> memberList;//存放成员信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infolist);
        //初始化页面组件
        initViews();
        //初始化下拉菜单
        initDataSpinner();
    }

    /**
     * 点击头部Text 动态修改ViewPager的内容
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId==R.id.all_info){
            vp.setCurrentItem(0, true);
        }else if (viewId==R.id.fever_info){
            vp.setCurrentItem(1, true);
        }else if (viewId==R.id.remove_info){
            vp.setCurrentItem(2, true);
        }else if (viewId==R.id.low_battery_info){
            vp.setCurrentItem(3, true);
        }else if(viewId==R.id.back_view){
            Intent intent = new Intent(InfoListActivity.this,HomeActivity.class);
            startActivity(intent);
        }else if (viewId==R.id.search_btn){
            //点击搜索按钮查询查询数据
            url= Constants.HOST +"/app_user/getUserList";
            map =new HashMap<String, String>();
            map.put("pid", pid.toString());
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
                            info_list.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(InfoListActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void fail(String msg) {
                    Toast.makeText(InfoListActivity.this,"请先选择班级",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * 初始化Frament
     */
    private void initFrament(){
        vp = (ViewPager) findViewById(R.id.mainViewPager);
        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        threeFragment = new ThreeFragment();
        fourFragment = new FourFragment();
        //给FragmentList添加数据
        mFragmentList.add(oneFragment);
        mFragmentList.add(twoFragment);
        mFragmentList.add(threeFragment);
        mFragmentList.add(fourFragment);

        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp.setOffscreenPageLimit(4);//ViewPager的缓存为2帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧

        all_info.setTextColor(Color.parseColor("#1ba0e1"));
        fever_info.setTextColor(Color.parseColor("#000000"));
        remove_info.setTextColor(Color.parseColor("#000000"));
        low_battery_info.setTextColor(Color.parseColor("#000000"));

        //ViewPager的监听事件
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                changeTextColor(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        });
    }

    /**
     * 查询下拉菜单
     */
    private void initDataSpinner(){
        url= Constants.HOST+"/app_user/getArea";
        map =new HashMap<String, String>();
        map.put("id", sp.getId(InfoListActivity.this).toString());
        map.put("lang",language);
        HttpUtils.submitPost(url, map, new CallBack() {
            @Override
            public <T> void success(T response) {
                try {
                    JSONObject json = new JSONObject((String) response);
                    if("ok".equals(json.getString("state"))){
                        JSONArray array = json.getJSONArray("obj");
                        spinnerList.add("请选择");
                        for (int i=0;i<array.length();i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            spinnerList.add(jsonObject.getString("name"));
                            id_map.put(jsonObject.getString("name"),jsonObject.getLong("id"));
                        }
                        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(InfoListActivity.this,android.R.layout.simple_spinner_item, spinnerList);
                        spinner.setAdapter(_Adapter);
                        //点击触发
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
                                //选中时根据id赋值
                                pid = id_map.get(spinnerList.get(pos));
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }else{
                        Toast.makeText(InfoListActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void fail(String msg) {
                Toast.makeText(InfoListActivity.this,"请求超时，请稍后重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class FragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    /**
     * 由ViewPager的滑动修改头部导航Text的颜色
     * @param position
     */
    private void changeTextColor(int position) {
        if (position == 0) {
            all_info.setTextColor(Color.parseColor("#1ba0e1"));
            fever_info.setTextColor(Color.parseColor("#000000"));
            remove_info.setTextColor(Color.parseColor("#000000"));
            low_battery_info.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            fever_info.setTextColor(Color.parseColor("#1ba0e1"));
            all_info.setTextColor(Color.parseColor("#000000"));
            remove_info.setTextColor(Color.parseColor("#000000"));
            low_battery_info.setTextColor(Color.parseColor("#000000"));
        }else if (position == 2) {
            remove_info.setTextColor(Color.parseColor("#1ba0e1"));
            all_info.setTextColor(Color.parseColor("#000000"));
            fever_info.setTextColor(Color.parseColor("#000000"));
            low_battery_info.setTextColor(Color.parseColor("#000000"));
        }else if (position == 3) {
            low_battery_info.setTextColor(Color.parseColor("#1ba0e1"));
            all_info.setTextColor(Color.parseColor("#000000"));
            remove_info.setTextColor(Color.parseColor("#000000"));
            fever_info.setTextColor(Color.parseColor("#000000"));
        }
    }

    /**
     * 初始化布局View
     */
    private void initViews() {
        //隐藏自带的标题菜单
        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        TextView text = findViewById(R.id.view_counter_buttons_1).findViewById(R.id.title_info);
        text.setText("实时监测");

        //初始化控件
        all_info = (TextView) findViewById(R.id.all_info);
        fever_info = (TextView) findViewById(R.id.fever_info);
        remove_info = (TextView) findViewById(R.id.remove_info);
        low_battery_info = (TextView) findViewById(R.id.low_battery_info);
        back_view= (TextView)findViewById(R.id.view_counter_buttons_1).findViewById(R.id.back_view);
        search_btn=(Button)findViewById(R.id.search_btn);
        linearLayout=(LinearLayout)findViewById(R.id.empty_list);
        info_list=(LinearLayout)findViewById(R.id.info_list);
        spinner = (Spinner)findViewById(R.id.spinner_info);

        all_info.setOnClickListener(this);
        fever_info.setOnClickListener(this);
        remove_info.setOnClickListener(this);
        low_battery_info.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        back_view.setOnClickListener(this);

        //搜索图标大小
        Drawable drawable1 = getResources().getDrawable(R.drawable.search);
        Drawable drawable2 = getResources().getDrawable(R.drawable.back);
        drawable1.setBounds(15, 0, 35, 35);
        drawable2.setBounds(15, 0, 35, 35);
        search_btn.setCompoundDrawables(drawable1 , null, null, null);//只放左边
        back_view.setCompoundDrawables(drawable2 , null, null, null);//只放左边
        //获取系统语言
        language = GetInfoUtil.getLanguage();
        queue= Volley.newRequestQueue(InfoListActivity.this);

        //初始化frament
        initFrament();

        info_list.setVisibility(View.GONE);
    }



}
