package com.work.qq_system_springboot.demo1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket{
    private int num = 30;

    public void sale(){

        Lock lock = new ReentrantLock(true);
        try {
            lock.lock();
            if(num>0){
                num--;
                System.out.println(Thread.currentThread().getName()+","+num);
            }else{
                System.out.println(Thread.currentThread().getName()+",sale out!");
            }
        }finally {
            lock.unlock();
        }


    }
}

public class ThreadDemo01 {

    public static void main(String[] args) {

        Ticket ticket = new Ticket();

        new Thread(()->{
            for(int i=0;i<30;i++)
            ticket.sale();
        }).start();

        new Thread(()->{
            for(int i=0;i<30;i++)
                ticket.sale();
        }).start();

        new Thread(()->{
            for(int i=0;i<30;i++)
                ticket.sale();
        }).start();
    }

}
