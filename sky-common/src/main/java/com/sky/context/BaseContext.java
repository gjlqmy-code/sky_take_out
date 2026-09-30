package com.sky.context;

public class BaseContext {
    //Thread当中的局部变量，属于同一个线程时可以用，为每一个线程提供一份单独的存储空间，每一次请求都是一个单独的线程

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }//设置当前线程局部变量的值

    public static Long getCurrentId() {
        return threadLocal.get();
    }//返回当前线程局部变量的值

    public static void removeCurrentId() {
        threadLocal.remove();
    }//移除当前线程局部变量

}
