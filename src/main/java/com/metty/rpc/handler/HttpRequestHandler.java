package com.metty.rpc.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by j_zhan on 2016/11/7.
 */
public abstract class HttpRequestHandler<Response> extends AbstractRequestHandler<FullHttpRequest, Response> {
    protected FullHttpRequest httpRequest;
    protected Channel channel;
    protected byte[] data;

    @Override
    public void setRequest(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }
}
