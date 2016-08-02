package com.letv.leauto.cameracmdlibrary.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by lixinlei on 16/1/14.
 */
public class HashUtils {
    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static String toMD5(String plainText) {
        String value = null;
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要。
            md5.update(plainText.getBytes());
            //通过执行诸如填充之类的最终操作完成哈希计算。
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
