package com.fydata.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {

    public static final String UTF8 = "UTF-8";
    public static final Base64.Decoder DECODER = Base64.getDecoder();
    public static final Base64.Encoder ENCODER = Base64.getEncoder();

    //编码
    public static String stringToEncode(String text) throws UnsupportedEncodingException {
        byte[] textByte = text.getBytes(UTF8);
        return ENCODER.encodeToString(textByte);
    }

    //编码
    public static String stringToEncode(byte[] textByte) {
        return ENCODER.encodeToString(textByte);
    }

    //解码
    public static String decodeToString(String encoded) throws UnsupportedEncodingException {
        return new String(DECODER.decode(encoded), UTF8);
    }

    //解码
    public static byte[] decodeToByte(String encoded) {
        return DECODER.decode(encoded);
    }


}
