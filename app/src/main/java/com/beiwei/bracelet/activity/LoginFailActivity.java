package com.beiwei.bracelet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.beiwei.bracelet.R;
import com.beiwei.bracelet.fragment.info.FourFragment;
import com.beiwei.bracelet.fragment.info.OneFragment;
import com.beiwei.bracelet.fragment.info.ThreeFragment;
import com.beiwei.bracelet.fragment.info.TwoFragment;

import java.util.ArrayList;
import java.util.List;

public class LoginFailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView to_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);

        initView();
    }

    private void initView() {
        to_login=(TextView)findViewById(R.id.to_login);
        to_login.setOnClickListener(this);

        //隐藏自带的标题菜单
        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.to_login){
            Intent intent=new Intent(LoginFailActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
