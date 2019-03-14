package com.ooliuyue.simplRpc.utils;

/**
 * @Auther: ly
 * @Date: 2019/3/14 11:58
 */

public class StringUtils {
    public static boolean isNotBlank(String str){
        if(str!=null&&str.length()>0)
            return true;
        return false;
    }
}