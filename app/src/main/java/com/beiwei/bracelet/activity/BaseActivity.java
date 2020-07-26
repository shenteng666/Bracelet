package com.beiwei.bracelet.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beiwei.bracelet.utils.Utils;


public class BaseActivity extends Activity implements IBaseView {
    private Toast toast;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        dialog = new ProgressDialog(this);
    }

    @Override
    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    @Override
    public void showDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog == null){
                    dialog = new ProgressDialog(BaseActivity.this);
                }
                if(dialog != null && !dialog.isShowing()){
                    dialog.setMessage(msg);
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void closeDialog() {
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public String getIMEI() {
        String imei = Utils.getIMET(this);
        Log.i("info","imei==="+imei);
        return imei;
    }

    public String getVersionName() {
        String version = "";
        try {
            version = getPackageManager().getPackageInfo("com.zjs.wristband", 0).versionName;
        }catch (PackageManager.NameNotFoundException e){
            version = "";
        }
        return version;
    }

    public int getVersionCode() {
        int version = 0;
        try {
            version = getPackageManager().getPackageInfo("com.zjs.wristband", 0).versionCode;
        }catch (PackageManager.NameNotFoundException e){
            version = 0;
        }
        return version;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
