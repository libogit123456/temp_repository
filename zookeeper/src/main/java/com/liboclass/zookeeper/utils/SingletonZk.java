package com.liboclass.zookeeper.utils;

import com.liboclass.zookeeper.application.ZKlock;


import java.io.IOException;


public class SingletonZk {

    private static ZKlock instance;

    static {
        try {
            instance = new ZKlock();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static ZKlock getInstance(){
        return  instance;
    }
}
