package com.moandroid.cocos2d.util;

import java.util.Formatter;

public class CCFormatter {
    private static StringBuilder sb = new StringBuilder();
    private static Formatter formatter = new Formatter(sb);

    public static String format(String s, Object... objects) {
    	sb.setLength(0);  
        formatter.format(s, objects);
        return sb.toString();
    }
}
