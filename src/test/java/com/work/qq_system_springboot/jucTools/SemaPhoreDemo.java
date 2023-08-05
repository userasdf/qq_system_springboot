package com.work.qq_system_springboot.jucTools;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class SemaPhoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);//三个停车位

        for(int i=0;i<7;i++){
            new Thread(()->{
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName()+"获得了停车位");
                    Thread.sleep(new Random().nextInt(10));
                    System.out.println(Thread.currentThread().getName()+"-----释放了停车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            }).start();
        }
    }

}
