package com.fydata.util;

public class RadixUtil {

    /**
     * 十进制和十六进制相互转换
     * @param value
     * @param radix
     * @return
     */
    public static String transfor(String value, int radix){
        int num;
        try {
            switch (radix){
                case 10:
                    num = Integer.parseInt(value, 16);
                    return String.valueOf(num);
                case 16:
                    num = Integer.parseInt(value);
                    return Integer.toHexString(num);
                default:
                    return value;
            }
        }catch (Exception e){
            return value;
        }
    }


    //测试
    public static void main(String[] args) {
        System.out.println(transfor("102323",16));

        System.out.println(transfor("18FB3dgfgdfgdf",10));

    }


}
