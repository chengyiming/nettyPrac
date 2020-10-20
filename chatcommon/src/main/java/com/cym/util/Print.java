package com.cym.util;

public class Print {

    /**
     * 带着线程名+类名+方法名称输出
     *
     * @param s 待输出的字符串形参
     */
    synchronized public static void tcfo(Object s) {
        String cft= "["+Thread.currentThread().getName()+"|"+ReflectionUtil.getNakeCallClassMethod()+"]";
        System.out.println(cft +   "："+ s);
    }
}
