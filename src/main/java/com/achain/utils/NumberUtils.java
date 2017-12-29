package com.achain.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fyk
 * @since 2017-12-28 11:29
 */
public class NumberUtils {

    /**
     * 判断转账金额是否合法
     *
     * @param num
     * @return
     */

    public static boolean isNumber(String num) {
        String positiveFloat = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
        Pattern pattern1 = Pattern.compile(positiveFloat);
        Matcher matcher1 = pattern1.matcher(num);
        String positiveInteger = "^([1-9]\\d*|[0]{1,1})";
        Pattern pattern2 = Pattern.compile(positiveInteger);
        Matcher matcher2 = pattern2.matcher(num);
        return matcher1.matches() || matcher2.matches();
    }
}
