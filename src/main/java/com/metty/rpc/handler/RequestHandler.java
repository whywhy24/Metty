package com.metty.rpc.handler;

import com.metty.rpc.netty.Server;
import io.netty.channel.Channel;

/**
 * Created by j_zhan on 2016/11/7.
 */
public interface RequestHandler<Request, Response> extends Runnable {
    void setServer(Server server);
    void setRequest(Request request);
    void setChannel(Channel channel);
    void setTimeStamp(long timeStamp);
    Response doRun() throws Exception;
}
