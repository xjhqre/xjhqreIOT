package com.xjhqre.common.utils.uuid;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    public static final String BASE_CHAR_NUMBER = "abcdefghijklmnopqrstuvwxyz0123456789";

    public RandomUtils() {}

    /**
     * 获取 length 位随机数字和字母
     */
    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        if (length < 1) {
            length = 1;
        }

        int baseLength = "abcdefghijklmnopqrstuvwxyz0123456789".length();

        for (int i = 0; i < length; ++i) {
            int number = ThreadLocalRandom.current().nextInt(baseLength);
            sb.append("abcdefghijklmnopqrstuvwxyz0123456789".charAt(number));
        }

        return sb.toString();
    }
}