package com.cym.util;


public class Logger {


    /**
     * 带着方法名称输出，方法名称放在前面
     */
    public static void debug(Object s) {
        String content = null;
        //todo common-lang3的导入过程出现了一些问题，关于maven，仔细查
        if(null != s) {
            content = s.toString().trim();
        } else {
            content = "";
        }
        // TODO 不明白这种表达
        String out = String.format("%20s |>  %s ", ReflectionUtil.getCallMethod(), content);
        System.out.println(out);
    }

    //todo 问什么标准答案的这里有一个synchronized
    public static void info(Object s) {
        String content = null;
        if(null != s) {
            content = s.toString().trim();
        } else {
            content = "";
        }
        String cft = "[" +
                Thread.currentThread().getName() + "|" +
                ReflectionUtil.getNakeCallClassMethod() + "]";
        String out = String.format("%20s |> %s ", cft, content);
        System.out.println(out);
    }

    //todo 问什么标准答案的这里有一个synchronized
    public static void info(Object... s) {
        StringBuilder content = new StringBuilder();
        for(int i = 0; i < s.length; i++) {
            content.append(s[i] != null?s[i].toString(): " null");
            content.append(" ");
        }
        String cft = "[" +
                Thread.currentThread().getName() + "|" +
                ReflectionUtil.getNakeCallClassMethod() + "]";
        String out = String.format("%20s |> %s ", cft, content);
        System.out.println(out);
    }
}
