package com.byids.hy.testpro.utils;

import android.util.Log;

/**
 * 打印长信息工具
 * Created by gqgz2 on 2016/11/8.
 */

public class LongLogCatUtil{
    public static void logE(String tag, String content) {
        int p = 2048;
        long length = content.length();
        if (length < p || length == p)
            Log.e(tag, content);
        else {
            while (content.length() > p) {
                String logContent = content.substring(0, p);
                content = content.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, content);
        }
    }
}
