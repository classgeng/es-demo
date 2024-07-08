package com.fydata.thread;

import lombok.SneakyThrows;
import lombok.Synchronized;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer extends Thread{

    private LinkedBlockingQueue queue;

    public Producer(LinkedBlockingQueue queue){
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        AtomicInteger count = new AtomicInteger();
        while (true){
            queue.put(count.getAndIncrement());
            System.out.println(Thread.currentThread().getId()+ " :生产者生产了一个数据:"+count);
            Thread.sleep(200);
        }

    }

    public synchronized void aa(){

    }


}
