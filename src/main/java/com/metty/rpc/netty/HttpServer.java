package com.metty.rpc.netty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by j_zhan on 2016/11/2.
 */
public class HttpServer extends AbstractServer {

    @Override
    public void init() {
        int CPU = Runtime.getRuntime().availableProcessors();
        int coreNum = CPU * 2;
        int maxNum = CPU * 20;
        int queueNum = 5000;
        this.executorService = new ThreadPoolExecutor(coreNum, maxNum, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueNum));
    }

    @Override
    public RequestParser getRequestParser() {
        return new HttpRequestParser();
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
