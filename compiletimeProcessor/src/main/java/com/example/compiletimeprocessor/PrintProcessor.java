package com.example.compiletimeprocessor;


import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * ProxyGenerator 这个类是rt.jar 里面的，sun.misc.ProxyGenerator
 *
 * android项目里面没有这个类，所以需要建立java library项目
 *
 * AbstractProcessor 编译时注解处理器也是rt.jar中的javax.annotation.processing.AbstractProcessor里的，
 *
 * 所以也需要建立java library
 */
public class PrintProcessor extends AbstractProcessor {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //annotations contains all types of annotation
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {//find special annotationed element
                print(te.getSimpleName() + ":" + e.toString());//print element
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(Print.class.getCanonicalName());
        annotations.add(Hello.class.getCanonicalName());
        return annotations;
    }

    private void print(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}