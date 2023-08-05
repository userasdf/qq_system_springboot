package com.work.qq_system_springboot.jucTools;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {


    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(15);

        for(int i=0;i<15;i++){
            new Thread(()->{
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+"running...");
            }).start();
        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName()+"running...");
    }

}
