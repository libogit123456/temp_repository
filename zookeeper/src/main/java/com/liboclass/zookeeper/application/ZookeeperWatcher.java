package com.liboclass.zookeeper.application;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ZookeeperWatcher implements Watcher {
    /**定义原子变量*/
    AtomicInteger seq = new AtomicInteger();
    /**定义session失效时间**/
    private static final int SEESION_TIMEOUT = 10000;
    /**zookeeper服务器地址**/
    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";
    /**zk父路径设置**/
    private static final String PARENT_PATH = "/p";
    /**zk子路径设置**/
    private static final String CHILDREN_PATH = "/p/c";
    /**进入标识**/
    private static final String LOG_PREFIX_OF_MAIN = "【MAIN】";
    /**zk变量**/
    private ZooKeeper zk = null;
    /**用于等待zookeeper连接建立之后通知阻塞程序继续向下执行**/
    private CountDownLatch countDownLatch = new CountDownLatch(1);


    @Override
    public void process(WatchedEvent event) {
        System.out.println("进入process。。。event =" + event);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(event == null){
            return;
        }
        /**连接状态**/
        Event.KeeperState keeperState = event.getState();
        /**事件类型**/
        Event.EventType eventType = event.getType();
        /**受影响的path**/
        String path = event.getPath();
        //原子对象seq，进入process的次数
        String logPrefix = "【Watcher-"+this.seq.incrementAndGet()+"】";

        System.out.println(logPrefix+"收到watcher通知");
        System.out.println(logPrefix+"连接状态:\t"+ keeperState.toString());
        System.out.println(logPrefix+"事件类型:\t"+ eventType.toString());
        //成功连接上
        if(Event.KeeperState.SyncConnected == keeperState){
            if(Event.EventType.None == eventType){
                System.out.println(logPrefix+"成功连上zk服务器");
                countDownLatch.countDown();
            }
            //创建节点
            else if(Event.EventType.NodeCreated == eventType){
                System.out.println(logPrefix+"节点创建");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新节点
            else if(Event.EventType.NodeDataChanged == eventType){
                System.out.println(logPrefix+"节点数据更新");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新子节点
            else if(Event.EventType.NodeChildrenChanged == eventType){
                System.out.println(logPrefix+"子节点变更");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //删除节点
            else if(Event.EventType.NodeDeleted == eventType){
                System.out.println(logPrefix+"节点"+path+"被删除");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else if(Event.KeeperState.Disconnected == keeperState){
            System.out.println(logPrefix+"与zk服务器断开连接");
        }else if(Event.KeeperState.AuthFailed == keeperState){
            System.out.println(logPrefix+"权限检查失败");
        }else if(Event.KeeperState.Expired == keeperState){
            System.out.println(logPrefix+"会话失效");
        }
    }

    /**创建zk连接**/
    public void createConnection()  {
        this.releaseConnection();
        try {
            zk = new ZooKeeper(CONNECT_ADDR,SEESION_TIMEOUT,this);
            System.out.println(LOG_PREFIX_OF_MAIN +"开始连接zk服务器");
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**释放zk连接**/
    public void releaseConnection(){
        if(null != this.zk){
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**创建子节点**/
    public boolean createPath(String path,String data,boolean needWatch) {
        try {
            //设置监控,在创建path之前进行watch,创建节点之后触发watch
            this.zk.exists(path, needWatch);
            System.out.println(LOG_PREFIX_OF_MAIN+"节点创建成功,Path:"+
                               this.zk.create(path,data.getBytes(),
                                       ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT)+" ,content:"+data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**获取子节点**/
    public List<String> getChildren(String path,boolean needWatch){
        List<String> childs = null;
        try {
            System.out.println("读取子节点操作");
            childs = this.zk.getChildren(path,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childs;
    }
    /**读取指定节点内容**/
    public String readData(String path,boolean needWatch){
        System.out.println("读取节点数据操作...");
        try {
//            zk.exists(PARENT_PATH,true);
            return new String(zk.getData(path,true,null));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**更新指定节点数据**/
    private void writeData(String path, String value) {
        System.out.println("更新节点数据");
        try {
            zk.setData(path, value.getBytes(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**判断指定节点是否存在**/
    public Stat exists(String path,Boolean needWatch) {
        try {
            Stat stat = this.zk.exists(path, needWatch);
            return stat;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        ZookeeperWatcher zw = new ZookeeperWatcher();
        zw.createConnection();
        //创建节点
        if(zw.createPath(PARENT_PATH,System.currentTimeMillis()+"",true)){
            //读取数据并对该节点添加watch事件
            zw.readData(PARENT_PATH,true);
//            zw.exists(PARENT_PATH,true); //同样也可以触发watcher
            //读取子节点
            zw.getChildren(PARENT_PATH,true);
            //更新数据
            zw.writeData(PARENT_PATH,System.currentTimeMillis()+"");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zw.createPath(CHILDREN_PATH,System.currentTimeMillis()+"",true);
            zw.getChildren(CHILDREN_PATH,true);
            zw.createPath(CHILDREN_PATH+"/c1",System.currentTimeMillis()+"",true);
            zw.createPath(CHILDREN_PATH+"/c1/c2",System.currentTimeMillis()+"",true);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zw.releaseConnection();

    }


}
