package com.cym.iodemo.reactor.multi_thread_reactor;

import java.util.concurrent.atomic.AtomicInteger;

public class Prac {
    static AtomicInteger atomicInteger = new AtomicInteger(3);

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++) {
            System.out.println(atomicInteger.getAndIncrement());
        }
    }

}
