package com.liboclass.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

public class ZkClientWatcher2 {

    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

    private static final int SESSION_OUTTIME= 5000;//ms

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient(new ZkConnection(CONNECT_ADDR),5000);

        zkClient.createPersistent("/super",true);
        zkClient.createPersistent("/super/c1",true);
        zkClient.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object data) throws Exception {
                System.out.println("变更的节点为:"+path+",变更的内容为:"+data);
            }

            @Override
            public void handleDataDeleted(String path) throws Exception {
                System.out.println("删除的节点为:"+ path );
            }
        });
        Thread.sleep(3000);
        zkClient.writeData("/super","456",-1);
        Thread.sleep(1000);
        zkClient.delete("/super");
        Thread.sleep(Integer.MAX_VALUE);
    }
}
