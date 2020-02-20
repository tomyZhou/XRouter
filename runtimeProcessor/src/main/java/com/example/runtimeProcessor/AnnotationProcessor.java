package com.example.runtimeProcessor;

import java.lang.reflect.Constructor;

/**
 * 运行时注解处理器，使用的就是反射原理
 */
public class AnnotationProcessor {

    public static void init(Object object){
        if(!(object instanceof User)){
            throw new IllegalArgumentException("["+object.getClass().getSimpleName()+"] isn't type of User" );
        }

        Constructor[] constructors = object.getClass().getDeclaredConstructors();
        for(Constructor constructor:constructors){
            if(constructor.isAnnotationPresent(UserData.class)){//判断是否有某种注解
                UserData userData =(UserData) constructor.getAnnotation(UserData.class);
                ((User) object).setAge(userData.age());
                ((User) object).setId(userData.id());
                ((User) object).setName(userData.name());

            }
        }
    }
}
