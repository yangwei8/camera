package com.leautolink.leautocamera.utils;

import com.leautolink.leautocamera.domain.ListingInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tianwei1 on 2016/3/8.
 */
public class SequenceUtils {
    private static final java.lang.String TAG = "SequenceUtils";

    /**
     * 针对正序的纯视频或者纯图片的List进行排序
     *
     * @param fileInfos
     */
    public void sequencePureDatas(List<ListingInfo.FileInfo> fileInfos) {

        Logger.i(TAG, "排序前：" + fileInfos.toString());
        List<ListingInfo.FileInfo> tempInfos = new ArrayList<ListingInfo.FileInfo>();
        int size = fileInfos.size();
        for (int i = 0; i < size; i++) {
            tempInfos.add(fileInfos.remove(size - i - 1));
        }

        for (int i = 0; i < size; i++) {

            String fileName = tempInfos.get(i).getFilename();
            Boolean isToday = isToday(fileName);
            if(!isToday) {
                fileInfos.add(tempInfos.get(i));
            }
        }
        Logger.i(TAG, "排序后：" + fileInfos.toString());
        tempInfos.clear();
        tempInfos = null;
    }

    public static Boolean isToday(String time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat currantTime = new SimpleDateFormat("HH");
        currantTime = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = currantTime.format(c.getTime());
        Logger.i(TAG, "formattedDate=" + formattedDate + ",time=" + time);
        if (formattedDate.equals(time)) {
            return true;
        }
        return false;
    }

    /**
     * 对文件按时间先后顺序进行排序
     *
     * @param fileNames
     */
    public void sequenceFileNames(Object[] fileNames) {
        for (int i = 0; i < fileNames.length - 1; i++) {
            for (int j = 0; j < fileNames.length - 1 - i; j++) {
                if (fileNames[j].toString().substring(0, 22).equals(fileNames[j + 1].toString().substring(0, 22))) {
                    if (fileNames[j].toString().length() > fileNames[j + 1].toString().length()) {
                        String temp = fileNames[j].toString();
                        fileNames[j] = fileNames[j + 1];
                        fileNames[j + 1] = temp;

                    }
                    continue;
                }

                if ((fileNames[j].toString().compareTo(fileNames[j + 1].toString())) < 0) {
                    String temp = fileNames[j].toString();
                    fileNames[j] = fileNames[j + 1];
                    fileNames[j + 1] = temp;
                }


            }
        }
    }
}
