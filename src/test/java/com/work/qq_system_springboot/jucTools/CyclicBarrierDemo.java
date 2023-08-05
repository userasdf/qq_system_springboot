package com.work.qq_system_springboot.jucTools;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier =
                new CyclicBarrier(7,()->{
                    System.out.println(Thread.currentThread().getName()+" 手机成功");
                });

        for(int i=0;i<7;i++){
            new Thread(()->{
                try {
                    System.out.println(Thread.currentThread().getName()+" is running...");
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        System.out.println(Thread.currentThread().getName()+" is over!");
    }

}
