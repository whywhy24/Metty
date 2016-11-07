package com.metty.rpc.handler;

import com.metty.rpc.netty.Server;

/**
 * Created by j_zhan on 2016/11/7.
 */
public abstract class AbstractRequestHandler<Request, Response> implements RequestHandler<Request, Response> {

    protected long timeStamp;

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public void setServer(Server server) {

    }
}
