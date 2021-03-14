package com.workflow.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataRedirectUtil {
    private final static String _IV = "12345678";
    private final static String Algorithm = "DESede/CBC/PKCS5Padding";// 加密方法／运算模式／填充模式

    private static SecretKey deskey;
    private static IvParameterSpec ips;

    static {
        final byte[] keyBytes = {(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
                (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x00, (byte) 0x11, (byte) 0x22,
                (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88,
                (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
                (byte) 0xFF};
        try {
            deskey = new SecretKeySpec(keyBytes, "DESede");
            ips = new IvParameterSpec(_IV.getBytes());
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public static String encrypt(String password) {

        Cipher cipher;
        String result = null;
        try {
            cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);

            byte[] arry = cipher.doFinal(password.getBytes());
            result = new String(Base64.encodeBase64(arry));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decrypt(String password) {
        Cipher cipher;
        String result = null;
        try {
            cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
            byte[] arry = cipher.doFinal(Base64.decodeBase64(password.getBytes()));
            result = new String(arry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
