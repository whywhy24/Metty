package com.metty.rpc.handler;

import com.metty.rpc.common.HttpUtil;

/**
 * Created by j_zhan on 2016/11/7.
 */
public abstract class SimpleHttpRequestHandler<Response> extends HttpRequestHandler<Response> {

    @Override
    public void run() {
        Object response;
        try {
            response = doRun();
        } catch (Throwable e) {
            response = e;
        }

        try {
            HttpUtil.sendHttpResponse(channel, response, timeStamp);
        } finally {
            if (channel.isOpen()) {
                channel.close();
            }
        }
    }
}
