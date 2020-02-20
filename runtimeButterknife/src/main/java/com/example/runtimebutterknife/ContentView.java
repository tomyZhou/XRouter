package com.example.runtimebutterknife;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {

    int value(); //只有一个参数需要传递时，可以用value做名称，这时候在使用时就不需要用名称进行标识
}
