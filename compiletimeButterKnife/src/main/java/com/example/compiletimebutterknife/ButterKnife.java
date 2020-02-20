package com.example.compiletimebutterknife;

public final class ButterKnife {

    /**
     * 通过反射创建一个通过javapoet的类的对象，调用生成的方法
     *
     * @param activity
     */

        public static void bind(Object activity){
            String name = activity.getClass().getName() + "_ViewBinding";
            try {
                Class<?> aClass = Class.forName(name);
                IBinder iBinder = (IBinder) aClass.newInstance();
                iBinder.initView(activity);
                iBinder.bindEvent(activity);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}