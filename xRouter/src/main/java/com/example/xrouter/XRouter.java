package com.example.xrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.xrouterprocessor.IRouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 模块之间跳转的类 主要是维护一个Activity的map
 *
 * @author zhoguang@unipus.cn
 * @date 2020/2/20 17:48
 */
public class XRouter {
    //装载所有的Activity的class的对象集合
    private HashMap<String, Class<? extends Activity>> activityMap;

    private static XRouter xRouter = new XRouter();

    private Context mContext;


    private XRouter() {
        activityMap = new HashMap<>();
    }

    /**
     * 单例
     *
     * @return
     */
    public static XRouter getInstance() {
        return xRouter;
    }

    public void init(Application application) {
        mContext = application;

        invokeActivityRouter();
    }

    /**
     * 调用生成的类的putActivity方法
     */
    private void invokeActivityRouter() {
        List<String> list = getClassName("com.example.xrouter.activityMap");
        for (String className : list) {
            try {
                Class clazz = Class.forName(className);
                if (IRouter.class.isAssignableFrom(clazz)) {
                    IRouter iRouter = (IRouter) clazz.newInstance();
                    iRouter.putActivity();
                }
                clazz.newInstance();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getClassName(String packageName) {
        List<String> classNameList = new ArrayList<String>();
        try {

            DexFile df = new DexFile(mContext.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();

                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classNameList;
    }

    /**
     * 存入Activity
     *
     * @param path
     * @param activity
     */
    public void putActivity(String path, Class<? extends Activity> activity) {
        if (path == null || activity == null) {
            return;
        }
        activityMap.put(path, activity);
    }

    public void jump(String path, Bundle bundle) {
        if (path == null) {
            return;
        }
        Class<? extends Activity> activity = activityMap.get(path);
        if (activity == null) {
            return;
        }
        Intent intent = new Intent().setClass(mContext, activity);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        mContext.startActivity(intent);

    }

}
