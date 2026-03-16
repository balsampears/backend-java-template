package com.balsam.system.common.utils;

import java.util.Base64;

/**
 * @DESCRIPTION: base64加解密工具类
 * @USER: liuyanbin
 * @DATE: 2023/12/28 0028 14:08
 */
public class Base64Util {

    /**
     * 加密
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String key) throws Exception {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    /**
     * 解密
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryBASE64(String key) throws Exception {
        return new String(Base64.getDecoder().decode(key));
    }
}
