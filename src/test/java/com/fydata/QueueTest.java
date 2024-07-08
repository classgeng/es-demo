package com.fydata;

import com.fydata.thread.Consumer;
import com.fydata.thread.Producer;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueTest {

    private static LinkedBlockingQueue queue = new LinkedBlockingQueue();

    public static void main(String[] args) {
        Producer producer = new Producer(queue);
        producer.start();

        Consumer consumer = new Consumer(queue);
        consumer.run();
    }


}
