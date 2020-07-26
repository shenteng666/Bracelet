package com.beiwei.bracelet.activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.model.callback.CallBack;
import com.beiwei.bracelet.utils.Constants;
import com.beiwei.bracelet.utils.GetInfoUtil;
import com.beiwei.bracelet.utils.HttpUtils;
import com.beiwei.bracelet.utils.SPUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    //id对应的组件
    private TextView seeList,bindlist,user_name,today_check_num,today_fever_num,manage_person_num,manage_class_num;
    private Button log_out;
    private Intent intent;
    private SPUtil sp=SPUtil.instance;
    private RequestQueue queue;
    private String language;
    private String url;
    private Map<String,String> map;
    //饼状图
    private PieChart bind_mChart,isWear_Chart;
    private List<Entry> isWearList;//是否佩戴
    private List<Entry> bindList;//绑定
    private List<String> textList;//饼状图上的文字
    private List<Integer> colorList;//颜色集合

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_other);

        initViews();
        //进入页面就加载数据
        initData();
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        if(view_id==R.id.log_out){
            //点击移除本地的id和name,返回到登录页
            sp.clearAll(HomeActivity.this);
            intent=new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(view_id==R.id.seeList){
            //信息列表
            intent=new Intent(HomeActivity.this, InfoListActivity.class);
            startActivity(intent);
        }else if(view_id==R.id.bindlist){
            //绑定列表
            intent=new Intent(HomeActivity.this, BindListActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 初始化页面信息
     */
    private void initData() {
        url= Constants.HOST+"/app_home/getCountNum";
        map =new HashMap<String, String>();
        map.put("id", sp.getId(HomeActivity.this).toString());
        map.put("lang",language);
        HttpUtils.submitPost(url, map, new CallBack() {
            @Override
            public <T> void success(T response) {
                try {
                    JSONObject json = new JSONObject((String) response);
                    if("ok".equals(json.getString("state"))){
                        //num1:管理人员数 num2:管理班级数 num3:今日发烧 num4:今日检测
                        user_name.setText(sp.getName(HomeActivity.this));
                        manage_person_num.setText(json.getString("num1"));
                        manage_class_num.setText(json.getString("num2"));
                        today_fever_num.setText(json.getString("num3"));
                        today_check_num.setText(json.getString("num4"));
                    }else{
                        Toast.makeText(HomeActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String msg) {
                Toast.makeText(HomeActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化绑定情况环状图
     */
    private void initBindFigure() {
        url= Constants.HOST+"/app_home/getDevData";
        map =new HashMap<String, String>();
        map.put("id", sp.getId(HomeActivity.this).toString());
        map.put("lang",language);
        HttpUtils.submitPost(url, map, new CallBack() {
            @Override
            public <T> void success(T response) {
                try {
                    JSONObject json = new JSONObject((String) response);
                    if("ok".equals(json.getString("state"))){
                        //颜色集合
                        colorList=new ArrayList<>();
                        colorList.add(Color.rgb(242, 118, 47));
                        colorList.add(Color.rgb(56,181,203));
                        //处理数据
                        PieData pieData = EditData(json, colorList);
                        //处理环状图数据
                        showChart( pieData,bind_mChart,"");

                    }else{
                        Toast.makeText(HomeActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void fail(String msg) {
                Toast.makeText(HomeActivity.this,"请求超时，请稍后重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化佩戴情况环状图
     */
    private void initIsWearFigure() {
        url= Constants.HOST+"/app_home/getDevWear";
        map =new HashMap<String, String>();
        map.put("id", sp.getId(HomeActivity.this).toString());
        map.put("lang",language);
        HttpUtils.submitPost(url, map, new CallBack() {
            @Override
            public <T> void success(T response) {
                try {
                    JSONObject json = new JSONObject((String) response);
                    if("ok".equals(json.getString("state"))){
                        //颜色集合
                        colorList=new ArrayList<>();
                        colorList.add(Color.rgb(56,181,203));
                        colorList.add(Color.rgb(242, 118, 47));
                        //处理数据
                        PieData pieData = EditData(json, colorList);
                        //处理环状图数据
                        showChart( pieData,isWear_Chart,"");
                    }else{
                        Toast.makeText(HomeActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void fail(String msg) {
                Toast.makeText(HomeActivity.this,"请求超时，请稍后重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 绘制饼状图
     * @param pieData
     */
    private void showChart(PieData pieData,PieChart mChart,String title) {
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(55f);  //内环半径
        mChart.setTransparentCircleRadius(60f); // 半透明圈半径
        //mChart.setHoleRadius(0);  // 实心圆
        mChart.setDescription(title);
        //mChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        //mChart.setDrawHoleEnabled(true);
        mChart.setRotationAngle(90); // 初始旋转角度
        mChart.setRotationEnabled(true); // 可以手动旋转
        mChart.setUsePercentValues(true);  //显示成百分比
        // 设置可触摸
        mChart.setTouchEnabled(true);
        // 设置数据
        mChart.setData(pieData);
        // 取消高亮显示
        mChart.highlightValues(null);
        mChart.invalidate();
        //设置比例图
        Legend mLegend = mChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //底部居中显示
        //  mLegend.setForm(Legend.LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        //设置动画
        mChart.animateXY(1000, 1000);
    }

    /**
     * 处理数据
     * @param json
     * @param colors
     * @throws JSONException
     * @return
     */
    public PieData EditData(JSONObject json, List<Integer> colors) throws JSONException {
        //环状图
        JSONArray arrValue = json.getJSONArray("arr2");
        //环状图上面的数据
        isWearList=new ArrayList<Entry>();
        for (int i=0;i<arrValue.length();i++){
            JSONObject obj = arrValue.getJSONObject(i);
            isWearList.add( new Entry(obj.getInt("value"), i));
        }
        //图例
        JSONArray tl = json.getJSONArray("arr1");
        textList=new ArrayList<>();
        for (int i=0;i<tl.length();i++){
            textList.add(tl.getString(i));
        }
        return getPieData(isWearList,textList,colors,"");
    }

    /**
     * @param yValues// yValues用来表示封装每个饼块的实际数据
     * @param colors// 设置饼图颜色
     * @param xValues// xValues用来表示每个饼块上的文字
     * @return
     */
    private PieData getPieData(List<Entry> yValues,List<String> xValues,List<Integer> colors,String title) {
        // y轴集合
        PieDataSet pieDataSet = new PieDataSet(yValues, title);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        // 设置饼图颜色
        pieDataSet.setColors(colors);
        // 设置选中态多出的长度
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 2 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);
        //设置百分比的格式，必须开启百分比
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setValueTextSize(6);
        // 创建饼图数据
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }

    /**
     * 初始化方法
     */
    private void initViews() {
        //隐藏自带的标题菜单
        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
//        seeList,bindlist,user_name,today_check_num,today_fever_num,manage_person_num,manage_class_num
        //获取组件
        seeList=(TextView)findViewById(R.id.seeList);
        bindlist=(TextView)findViewById(R.id.bindlist);
        user_name=(TextView)findViewById(R.id.user_name);
        today_fever_num=(TextView)findViewById(R.id.today_fever_num);
        today_check_num=(TextView)findViewById(R.id.today_check_num);
        manage_person_num=(TextView)findViewById(R.id.manage_person_num);
        manage_class_num=(TextView)findViewById(R.id.manage_class_num);
        log_out=(Button)findViewById(R.id.log_out);
        bind_mChart = (PieChart) findViewById(R.id.bind_chart);
        isWear_Chart = (PieChart) findViewById(R.id.iswear_chart);

        // 绑定事件
        log_out.setOnClickListener(this);
        seeList.setOnClickListener(this);
        bindlist.setOnClickListener(this);

        //获取系统语言
        language = GetInfoUtil.getLanguage();
        queue= Volley.newRequestQueue(HomeActivity.this);

        //饼状图
        initIsWearFigure();
        initBindFigure();
    }


}
