package com.fydata.thread;

public class Singleton {

    private static Singleton singleton;

    private Singleton(){}

    public static Singleton getInstance1(){
        if(null == singleton){
            synchronized(Singleton.class){
                if(null == singleton){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }


    private static class SingletonInner{
        private static final Singleton singleton = new Singleton();
    }

    public static final Singleton getInstance2(){
       return SingletonInner.singleton;
    }

}
