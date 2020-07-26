package com.beiwei.bracelet.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.beiwei.bracelet.R;

public class EmptyListActivity extends AppCompatActivity {
    private Button search_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emptylist);
        initViews();

    }

    private void initViews() {
        //隐藏自带的标题菜单
        androidx.appcompat.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        TextView text = findViewById(R.id.view_counter_buttons_1).findViewById(R.id.title_info);
        text.setText("实时监测");
        //初始化组件
        search_btn = (Button) findViewById(R.id.search_btn);

        // 绑定按钮
    }
}
