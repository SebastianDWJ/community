package com.mybbs.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@SpringBootTest
public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);//阻塞队列长度为10，ArrayBlockingQueue：用数组实现队列
        new Thread(new Producer(queue)).start();//启动生产者线程1
        new Thread(new Consumer(queue)).start();//启动消费者线程1
        new Thread(new Consumer(queue)).start();//启动消费者线程2
        new Thread(new Consumer(queue)).start();//启动消费者线程3
    }
}

//生产者线程
//继承Runnable接口来实现多线程
class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    //有参构造器：实例化Producer时把阻塞队列BlockingQueue传进来
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    //实现接口的run方法
    @Override
    public void run() {
        try {
            //模拟不断往阻塞队列传数据
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

//消费者线程
//继承Runnable接口来实现多线程
class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    // //有参构造器：实例化Producer时把阻塞队列BlockingQueue传进来
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //模拟不断从阻塞队列取数据
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}