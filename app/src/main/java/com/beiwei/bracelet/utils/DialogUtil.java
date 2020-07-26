package com.beiwei.bracelet.utils;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.beiwei.bracelet.R;
import com.beiwei.bracelet.dialog.BindDialogFail;
import com.beiwei.bracelet.dialog.BindDialogIdentify;
import com.beiwei.bracelet.dialog.BindDialogSuccess;
import com.beiwei.bracelet.dialog.HistoryListDialog;
import com.beiwei.bracelet.fragment.adapter.HistoryAdapter;

import java.util.List;

/**
 *初始自定义弹窗
 *
 */
public class DialogUtil {

    /*
     * 打开识别设备弹窗
     */
    public static BindDialogIdentify getBindIdentify(Context context){
        BindDialogIdentify builder_identify = new BindDialogIdentify.Builder(context).create();
        builder_identify.show();
        WindowManager.LayoutParams params = builder_identify.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 941;
        builder_identify.getWindow().setAttributes(params);
        return builder_identify;
    }

    /*
     * 打开识别成功弹窗
     */
    public static BindDialogSuccess getIdentifySuccess(Context context){
        BindDialogSuccess builder_dialog = new BindDialogSuccess.Builder(context).create();
        builder_dialog.show();
        WindowManager.LayoutParams params = builder_dialog.getWindow().getAttributes();
        params.height = 600;
        params.width = 800;
        builder_dialog.getWindow().setAttributes(params);
        return builder_dialog;
    }

    /*
     * 打开识别失败弹窗
     */
    public static BindDialogFail getIdentifyFail(Context context){
        BindDialogFail builder_fail = new BindDialogFail.Builder(context).create();
        builder_fail.show();
        WindowManager.LayoutParams params = builder_fail.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 941;
        builder_fail.getWindow().setAttributes(params);
        return builder_fail;
    }

    /*
     * 打开历史记录弹窗
     */
    public static HistoryListDialog getHistoryListDialog(Context context){
        HistoryListDialog builder_history = new HistoryListDialog.Builder(context).create();;
        //弹出窗口，显示历史记录
        builder_history.show();
        WindowManager.LayoutParams params = builder_history.getWindow().getAttributes();
        params.height = 941;
        params.width = 1050;
        builder_history.getWindow().setAttributes(params);
        return builder_history;
    }
}
