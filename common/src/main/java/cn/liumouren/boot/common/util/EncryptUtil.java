package cn.liumouren.boot.common.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 *
 *
 * @author freeman
 * @date 2021/12/4 20:28
 */
public final class EncryptUtil {

    private static final AES AES = SecureUtil.aes("freeman-microservice-parent-1123".getBytes(StandardCharsets.UTF_8));

    /**
     * 加密为 16 进制字符串
     * @param str 原文
     * @return 密文
     */
    public static String encode(String str) {
        return AES.encryptHex(str);
    }

    /**
     * 解密加密后的 16 进制字符串
     * @param str 密文
     * @return 原文
     */
    public static String decode(String str) {
        return AES.decryptStr(str);
    }
}
