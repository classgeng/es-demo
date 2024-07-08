package com.fydata.thread;

import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;

public class Consumer implements Runnable {

    private LinkedBlockingQueue queue;

    public Consumer(LinkedBlockingQueue queue){
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true){
            System.out.println("消费者消费的数据是" + queue.take());
            Thread.sleep(200);
        }
    }

}
