package com.cym;

import com.cym.util.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/*
todo
在主函数中，如果不创建静态类，怎么写？？
 */

public class FutureTaskDemo {
    private static final int SLEEP_GAP = 500;
    private static String getCurThreadName() {
        return Thread.currentThread().getName();
    }
    class BoilWater implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {
                Logger.info("洗好水壶");
                Logger.info("灌上凉水");
                Logger.info("放在火上");
                //线程睡眠，表示正在烧水
                Thread.sleep(SLEEP_GAP);
                Logger.info("水烧开了");
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            Logger.info("运行结束");
            return true;
        }
    }

    class CleanCups implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {
                Logger.info("洗茶壶");
                Logger.info("洗茶杯");
                Logger.info("拿茶叶");
                //睡眠
                Thread.sleep(SLEEP_GAP);
                Logger.info("洗完了");
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            Logger.info("运行结束");
            return true;
        }
    }

    public static void main(String[] args) {
        JoinDemo joinDemo = new JoinDemo();
        /*Callable<Boolean> cleanCups = new CleanCups();
        Callable<Boolean> boilWater = new BoilWater();*/
        JoinDemo.CleanCups cleanCups = joinDemo.new CleanCups();
        JoinDemo.BoilWater boilWater = joinDemo.new BoilWater();
        FutureTask<Boolean> cleanCupsFutureTask = new FutureTask<Boolean>((Callable<Boolean>) cleanCups);
        FutureTask<Boolean> boilwaterFutureTask = new FutureTask<Boolean>((Callable<Boolean>) boilWater);
        new Thread(cleanCupsFutureTask).start();
        new Thread(boilwaterFutureTask).start();
        try {
            boolean isCleanFinish = cleanCupsFutureTask.get();
            boolean isBoilwaterOver = boilwaterFutureTask.get();
            if(isBoilwaterOver && isCleanFinish) {
                Logger.info("烧水完毕，清洗完毕，开始泡茶....");
                Thread.sleep(SLEEP_GAP);
            } else if(isBoilwaterOver) {
                Logger.info("清洗出现了问题");
            } else {
                Logger.info("烧水出现了问题");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
