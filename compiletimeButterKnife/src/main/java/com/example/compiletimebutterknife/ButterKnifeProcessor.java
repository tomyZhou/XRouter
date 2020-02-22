package com.example.compiletimebutterknife;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 *
 *
 * javapoet 中的相关知识点
 * 2.1.1 几个常用的类
 * MethodSpec 代表一个构造函数或方法声明。
 * TypeSpec 代表一个类，接口，或者枚举声明
 * FieldSpec 代表一个成员变量，一个字段声明。
 * JavaFile包含一个顶级类的Java文件。
 * ParameterSpec 用来创建参数
 * AnnotationSpec 用来创建注解
 * TypeName 类型，如在添加返回值类型是使用 TypeName.VOID
 * ClassName 用来包装一个类
 *
 * 2.1.2 javapoet 常用的API
 *
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
 *
 * 2.1.3 占位符
 * $L代表的是字面量
 * $S for Strings
 * $N for Names(我们自己生成的方法名或者变量名等等)
 * $T for Types
 * 这里的$T，在生成的源代码里面，也会自动导入你的类。
 * ————————————————
 * 原文链接：https://blog.csdn.net/qq_26376637/article/details/52374063
 */

public class ButterKnifeProcessor extends AbstractProcessor {

    /**
     * Element 工具类
     */
    private Elements elementUtils;

    /**
     * 用来创建源文件的工具
     **/
    private Filer filer;

    /**
     * 打印
     */
    private Messager mMessager;
    private MethodSpec initViewMethodSpec;
    private MethodSpec bindEventMethodSpec;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annimations, RoundEnvironment roundEnvironment) {


//       遍历所有的注解
        for (TypeElement typeElement : annimations) {
            Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element element : elementSet) {
                print(typeElement.getSimpleName() + ":"
                        + element.toString() + ":"
                        + element.getKind() + ":"
                        + element.getSimpleName() + ":"
                        + element.asType());

                if (element.getKind() == ElementKind.CLASS) {
                    print("我是一个类Element");
                }

                if (element.getKind() == ElementKind.FIELD) {
                    print("我是一个属性Element");
                }

                if (typeElement.getSimpleName().toString().equals(BindView.class.getSimpleName())) {
                    int id = element.getAnnotation(BindView.class).value();
                    print("" + id);
                } else {
                    print("typeElement:" + typeElement.getSimpleName().toString());
                    print("BindView CanonicalName:" + BindView.class.getSimpleName());
                }
            }
        }
        initView(roundEnvironment);
        return true;
    }

    /**
     * 生成setOnclickListener方法
     *
     * @param roundEnvironment
     */
    private void bindEvent(RoundEnvironment roundEnvironment, String activityName) {
        //获取所有包含 OnClick 注解的元素
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        /**
         *    因为很多Activity中都有OnClick注解，所以要按Activity对获得到的OnClick注解分类。用一个Map存储。
         *     key就是Activity名字，value就是这个Activity中被OnClick标注的注解元素集合。
         */
        Map<String, List<Element>> activityElementMap = new HashMap<>();
        for (Element element : bindViewElements) {
            //将Element遍历，放在不同的Activity的map中。
            List<Element> elementList = activityElementMap.get(activityName);
            if (elementList == null) {
                elementList = new ArrayList<>();
                elementList.add(element);
                activityElementMap.put(activityName, elementList);
            } else {
                elementList.add(element);
            }
        }


        ArrayList<Element> list = (ArrayList<Element>) activityElementMap.get(activityName);


        //第一步：生成方法
        MethodSpec.Builder bindEventmethodBuilder = MethodSpec.methodBuilder("bindEvent")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeVariableName.get(activityName), "context")
                .returns(void.class);


        for (Element element : list) {
            int id[] = element.getAnnotation(OnClick.class).value();
            for (int index : id) {
                bindEventmethodBuilder.addStatement("context.findViewById(" + index + ").setOnClickListener(context)");
            }

        }

        bindEventMethodSpec = bindEventmethodBuilder.build();
    }


    private void initView(RoundEnvironment roundEnvironment) {
        //获取所有包含 BindView 注解的元素
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        /**
         *    因为很多Activity中都有BindView注解，所以要按Activity对获得到的BindView注解分类。用一个Map存储。
         *     ey就是Activity名字，value就是这个Activity中被BindView标注的注解元素集合。
         */
        Map<String, List<Element>> activityElementMap = new HashMap<>();
        for (Element element : bindViewElements) {
            //将Element遍历，放在不同的Activity的map中。
            String activityName = getActivityName(element);
            List<Element> elementList = activityElementMap.get(activityName);
            if (elementList == null) {
                elementList = new ArrayList<>();
                elementList.add(element);
                activityElementMap.put(activityName, elementList);
            } else {
                elementList.add(element);
            }
        }

        //生成文件
        for (String activityName : activityElementMap.keySet()) {

            ArrayList<Element> list = (ArrayList<Element>) activityElementMap.get(activityName);


            //第一步：生成方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("initView")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeVariableName.get(activityName), "context")
                    .returns(void.class);

            for (Element element : list) {
                int id = element.getAnnotation(BindView.class).value();
                methodBuilder.addStatement("context." + element.getSimpleName().toString()
                        + "=(" + element.asType() + ")" + "context.findViewById(" + id + ");");
            }

            initViewMethodSpec = methodBuilder.build();

            bindEvent(roundEnvironment, activityName);

            //导入IBinder那个接口文件,这样可以导入包。重要！！！
            ClassName iBinder = ClassName.get(IBinder.class.getPackage().getName(), IBinder.class.getSimpleName());
            ClassName activityClassName = ClassName.bestGuess(activityName);

            //参考 https://blog.csdn.net/small_lee/article/details/80264415
            //第二步：生成类
            TypeSpec bindActivity = TypeSpec.classBuilder(activityName + "_ViewBinding")
                    .addModifiers(Modifier.PUBLIC)
//                    .addSuperinterface(IBinder.class)
                    //实现带泛型的接口，重要！！！！
                    .addSuperinterface(ParameterizedTypeName.get(iBinder, activityClassName))
                    .addMethod(initViewMethodSpec)
                    .addMethod(bindEventMethodSpec)
                    .build();


            //第三步：写到文件中
            JavaFile javaFile = JavaFile.builder(getPackageName(list.get(0)), bindActivity).build();

            //写到控制台
            try {
                javaFile.writeTo(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //写到文件中
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取这个注解所在的Activity名字
     *
     * @return
     */
    private String getActivityName(Element element) {
        String activityName = element.getEnclosingElement().getSimpleName().toString();
        print("我是ActivityName:" + activityName);
        return activityName;
    }


    /**
     * 获取这个Activity所在的包名字
     *
     * @param element
     * @return
     */
    private String getPackageName(Element element) {
        PackageElement packageElement = elementUtils.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(ContentView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.latestSupported();
    }


    private void print(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);

    }

}
