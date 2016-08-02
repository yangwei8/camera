package com.leautolink.leautocamera.utils;

import java.text.DecimalFormat;

/**
 * Created by lixinlei on 15/9/24.
 */
public class FormatUtils {
    public FormatUtils(){
        //需要添加返回上下文对象的方法
    }
    public static String formatBFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String formatKBFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "KB";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "MB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "GB";
        }
        return fileSizeString;
    }
////以万，千万， 亿分割
//    +(NSString *)coverCountToStringWithCountOne:(NSInteger)count {
//        NSString *str;
//        if (count < 1) {
//            return @"0";
//        }else if (count < 10000) {
//            str = [NSString stringWithFormat:@"%ld",(long)count];
//        }else if (count < 1000000){
//            NSInteger intTemp1 = count/10000;
//            NSInteger intTemp2 = (count - 10000 * intTemp1) / 1000;
//            str = intTemp2 == 0? [NSString stringWithFormat:@"%ld万",(long)intTemp1]:[NSString stringWithFormat:@"%ld.%ld万",(long)intTemp1,(long)intTemp2];
//        }else if(count < 10000000) {
//            NSInteger intTemp1 = count/10000;
//            str = [NSString stringWithFormat:@"%ld万", (long)intTemp1];
//        }else if(count < 100000000) {
//            NSInteger intTemp1 = count/10000000;
//            NSInteger intTemp2 = (count - 10000000 * intTemp1) / 100000;
//            str = [NSString stringWithFormat:@"%ld.%ld千万", (long)intTemp1, (long)intTemp2];
//        }else {
//            NSInteger intTemp1 = count/100000000;
//            NSInteger intTemp2 = (count - 100000000 * intTemp1) / 1000000;
//            str = [NSString stringWithFormat:@"%ld.%ld亿", (long)intTemp1, (long)intTemp2];
//        }
//        return str;
//    }
    public static String formatCount(int count){
        String countStr = "";
        if(count<1){
            countStr = "0";
        }else if (count < 10000) {
            countStr = count+"";
        }else if(count < 1000000){
            int int1 = count/10000;
            int int2 = (count - 10000 * int1) / 1000;
            countStr = int2 == 0?int1+"万":int1+"."+int2+"万";
        }else if(count < 10000000) {
            int int1 = count/10000;
            countStr = int1+"万";
        }else if(count < 100000000) {
            int intTemp1 = count/10000000;
            int intTemp2 = (count - 10000000 * intTemp1) / 100000;
            countStr = intTemp2 == 0? intTemp1+"千万":intTemp1+"."+intTemp2+"千万";
        }else {
            int intTemp1 = count/100000000;
            int intTemp2 = (count - 100000000 * intTemp1) / 1000000;
            countStr = intTemp2 == 0? intTemp1+"亿":intTemp1+"."+intTemp2+"亿";
        }

        return countStr;
    }

}
