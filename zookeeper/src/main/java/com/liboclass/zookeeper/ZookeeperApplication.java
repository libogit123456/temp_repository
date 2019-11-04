package com.liboclass.zookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZookeeperApplication {
    //this is a test
    private static final String CONNECT_ADDR = "192.168.183.100:2181,192.168.183.101:2181,192.168.183.102:2181";

    private static final int SESSION_OUTTIME= 5000;//ms

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplication.class, args);
    }

}
