package com.beiwei.bracelet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.beiwei.bracelet.R;

public class BindDialogFail extends Dialog {
    public BindDialogFail(@NonNull Context context, int quick_option_dialog ) {
        super(context,quick_option_dialog);
    }

    public static class Builder {
        private View mLayout;
        private BindDialogFail mDialog;
        private LayoutInflater inflater;
        private Button dialog_btn_back,dialog_btn_retry;
        private Context ct;

        public Builder(Context context) {
            this.ct=context;
            mDialog = new BindDialogFail(context,R.style.quick_option_dialog);
            //加载布局文件
            inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout =(RelativeLayout)inflater.inflate(R.layout.bind_dialog2, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                               ViewGroup.LayoutParams.MATCH_PARENT));
            dialog_btn_back=(Button)mLayout.findViewById(R.id.dialog_btn_back);
            dialog_btn_retry=(Button)mLayout.findViewById(R.id.dialog_btn_retry);
        }

        public View getInflater(){
            mDialog.setContentView(mLayout);
            return mLayout;
        }
        /**
         * 设置按钮监听
         */
        public BindDialogFail create() {
            mDialog.setContentView(mLayout);
            //关闭
            dialog_btn_back.setOnClickListener(view -> {
                mDialog.dismiss();
            });
//            //重新绑定页面
//            dialog_btn_retry.setOnClickListener(view -> {
//                //关闭当前弹框
//                mDialog.dismiss();
//                BindDialogIdentify builder = new BindDialogIdentify.Builder(ct).create();
//                builder.show();
//                WindowManager.LayoutParams params = builder.getWindow().getAttributes();
//                // params.width = ActionBar.LayoutParams.FILL_PARENT;
//                params.width = WindowManager.LayoutParams.MATCH_PARENT;
//                params.height = 941;
//                builder.getWindow().setAttributes(params);
//            });
//            mDialog.setCancelable(true); //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}

