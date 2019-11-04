package com.liboclass.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

public class ZkClientWatcher1 {

    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

    private static final int SESSION_OUTTIME= 5000;//ms

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR),5000);
        //对父节点添加监听子节点变化，内部实现了重复watcher的机制(监听自身及子节点的新增和删除操作，不监听update操作)
        zkc.subscribeChildChanges("/super", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("parentPath:"+parentPath);
                System.out.println("currentChilds:"+currentChilds);
            }
        });
        Thread.sleep(3000);
        zkc.createPersistent("/super");
        Thread.sleep(1000);
        zkc.writeData("/super","init");
        Thread.sleep(1000);
        zkc.createPersistent("/super"+"/"+"c1","c1内容");
        Thread.sleep(1000);
        zkc.createPersistent("/super"+"/"+"c2","c2内容");
        //并不会监听子节点的update操作
        Thread.sleep(1000);
        zkc.writeData("/super/c1","c1的新内容");
        zkc.delete("/super/c2");
        Thread.sleep(1000);
        zkc.deleteRecursive("/super");
        Thread.sleep(Integer.MAX_VALUE);
    }
}
