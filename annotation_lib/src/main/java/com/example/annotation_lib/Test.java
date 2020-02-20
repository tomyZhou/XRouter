package com.example.annotation_lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)        //注解作用的位置：类或接口
@Retention(RetentionPolicy.CLASS)//注解作用的时机：编译时注解
public @interface Test{
    String path();
}