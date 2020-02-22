package com.example.xrouterprocessor;

import com.example.annotation_lib.XRouterPath;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * javapoet 中的相关知识点
 * <p>
 * 2.1.1 几个常用的类
 * MethodSpec 代表一个构造函数或方法声明。
 * TypeSpec 代表一个类，接口，或者枚举声明
 * FieldSpec 代表一个成员变量，一个字段声明。
 * JavaFile包含一个顶级类的Java文件。
 * ParameterSpec 用来创建参数
 * AnnotationSpec 用来创建注解
 * TypeName 类型，如在添加返回值类型是使用 TypeName.VOID
 * ClassName 用来包装一个类
 * <p>
 * 2.1.2 javapoet 常用的API
 * <p>
 * addStatement() 方法负责分号和换行
 * beginControlFlow() + endControlFlow() 需要一起使用，提供换行符和缩进。
 * addCode() 以字符串的形式添加内
 * returns 添加返回值类型
 * .constructorBuilder() 生成构造器函数
 * .addAnnotation 添加注解
 * addSuperinterface 给类添加实现的接口
 * superclass 给类添加继承的父类
 * ClassName.bestGuess(“类全名称”) 返回ClassName对象，这里的类全名称表示的类必须要存在，会自动导入相应的包
 * ClassName.get(“包名”，”类名”) 返回ClassName对象，不检查该类是否存在
 * TypeSpec.interfaceBuilder(“HelloWorld”)生成一个HelloWorld接口
 * MethodSpec.constructorBuilder() 构造器
 * addTypeVariable(TypeVariableName.get(“T”, typeClassName))
 * 会给生成的类加上泛型
 * <p>
 * 2.1.3 占位符
 * $L代表的是字面量
 * $S for Strings
 * $N for Names(我们自己生成的方法名或者变量名等等)
 * $T for Types
 * 这里的$T，在生成的源代码里面，也会自动导入你的类。
 * ————————————————
 * 原文链接：https://blog.csdn.net/qq_26376637/article/details/52374063
 */

public class XRouterProcessor extends AbstractProcessor {

    //生成ActivityRouterUtil.java


    /**
     * public class ActivityRouterUtil implements IRouter {
     *
     * @Override public void putActivity() {
     * XRouter.getInstance().putActivity("main/main", MainActivity.class);
     * XRouter.getInstance().putActivity("main/home",HomeActivity.class);
     * }
     * }
     */


    private Filer mFiler;
    private Messager mMessager;
    private Elements elementsUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        elementsUtil = processingEnvironment.getElementUtils();
    }

    /**
     * 声明我的的注解处理器要处理什么注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set types = new HashSet();
        types.add(XRouterPath.class.getCanonicalName());
        return types;
    }


    /**
     * 声明注解处理器支持的源版本，固定写法
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        print("xxxxxxxxxxxxxxxxxxxxxxx:XRouterProcessor");
        //第一步：获取数据
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XRouterPath.class);

        Map<String, String> map = new HashMap<>();
        for (Element element : elements) {
            String path = element.getAnnotation(XRouterPath.class).value();
            element.getSimpleName();//只能获取到简短的类名
            TypeElement typeElement = (TypeElement) element;
            String activityName = typeElement.getQualifiedName().toString();//获取到完整的类名
            map.put(path, activityName);
        }

        if (map.size() == 0) {
            return false;
        }

        //第二步：创建方法
        /**
         * ClassName.bestGuess(“类全名称”) 返回ClassName对象，这里的类全名称表示的类必须要存在，会自动导入相应的包
         * ClassName.get(“包名”，”类名”) 返回ClassName对象，不检查该类是否存在
         */
        ClassName interfaceName = ClassName.get(IRouter.class.getPackage().getName(), IRouter.class.getSimpleName()); //这一句是为了导入IRouter类

        //手动导包XRouter
        ClassName xRouter = ClassName.get("com.example.xrouter", "XRouter");

        MethodSpec.Builder builder = MethodSpec.methodBuilder("putActivity")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        for (String key : map.keySet()) {
            //$S加引号，$L不加引号
            builder.addStatement("$T.getInstance().putActivity($S,$L)", xRouter, key, map.get(key) + ".class");
        }
        MethodSpec putActivity = builder.build();

        //第三步：创建文件
        //https://blog.csdn.net/IO_Field/article/details/89355941
        TypeSpec typeSpec = TypeSpec.classBuilder("ActivityUtil_" + System.currentTimeMillis())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(interfaceName)
                .addMethod(putActivity)
                .build();

        //第四步：写文件
        JavaFile javaFile = JavaFile.builder("com.example.xrouter.activityMap", typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void print(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}

