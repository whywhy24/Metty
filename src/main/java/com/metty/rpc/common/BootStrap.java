package com.metty.rpc.common;

import com.metty.rpc.netty.HttpServer;

/**
 * Created by j_zhan on 2016/11/2.
 */
public class BootStrap {
    public static void main(String[] args) {
        new HttpServer().start(8080);
    }
}
