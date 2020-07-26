package com.beiwei.bracelet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.beiwei.bracelet.R;

public class BindDialogSuccess extends Dialog {
    public BindDialogSuccess(@NonNull Context context ) {
        super(context);
    }

    public static class Builder {
        private View mLayout;
        private BindDialogSuccess mDialog;
        private LayoutInflater inflater;
        private Button dialig_btn_cancel,dialig_btn_sure;

        public Builder(Context context) {
            mDialog = new BindDialogSuccess(context);
            //加载布局文件
            inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout =(RelativeLayout)inflater.inflate(R.layout.bind_dialog3, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                               ViewGroup.LayoutParams.WRAP_CONTENT));
            dialig_btn_cancel=(Button)mLayout.findViewById(R.id.dialig_btn_cancel);
            dialig_btn_sure=(Button)mLayout.findViewById(R.id.dialig_btn_sure);
        }
        public View getInflater(){
            mDialog.setContentView(mLayout);
            return mLayout;
        }
        /**
         * 设置按钮监听
         */
        public BindDialogSuccess create() {
            mDialog.setContentView(mLayout);
            //关闭
            dialig_btn_cancel.setOnClickListener(view -> {
                mDialog.dismiss();
            });
//            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}
