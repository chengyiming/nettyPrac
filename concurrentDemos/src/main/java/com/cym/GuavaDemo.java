package com.cym;


import com.cym.util.Logger;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GuavaDemo {
    private static final int SLEEP_GAP = 500;
    private static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    static class BoilWater implements Callable<Boolean> {

        @Override
        public Boolean call() throws InterruptedException {
            Logger.info("洗好水壶");
            Logger.info("灌上凉水");
            Logger.info("放在火上");
            //线程睡眠，表示正在烧水
            Thread.sleep(SLEEP_GAP);
            Logger.info("水烧开了");
            Logger.info("运行结束");
            return true;
        }
    }

    static class CleanCups implements Callable<Boolean> {

        @Override
        public Boolean call() throws InterruptedException {
            Logger.info("洗茶壶");
            Logger.info("洗茶杯");
            Logger.info("拿茶叶");
            //睡眠
            Thread.sleep(SLEEP_GAP);
            Logger.info("洗完了");
            Logger.info("运行结束");
            return true;
        }
    }

    static class MainJob implements Runnable {
        boolean isWaterboil = false;
        boolean isCupClean = false;
        int gap = SLEEP_GAP / 10;
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(gap);
                    Logger.info("reading....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isCupClean && isWaterboil) {
                    isWaterboil= false;
                    gap = SLEEP_GAP*1000;
                    Logger.info("烧水成功，正在喝水....");
                }
            }
        }
    }


    public static void main(String[] args) {
        /*ThreadPoolExecutor jdkThreadPool = new ThreadPoolExecutor(2,
                4,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                (r)-> {
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    return new Thread(r, "pool-" +
                            Thread.currentThread().getName()+
                            atomicInteger.getAndIncrement());}
        );*/

        MainJob mainJob = new MainJob();
        new Thread(mainJob).start();

        //创建java 线程池
        ExecutorService jdkThreadPool =
                Executors.newFixedThreadPool(10);
        ListeningExecutorService guavaThreadPool =
                MoreExecutors.listeningDecorator(jdkThreadPool);

        Callable<Boolean> boilWater = new BoilWater();
        Callable<Boolean> cleanCups = new CleanCups();
        ListenableFuture<Boolean> boilwaterFuture = guavaThreadPool.submit(boilWater);
        ListenableFuture<Boolean> cleancupsFuture = guavaThreadPool.submit(cleanCups);
        Futures.addCallback(boilwaterFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    mainJob.isWaterboil = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.info("烧水失败...");
            }
        });
        Futures.addCallback(cleancupsFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    mainJob.isCupClean = true;
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Logger.info("清洗失败....");
            }
        });
    }
}
