package com.beiwei.bracelet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.beiwei.bracelet.R;

public class HistoryListDialog extends Dialog {
    public HistoryListDialog(@NonNull Context context ) {
        super(context);
    }
    public static class Builder {
        private  View mLayout;
        private ImageButton mButton;
        private HistoryListDialog mDialog;
        private LayoutInflater inflater;

        public Builder(Context context) {
            mDialog = new HistoryListDialog(context);
            //加载布局文件
            inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayout =(RelativeLayout)inflater.inflate(R.layout.history_dialog, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                               ViewGroup.LayoutParams.WRAP_CONTENT));
            mButton = mLayout.findViewById(R.id.dialog2_close);
        }

        public View getInflater(Context context){
                return mLayout;
        }

        /**
         * 设置按钮监听
         */
        public HistoryListDialog create() {
            mButton.setOnClickListener(view -> {
                mDialog.dismiss();
            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
//            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}
