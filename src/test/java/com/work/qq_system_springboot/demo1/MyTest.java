package com.work.qq_system_springboot.demo1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

//创建线程的方式一：继承Thread类
class MyThread extends Thread{
    public void run(){
        System.out.println("myThread is running...");
    }
}

//创建线程的方式二：实现Runnable接口
class MyRunnable implements Runnable{
    public void run(){
        System.out.println("myRunnable is running...");
    }
}

//创建线程的方式三：实现Callable接口
class MyCallable implements Callable{
    @Override
    public Object call() {
        System.out.println("myCallable is running...");
        return 100;
    }
}

public class MyTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //继承Thread
        MyThread myThread = new MyThread();
        myThread.start();

        //实现Runnable接口
        Thread thread = new Thread(new MyRunnable());
        thread.start();

        //实现Callable接口
        //创建未来任务
        FutureTask futureTask = new FutureTask(new MyCallable());
        thread = new Thread(futureTask);
        thread.start();
        Object o = futureTask.get();
        System.out.println(o);


    }

}
