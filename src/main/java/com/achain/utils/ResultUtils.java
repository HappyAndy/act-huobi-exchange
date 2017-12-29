package com.achain.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fyk
 * @since 2017-12-28 11:32
 */
public class ResultUtils {

    public static Map<String, Object> resultFailMap(String code, String msg) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> resultSuccessMap(String code, String msg, Object data) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

}
