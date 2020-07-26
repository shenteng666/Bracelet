package com.beiwei.bracelet.utils;

import android.os.Environment;

public class Constants {
    public static final String APP_ID = "DNDocv8JtRunFpE2u5uuyog1P3k2c5J3zUXVMHtPevGo";
    public static final String SDK_KEY = "EXV1UNEPCAh9x4DpatRuQZKJbddQ2A8piomu8Gc4JLsT";
    public static String REGISTERPATH = Environment.getExternalStorageDirectory()+"/CTBPorject/register/";
    public static String FAILEDPATH = Environment.getExternalStorageDirectory()+"/CTBPorject/failed/";
    public static String FILESAVE = Environment.getExternalStorageDirectory()+"/CTBPorject/failsave/";
    public static String PAPERSAVE = Environment.getExternalStorageDirectory()+"/CTBPorject/paper/";
    public static String PAPERIMGSAVENAME = "output_image.jpg";
    public static String IMGPATH = Environment.getExternalStorageDirectory()+"/CTBPorject/img/";
    //public static final String HOST = "http://192.168.5.68:81";
    //public static final String HOST = "http://188.188.188.51:82";
    //public static final String HOST = "http://blehand.woyun.net.cn";
//    public static final String HOST = "http://blem.smacha.net";
    public static final String HOST = "http://192.168.5.138:81";
    public static String uploadUrl = "";

    public static final String URL_SPLITTER = "/login";
    public static final  String MIDDLE = "";

//    public static final String UPDATEINFO = HOST + URL_SPLITTER + MIDDLE  +"version";
    public static final String UPDATEINFO = HOST + URL_SPLITTER + MIDDLE  +"/version";

}
