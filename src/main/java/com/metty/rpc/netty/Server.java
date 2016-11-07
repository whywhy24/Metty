package com.metty.rpc.netty;

import java.util.concurrent.ExecutorService;

/**
 * Created by j_zhan on 2016/11/2.
 */
public interface Server {

    void init();
    RequestParser getRequestParser();
    ExecutorService getExecutorService();
    void start(int port);
}
