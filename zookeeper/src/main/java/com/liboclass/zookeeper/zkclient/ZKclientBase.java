package com.liboclass.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

public class ZKclientBase {

    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

    private static final int SESSION_OUTTIME= 5000;//ms

    public static void main(String[] args) {
            ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR),5000);
          //create和delete方法
//          zkc.createEphemeral("/temp");
            //1.创建节点和内容
            zkc.createPersistent("/super","super内容");
            zkc.createPersistent("/super/c1","c1内容");//可以递归创建节点，但是只能手动赋值
            zkc.createPersistent("/super/c2","c2内容");
//          zkc.deleteRecursive("/super");//递归删除
            //2.设置data和path，并且读取子节点和每个节点的内容
            List<String> list = zkc.getChildren("/super");
            for(String p : list){
                System.out.println(p);
                String rp = "/super/"+p;
                String data = zkc.readData(rp);
                System.out.println("节点为"+rp+",内容为"+data);
            }
           //3.更新和判断节点是否存在
           zkc.writeData("/super/c1","新内容");
           System.out.println((String) zkc.readData("/super/c1"));
           System.out.println(zkc.exists("/super/c1"));
          try {
            Thread.sleep(100);
          }catch (InterruptedException e) {
            e.printStackTrace();
         }
    }

}
