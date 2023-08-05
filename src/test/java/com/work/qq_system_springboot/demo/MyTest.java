package com.work.qq_system_springboot.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyTest {

    public MyTest(){

    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);
        new Producer(queue).start();
        new Consumer(queue).start();
    }



}


class Producer extends Thread{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    public void run(){
        try {
            for(int i=0;i<100;i++){
                queue.put(3);
                System.out.println(this.getName()+","+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

class Consumer extends Thread{
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    public void run(){
        while(true){
            try {
                Thread.sleep(1);
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this.getName()+","+queue.size());
        }
    }
}