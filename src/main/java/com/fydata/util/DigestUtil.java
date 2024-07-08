/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */

package com.fydata.util;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DigestUtil {
    private DigestUtil() {}

    public static final MessageDigest SHA_256;
    public static final MessageDigest SHA_1;
    public static final MessageDigest MD5;
    static {
        try {
            SHA_256 = MessageDigest.getInstance("SHA-256");
            SHA_1 = MessageDigest.getInstance("SHA-1");
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha256(String msg) {
        return digest(SHA_256, msg);
    }

    public static String sha256(byte[] msg) {
        return digest(SHA_256, msg);
    }


    public static String sha(String msg) {
        return digest(SHA_1, msg);
    }

    public static String sha(byte[] msg) {
        return digest(SHA_1, msg);
    }

    public static String md5(String msg) {
        return digest(MD5, msg);
    }

    public static String md5(byte[] msg) {
        return digest(MD5, msg);
    }

    public static String digest(MessageDigest digest, String msg) {
        return digest(digest, msg.getBytes());
    }

    public static String digest(MessageDigest digest, byte[] msg) {
        return Hex.encodeHexString(digestBytes(digest, msg));
    }

    public static byte[] digestBytes(MessageDigest digest, byte[] msg) {
        digest.update(msg);
        return digest.digest();
    }

}
