package com.cym.util;

public class ReflectionUtil {

    /**
     * 获得调用方法的名称
     * @return
     */
    public static String getCallMethod() {
        //todo 我真是一脸震惊，居然还有这种用法
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        //获得调用的方法名称
        String methodName = stackTrace[3].getMethodName();
        return methodName;
    }


    /**
     * 返回调用方法的  类名.方法名
     * @return
     */
    public static String getNakeCallClassMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String[] className = stackTrace[3].getClassName().split("\\.");
        String fullName = className[className.length - 1] + "." +
                stackTrace[3].getMethodName();
        return fullName;
    }
}
