package com.liboclass.zookeeper.application;

import com.liboclass.zookeeper.utils.SingletonZk;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 基于zk实现分布式锁
 */

public class ZKlock {

     private ZooKeeper zk;

     private CountDownLatch countDownLatch = new CountDownLatch(1);

     private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

     public ZKlock() throws IOException, InterruptedException {

          zk = new ZooKeeper(CONNECT_ADDR, 5000, new ZkWatcher());
          System.out.println(zk.getState());
          countDownLatch.await();
     }

     public static ZKlock getInstance(){
          return SingletonZk.getInstance();
     }

     //连接上就会触发watcher
     private class ZkWatcher implements Watcher {
          @Override
          public void process(WatchedEvent event) {
               System.out.println("接收到监听状态===>"+event.getState());
               if(Event.KeeperState.SyncConnected == event.getState()){
                    if(Event.EventType.None == event.getType()){
                         countDownLatch.countDown();
                         System.out.println("zk 建立连接");
                    }
               }
          }
     }
     //上锁
     public void lock(Integer id)  {
          String path = "/liboLock"+id;
          //创建临时节点防止锁无法释放,如果成功的话表示获取锁，如果失败则不断尝试
          try {
               zk.create(path,"lock".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
               System.out.println("成功获取到锁");
          } catch (Exception e) {
              e.printStackTrace();
              while (true){
                   try {
                        Thread.sleep(500);
                   } catch (InterruptedException e1) {
                        e1.printStackTrace();
                   }
                   try {
                        zk.create(path,"lock".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                   }catch (Exception e1){
                        e1.printStackTrace();
                        continue;
                   }
                   break;
              }

          }
     }

     /**
      * 释放锁
      * @param id
      */
     public void unlock(Integer id){
          String path = "/liboLock"+id;
          try {
               zk.delete(path,-1);
          } catch (InterruptedException e) {
               e.printStackTrace();
          } catch (KeeperException e) {
               e.printStackTrace();
          }
     }
}
