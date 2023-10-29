package com.chy.lamia.utils;


import java.util.Random;

public class CommonUtils {


    public static String lamiaPrefix = "lamia$$";

    public static String tempPrefix = "Temp$";

    public static String generateVarName(String type) {
        StringBuilder result = new StringBuilder(lamiaPrefix);
        String randomString = getRandomString(6);
        if (type != null) {
            result.append(type).append("$$");
        }

        result.append(randomString);
        return result.toString();
    }

    public static String tempName(String name){
        StringBuilder result = new StringBuilder(name);
        String randomString = getRandomString(3);
        result.append(tempPrefix).append(randomString);
        return result.toString();
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
