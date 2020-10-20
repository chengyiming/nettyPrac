package com.cym.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;

public class IOUtil {


    /**
     * 取得当前类路径下的resName资源的完整路径
     * url.getPath()获取到的路径被utf-8编码了
     * 需要使用URLDecoder.decode(path, "utf-8")
     * @param resName
     * @return
     */
    public static String getResourcePath(String resName) {
        //todo 什么才能被叫做类路径下的资源， 这里得到的是什么
        URL url = IOUtil.class.getResource(resName);
        String path = url.getPath();
        String decodePath = null;
        try {
            decodePath = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            //todo 在工业界，处理异常的方式是什么
            e.printStackTrace();
        }
        return decodePath;
    }


    /**
     * 只是为了统一处理抛出的错误
     * @param o
     */
    public static void closeQuietly(Closeable o) {
        if(null == o) {
            return;
        }
        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static DecimalFormat fileSizeFormater = FormatUtil.decimalFormat(1);
    /**
     * 格式化文件的大小
     * @param length
     * @return
     */
    public static String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return fileSizeFormater.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return fileSizeFormater.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return fileSizeFormater.format(size) + "KB";
        }
        return length + "B";
    }
}
