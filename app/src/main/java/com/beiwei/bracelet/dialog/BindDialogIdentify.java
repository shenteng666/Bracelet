package com.beiwei.bracelet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.beiwei.bracelet.R;

public class BindDialogIdentify extends Dialog {
    public BindDialogIdentify(@NonNull Context context, int quick_option_dialog) {
        super(context,quick_option_dialog);
    }
    public static class Builder {
        private View mLayout;
        private BindDialogIdentify mDialog;
        private LayoutInflater inflater;
        public Builder(Context context) {
            mDialog = new BindDialogIdentify(context,R.style.quick_option_dialog);
            //加载布局文件
            inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout =(RelativeLayout)inflater.inflate(R.layout.bind_dialog1, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                               ViewGroup.LayoutParams.MATCH_PARENT));
        }
        public View getInflater(){
            mDialog.setContentView(mLayout);
            return mLayout;
        }
        /**
         * 设置按钮监听
         */
        public BindDialogIdentify create() {
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(false);                //用户不可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}
