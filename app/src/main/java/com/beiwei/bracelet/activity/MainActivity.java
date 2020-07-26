package com.beiwei.bracelet.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beiwei.bracelet.R;
import com.beiwei.bracelet.model.impl.HttpGetDataImpl;
import com.beiwei.bracelet.model.inter.IHttpGetDataModel;
import com.beiwei.bracelet.utils.Constants;
import com.beiwei.bracelet.utils.GetInfoUtil;
import com.beiwei.bracelet.utils.HttpUtils;
import com.beiwei.bracelet.utils.JsonUtils;
import com.beiwei.bracelet.utils.MD5;
import com.beiwei.bracelet.utils.SPUtil;
import com.beiwei.bracelet.utils.UpdateManager;
import com.beiwei.bracelet.utils.Utils;
import com.beiwei.bracelet.model.callback.CallBack;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_login,btn_getCode;
    private EditText edit_phone,edit_code;
    private String language;
    private String url;
    private RequestQueue queue;
    private Intent intent;
    private SPUtil sp=SPUtil.instance;
    private IHttpGetDataModel model;
    private Map<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new HttpGetDataImpl();
        initView();
        //检查版本信息
        checkVersion();
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        if(view_id==R.id.login){//登录
            if (TextUtils.isEmpty(edit_phone.getText())){
                Toast.makeText(MainActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(edit_code.getText())){
                Toast.makeText(MainActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            String phone=edit_phone.getText().toString();
            String code=edit_code.getText().toString();
            url=Constants.HOST+"/login/msgCodeVal";
            map= new HashMap<String, String>();
            map.put("phone",phone);
            map.put("code",code);
            map.put("lang",language);
            HttpUtils.submitPost(url, map, new CallBack() {
                @Override
                public <T> void success(T response) {
                    try {
                        JSONObject json = new JSONObject((String) response);
                        //请求成功跳转到主页，并将用户信息持久化到本地
                        if("ok".equals(json.getString("state"))){
                            //将返回的id存到持久化
                            long id = json.getJSONObject("u").getLong("id");
                            String name = json.getJSONObject("u").getString("username");
                            sp.setId(MainActivity.this,id);
                            sp.setName(MainActivity.this,name);
                            //持久化完跳转
                            intent=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }else{//请求失败返回提示信息
                            Toast.makeText(MainActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void fail(String msg) {
                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    //请求失败跳转到登录超时页面
                    intent=new Intent(MainActivity.this,LoginFailActivity.class);
                    startActivity(intent);
                }
            });
        }else if(view_id==R.id.getCode){//获取验证码 /login/getCode
            if (TextUtils.isEmpty(edit_phone.getText())){
                Toast.makeText(MainActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                return;
            }
            String phone=edit_phone.getText().toString();
            url=Constants.HOST+"/login/getCode";
            map = new HashMap<String, String>();
            map.put("phone",phone);
            map.put("lang",language);
            HttpUtils.submitPost(url, map, new CallBack() {
                @Override
                public <T> void success(T response) {
                    try {
                        JSONObject json = new JSONObject((String) response);
                        //请求成功跳转到主页，并将用户信息持久化到本地
                        if("ok".equals(json.getString("state"))) {
//                            Toast.makeText(MainActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void fail(String msg) {
                    Toast.makeText(MainActivity.this,"获取验证码失败，请重试",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 校验版本信息
     */
     public void checkVersion(){
         String imei = Utils.getIMET(this);
         String s = MD5.GetMD5Code(imei+System.currentTimeMillis());
         Map<String,Object> map = new HashMap<>();
         map.put("t","v");
         map.put("i",imei);
         map.put("s",s);
         map.put("v",getVersionCode());
         String json = JsonUtils.getJson(map);
         Map<String,String> para = new HashMap<>();
         para.put("para",json);
         model.submitPost(Constants.UPDATEINFO, para, new CallBack() {
             @Override
             public <T> void success(T response) {
                 try {
                     JSONObject json = (JSONObject) response;
                     JSONObject result = json.getJSONObject("obj");
                     int v = result.getInt("v");
                     if(v == 1){
                         String url = result.getString("f");
                         update(url,1);
                     }
                 }catch (JSONException e){
                     e.printStackTrace();
                     Log.i("info","数据解析错误:"+e.getMessage());
                 }
             }

             @Override
             public void fail(String msg) {
                 showToast(msg);
             }
         });
     }

    private void update(String url,int ver){
        new UpdateManager(this, ver, url, "WristBand", ver, 0);
    }
    /**
     * 初始化方法
     */
    private void initView(){
        //获取组件对应的id
        btn_login=(Button)findViewById(R.id.login);
        btn_getCode=(Button)findViewById(R.id.getCode);
        edit_phone = (EditText) findViewById(R.id.phone);
        edit_code = (EditText) findViewById(R.id.code);

        //关闭页面一进入就弹出键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //隐藏自带的标题菜单
//        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
//        if (actionBar!=null){
//            actionBar.hide();
//        }

        //控制输入框图标大小
        Drawable drawable1 = getResources().getDrawable(R.drawable.phone);
        Drawable drawable2 = getResources().getDrawable(R.drawable.code);
        //第一个 0 是距左边距离，第二个 0 是距上边距离，40 分别是长宽
        drawable1.setBounds(0, 0, 25, 40);
        drawable2.setBounds(0, 0, 25, 40);

        edit_phone.setCompoundDrawables(drawable1 , null, null, null);//只放左边
        edit_code.setCompoundDrawables(drawable2 , null, null, null);//只放左边

        //获取系统语言
        language = GetInfoUtil.getLanguage();
        //点击事件
        btn_login.setOnClickListener(this);
        btn_getCode.setOnClickListener(this);

        //创建队列
        queue=Volley.newRequestQueue(MainActivity.this);

        if(sp.getId(MainActivity.this)!=null && sp.getId(MainActivity.this)!=0L){
            intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }
        //修改顶部状态栏
//        StatusBarUtils.with(this)
//                .setColor(getResources().getColor(R.color.main_tab_text_color))
//                .init();
    }


}