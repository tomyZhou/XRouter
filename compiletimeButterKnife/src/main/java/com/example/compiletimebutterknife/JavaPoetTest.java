package com.example.compiletimebutterknife;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;

/**
 *  使用javapoet生成src源代码的小例子。
 */
public class JavaPoetTest {

    public static void main(String args[]) {
        generateHelloWorld();
    }

    public static void generateHelloWorld() {

        MethodSpec test = MethodSpec.methodBuilder("test")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addStatement("$T.out.println($S)",System.class,"Hello I'm test!")
                .build();

        //第一步，生成main函数
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class,"args")
                //添加一个语句。也可以直接用addCode，但是只能写死，没有addStatement灵活
                //$S 表示字符串占位符，不能改成别的
                //$T 使用这个占位符会自动导包
                //$N 可以调用自己写的别的方法
                .addStatement("$T.out.println($S)",System.class,"Hello JavaPoet!")
                .addStatement("$N()",test)
                .build();

        int from =0;
        int to = 10;
        MethodSpec test$L = MethodSpec.methodBuilder("testL")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .beginControlFlow("for(int i =$L;i<$L,i++)",from,to) //ControlFlow可以添加大括号
                //使用$L就可以把放入其中的字符当做对象来处理，和$S的区别是加不加引号
                .addStatement("$T.out.println($L)",System.class,"i")
                .endControlFlow()
                .build();


        //第二步，生成类
        TypeSpec helloWorld  = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)  //将上面的main方法添加到这个类中
                .addMethod(test)
                .addMethod(test$L)
                .build();

        //第三步，生成java文件对象
        JavaFile javaFile = JavaFile.builder("com.example.compiletimebutterknife",helloWorld).build();

        //第四步，输出到控制台（也可以输出到文件）
        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}

