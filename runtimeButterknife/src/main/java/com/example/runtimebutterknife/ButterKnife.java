package com.example.runtimebutterknife;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 自定义的ButterKnife 使用运行时注解，通过反射实现。
 */
public class ButterKnife {

    public static void inject(Activity activity) {
        setContentView(activity);
        initViews(activity, activity.getWindow().getDecorView());

//        initLayout(activity);
//        initViews(activity, activity.getWindow().getDecorView());
    }


    //获取布局的注解，设置布局资源
    public static void initLayout(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        ContentView contentView = activityClass.getAnnotation(ContentView.class);
        if (contentView != null) {
            int layoutId = contentView.value();
            try {
                //反射执行setContentView（）方法
                Method method = activityClass.getMethod("setContentView", int.class);
                method.invoke(activity, layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //view控件
    public static void initViews(Object object, View sourceView) {
        //获取该类声明的成员变量
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            //获取该成员变量上使用的ViewInject注解
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int viewId = viewInject.id();//获取id参数值
                boolean clickable = viewInject.clickable();//获取clickable参数值
                if (viewId != -1) {
                    try {
                        field.setAccessible(true);
                        field.set(object, sourceView.findViewById(viewId));
                        if (clickable == true) {
                            sourceView.findViewById(viewId).setOnClickListener((View.OnClickListener) (object));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //通过反射来获取R.id.layout，并设置contentView
    private static void setContentView(Activity activity) {
        ContentView contentView = activity.getClass().getAnnotation(ContentView.class);
        if (contentView != null) {
            int layout = contentView.value();
            activity.setContentView(layout);
        }
    }

    private static void initViews(Activity activity, View contentView) {
        Field[] fileds = activity.getClass().getDeclaredFields();
        for (Field field : fileds) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int viewId = viewInject.id();
                boolean clickable = viewInject.clickable();

                if (viewId != -1) {
                    field.setAccessible(true);
                    try {
                        // TextView tv_name = contentView.findViewById(R.id.tv_name);
                        field.set((Object) activity, contentView.findViewById(viewId));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (clickable == true) {
                        contentView.findViewById(viewId).setOnClickListener((View.OnClickListener) activity);
                    }
                }
            }

        }
    }
}
