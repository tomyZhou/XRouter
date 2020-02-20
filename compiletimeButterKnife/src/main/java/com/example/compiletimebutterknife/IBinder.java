package com.example.compiletimebutterknife;

public interface IBinder<T> {
    void initView(T t);

    void bindEvent(T t);
}
