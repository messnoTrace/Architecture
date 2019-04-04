package com.notrace.refreshlayout;

import java.util.List;

/**
 * create by chenyang on 2019/4/4
 **/
public class ListUtils {
    /**
     * 判断是否是空list
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 可添加默认值，在list为null的时候返回false
     * @param list
     * @return
     */
    public static boolean isEmptyInitFalse(List list) {
        if (list == null) {
            return false;
        }
        return list.size() == 0;
    }
}
