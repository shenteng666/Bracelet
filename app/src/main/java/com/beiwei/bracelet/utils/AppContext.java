package com.beiwei.bracelet.utils;

import android.app.Application;


public class AppContext extends Application {
    private static AppContext Instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        LogToFile.init(this);
    }
    public static AppContext getInstance() {
        return Instance;
    }

}
