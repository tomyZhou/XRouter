package com.example.xrouter;

import android.app.Application;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        XRouter.getInstance().init(this);

//        //添加app module的Activity
//        ActivityRouterUtil mainActivityRouterUtil = new ActivityRouterUtil();
//        mainActivityRouterUtil.putActivity();
//
//        //添加login module的Activity
//        com.example.login.ActivityRouterUtil loginActivityRouterUtil = new com.example.login.ActivityRouterUtil();
//        loginActivityRouterUtil.putActivity();
    }
}
