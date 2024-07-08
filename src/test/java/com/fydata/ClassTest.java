package com.fydata;

import org.openjdk.jol.info.ClassLayout;

public class ClassTest {

    public static class T{
        int m;
        boolean b;
        char c;
        long l;
        double d;
        String s;
    }

    public static void main(String[] args) {
        T t = new T();
        String s = ClassLayout.parseInstance(t).toPrintable();
        System.out.println(s);
    }
}
