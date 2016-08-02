package com.leautolink.leautocamera.utils;

import java.util.Comparator;

//List排序
public class ComparatorInteger implements Comparator {

    public int compare(Object arg0, Object arg1) {
        Integer user0 = (Integer) arg0;
        Integer user1 = (Integer) arg1;

        int flag = user1.compareTo(user0);

        return flag;
    }

}