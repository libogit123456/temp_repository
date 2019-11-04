package com.liboclass.zookeeper;

import com.liboclass.zookeeper.application.ZKlock;
import com.liboclass.zookeeper.utils.SingletonZk;

import java.util.concurrent.CountDownLatch;


public class ZKLockTest {

    private static int num = 0;

    private static CountDownLatch countDownLatch = new CountDownLatch(10);

    private static ZKlock lock = ZKlock.getInstance();


    public static void inCreate() {
        lock.lock(1);
        num++;
        System.out.println(num);
        lock.unlock(1);
    }



    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    inCreate();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //每个线程执行完成之后，调用countdownLatch
                countDownLatch.countDown();
            }).start();
        }

        while (true) {
            if (countDownLatch.getCount() == 0) {
                System.out.println(num);
                break;
            }
        }
    }

}
