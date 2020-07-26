package com.beiwei.bracelet.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.beiwei.bracelet.fragment.bind.BindOneFragment;
import com.beiwei.bracelet.model.Member;
import com.beiwei.bracelet.fragment.bind.BindTwoFragment;
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

public class BindListActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayout,info_list;
    private TextView all,unbind,back_view;
    private ViewPager vp;
    private Spinner spinner;
    private Button search_btn;
    private BindOneFragment bof;
    private BindTwoFragment btf;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private InfoListActivity.FragmentAdapter mFragmentAdapter;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private SPUtil sp=SPUtil.instance;
    private RequestQueue queue;
    private String language;
    private String url;
    private Long pid;//查询的渠道id
    private ArrayList<String> spinnerList = new ArrayList<String>();//创建数组列表 用来存放以后要显示的内容
    private Map<String,Long> id_map=new HashMap<>();;//根据班级名称存储 id
    private Map<String, String> map;
    public static ArrayList<Member> memberList;//存放成员信息
    //蓝牙
    public static BindListActivity bla=new BindListActivity();
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindlist);
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
        if(viewId==R.id.all){
            vp.setCurrentItem(0, true);
        }else if (viewId==R.id.unbind){
            vp.setCurrentItem(1, true);
        }else if(viewId==R.id.back_view){
            Intent intent = new Intent(BindListActivity.this,HomeActivity.class);
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
                            //搜索成功的时候将查询的id存放到sp中持久化
                            sp.setPid(BindListActivity.this,pid);
//                            Long pid = sp.getPid(BindListActivity.this);
//                            Map<String, ?> all = sp.getAll(BindListActivity.this);
                            //传入通讯变更时用的数据
                            EventBus.getDefault().post(memberList);
                            //移除布局初始布局文件
                            info_list.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(BindListActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void fail(String msg) {
                    Toast.makeText(BindListActivity.this,"请先选择班级",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 初始化Frament
     */
    private void initFrament(){
        vp = (ViewPager) findViewById(R.id.bindViewPager);
        bof = new BindOneFragment();
        btf = new BindTwoFragment();

        //给FragmentList添加数据
        mFragmentList.add(bof);
        mFragmentList.add(btf);

        mFragmentAdapter = new InfoListActivity.FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        all.setTextColor(Color.parseColor("#1ba0e1"));
        unbind.setTextColor(Color.parseColor("#000000"));
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
        map.put("id", sp.getId(BindListActivity.this).toString());
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
                        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(BindListActivity.this,android.R.layout.simple_spinner_item, spinnerList);
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
                        Toast.makeText(BindListActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void fail(String msg) {
                Toast.makeText(BindListActivity.this,"请求超时，请稍后重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始蓝牙
     */
    public void initBlueTooth(){
        // 初始化蓝牙适配器
        this.bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        //判断是否支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙,无法进行绑定设备", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断蓝牙是否开启，如果关闭则请求打开蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            //请求打开蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
            //直接打开蓝牙
            // bAdapter.enable();
        }
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
    }
    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public class FragmentAdapter extends FragmentPagerAdapter {
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
            all.setTextColor(Color.parseColor("#1ba0e1"));
            unbind.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            unbind.setTextColor(Color.parseColor("#1ba0e1"));
            all.setTextColor(Color.parseColor("#000000"));
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
        text.setText("数据绑定");

        //初始化组件
        all = (TextView) findViewById(R.id.all);
        unbind = (TextView) findViewById(R.id.unbind);
        linearLayout=(LinearLayout)findViewById(R.id.empty_list);
        info_list=(LinearLayout)findViewById(R.id.info_list);
        spinner = (Spinner)findViewById(R.id.spinner_info);
        search_btn=(Button)findViewById(R.id.search_btn);
        back_view= (TextView)findViewById(R.id.view_counter_buttons_1).findViewById(R.id.back_view);

        all.setOnClickListener(this);
        unbind.setOnClickListener(this);
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
        queue= Volley.newRequestQueue(BindListActivity.this);

        info_list.setVisibility(View.GONE);
        //初始化Frament
        initFrament();
        //初始化蓝牙
        initBlueTooth();
    }
}
