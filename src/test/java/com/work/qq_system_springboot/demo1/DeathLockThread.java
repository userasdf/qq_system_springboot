package com.work.qq_system_springboot.demo1;

public class DeathLockThread {

    //资源a
    public static Object a = new Object();
    //资源b
    public static Object b = new Object();

    public static void main(String[] args) {


        //线程A拥有资源a，在尝试获取资源b
        new Thread(()->{
            synchronized (a){
                System.out.println(Thread.currentThread()+",持有资源：a，尝试获取资源b");
                //尝试获取资源b
                try {
                    Thread.sleep(1000);//等待一秒，先让线程b只有资源b
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b){
                    System.out.println(Thread.currentThread()+",持有资源：b");
                }
            }
        }).start();
        //线程B拥有资源b，在尝试获取资源a
        new Thread(()->{
            synchronized (b){
                System.out.println(Thread.currentThread()+",持有资源：b，尝试获取资源a");
                //尝试获取资源b
                synchronized (a){
                    System.out.println(Thread.currentThread()+",持有资源：a");
                }
            }
        }).start();

    }

}
