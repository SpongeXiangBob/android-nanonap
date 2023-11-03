package com.zxy.nanonap.common;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication instance;
    private Context mainActivityContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void setMainActivityContext(Context context) {
        this.mainActivityContext = context;
    }

    public Context getMainActivityContext() {
        return mainActivityContext;
    }
}
