package com.cym;


//todo pom.xml文件中没有这个模块，为什么可以导入
import com.cym.util.Logger;


public class JoinDemo {
    private static final int SLEEP_GAP = 500;
    private static String getCurThreadName() {
        return Thread.currentThread().getName();
    }
    class BoilWater extends Thread {
        BoilWater() {
            super("**烧水线程");
        }

        @Override
        public void run() {
            try {
                Logger.info("洗好水壶");
                Logger.info("灌上凉水");
                Logger.info("放在火上");
                //线程睡眠，表示正在烧水
                Thread.sleep(SLEEP_GAP);
                Logger.info("水烧开了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.info("运行结束");
        }
    }

    class CleanCups extends Thread {
        CleanCups() {
            super("**清洗线程");
        }

        @Override
        public void run() {
            try {
                Logger.info("洗茶壶");
                Logger.info("洗茶杯");
                Logger.info("拿茶叶");
                //睡眠
                Thread.sleep(SLEEP_GAP);
                Logger.info("洗完了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.info("运行结束");
        }
    }

    public static void main(String[] args) {
        JoinDemo joinDemo = new JoinDemo();
        CleanCups cleanCups = joinDemo.new CleanCups();
        BoilWater boilWater = joinDemo.new BoilWater();
        cleanCups.start();
        boilWater.start();
        try {
            cleanCups.join();
            boilWater.join();
            Logger.info("烧水完毕，清洗完毕，开始泡茶....");
            Thread.sleep(SLEEP_GAP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
