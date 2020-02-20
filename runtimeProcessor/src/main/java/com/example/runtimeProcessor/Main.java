package com.example.runtimeProcessor;

public class Main {
    public static void main(String args[]){
        User user = new User();
        AnnotationProcessor.init(user);
        System.out.print(user);
    }
}
