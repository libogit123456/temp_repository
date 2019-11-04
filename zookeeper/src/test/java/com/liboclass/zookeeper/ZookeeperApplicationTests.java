package com.liboclass.zookeeper;

import org.apache.zookeeper.*;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
/**
 * zookeeper客户端和服务端建立连接是一个异步的过程，需要用countDownLatch做一个协调
 */
public class ZookeeperApplicationTests {

    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

    private static final int SESSION_OUTTIME = 5000;

    private static final CountDownLatch coutDown = new CountDownLatch(1);

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //获取事件状态
                Event.KeeperState keeperState = event.getState();
                //获取事件的类型
                Event.EventType eventType = event.getType();
                if(keeperState.SyncConnected == keeperState){
                    if(Event.EventType.None == eventType){
                        coutDown.countDown();
                        System.out.println("zk 建立连接");
                    }

                }
            }
        });
        //进行阻塞，保证zk连接成功之后再执行后续代码
        coutDown.await();
        //创建父节点
//        String result = zk.create("/testRoot","testRoot123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        System.out.println(result);
//        //获取节点
//        byte[] data =zk.getData("/testRoot",false,null);
//        System.out.println(new String(data));
//          List<String> list = zk.getChildren("/testRoot",false);//getChildren只支持直接子节点
//          for(String node:list){
//              System.out.println(node);
//              String realPath = "/testRoot/"+node;
//              System.out.println(new String(zk.getData(realPath,false,null)));
//          }
//        //创建子节点
//        String child = zk.create("/testRoot/child","child data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);//临时节点只能保证当前会话有效
//        System.out.println(child);
////        Thread.sleep(10000);
        //version为节点数据的版本号
//        zk.delete("/testRoot", -1, new AsyncCallback.VoidCallback() {
//            @Override
//            public void processResult(int src, String path, Object ctx) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(src);
//                System.out.println(path);
//                System.out.println(ctx);
//            }
//        },"a");
         //修改节点的值
//          zk.setData("/testRoot","modify data root".getBytes(),-1);
//          byte[] data = zk.getData("/testRoot",false,null);
//          System.out.println(new String(data));

          System.out.println(zk.exists("/testRoot/a1",false));//判断节点是否存在
          zk.delete("/testRoot",-1); //删除节点(不支持递归删除)
//        Thread.sleep(3000);
        zk.close();
    }
}
