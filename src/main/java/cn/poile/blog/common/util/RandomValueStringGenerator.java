package cn.poile.blog.common.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 随机字符串生成器
 *
 * @author: yaohw
 * @create: 2019-11-12 14:38
 **/
public class RandomValueStringGenerator {

    private static final char[] DEFAULT_CODE = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    private Random random = new SecureRandom();

    private int length;


    public RandomValueStringGenerator() {
        this(10);
    }


    public RandomValueStringGenerator(int length) {
        this.length = length;
    }

    public String generate() {
        byte[] verifierBytes = new byte[length];
        random.nextBytes(verifierBytes);
        return getCodeString(verifierBytes);
    }

    private String getCodeString(byte[] verifierBytes) {
        char[] chars = new char[verifierBytes.length];
        for (int i = 0; i < verifierBytes.length; i++) {
            chars[i] = DEFAULT_CODE[((verifierBytes[i] & 0xFF) % DEFAULT_CODE.length)];
        }
        return new String(chars);
    }
}
