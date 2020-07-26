package com.beiwei.bracelet.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static void showMsg(String msg,Context context){
		Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
	}
	
	public static String getIMET(Context context) throws NullPointerException,SecurityException{

//		if(context != null){
//			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
//			String imei = "";
//			imei = telephonyManager.getDeviceId();
//			if(imei != null && !imei.equals("")){
//                return imei;
//            }else{
//			    return getDeviceSN(context);
//            }
//		}
		String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return m_szAndroidID;
	}

    public static String getDeviceSN(Context context) {

        String serial = null;

        try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					serial = Build.getSerial();
				}
				serial = Build.getSerial();
			}else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
				serial = Build.SERIAL;
			}else{
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get =c.getMethod("get", String.class);

                serial = (String)get.invoke(c, "ro.serialno");
			}

        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;
    }
	
	
	public static String getSystemModel(){
		return Build.MODEL;
	}
	

	public static String getIMEI(int slotId){
            try {
				Class clazz = Class.forName("android.os.SystemProperties");
				Method method = clazz.getMethod("get", String.class, String.class);
				String imei = (String) method.invoke(null, "ril.gsm.imei", "");
				if(!TextUtils.isEmpty(imei)){
				    String[] split = imei.split(",");
				    if(split.length > slotId){
				        imei = split[slotId];
				    }
				    //Log.d(TAG,"getIMEI imei: "+ imei);
				    return imei;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return "";
    }
	
	
	
	public static boolean isNetworkConnected(Context context) {
		if(context != null){
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			return ni != null && ni.isConnectedOrConnecting();
		}
		return false;
	}

	public static String getDate(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(time);
		return formatter.format(date);
	}

    /**
     * Xiwi
     * 遍历文件/文件夹 - 函数
     * [String]path        文件路径
     */
    public static void recursiveFiles(){
        String path = "";//Urls.VIDEOPATH;
        // 创建 File对象
        File file = new File(path);
        
        // 取 文件/文件夹
        File files[] = file.listFiles();
        
        // 对象为空 直接返回
        if(files == null){
            return;
        }
                
        // 目录下文件
        if(files.length == 0){
            System.out.println(path + "该文件夹下没有文件");
        }
        
        // 存在文件 遍历 判断
        for (File f : files) {
            // 判断是否为 文件
            if(f.isFile()){
            	if(f.getName().endsWith(".tmp")){
            		f.delete();
            		Log.i("info", f.getName()+"被删除");
            	}
                System.out.print("文件: "+f.getAbsolutePath()); 
            } else {
                System.out.print("未知错误文件"); 
            }
            
        }
        
    }
	
	
}
