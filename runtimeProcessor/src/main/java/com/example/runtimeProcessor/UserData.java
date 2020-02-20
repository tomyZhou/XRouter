package com.example.runtimeProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.CONSTRUCTOR)     //作用于构造函数的注解
@Retention(RetentionPolicy.RUNTIME)  //注解类型：运行时注解
public @interface UserData {

    public int id() default 0;

    public String name() default  "zhougang";

    public int age() default 10;
}
