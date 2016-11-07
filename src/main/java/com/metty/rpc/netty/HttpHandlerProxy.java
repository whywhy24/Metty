package com.metty.rpc.netty;

import com.metty.rpc.common.HttpUtil;
import com.metty.rpc.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by j_zhan on 2016/11/2.
 */
public class HttpHandlerProxy extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Server httpServer;
    private long timeStamp;

    public HttpHandlerProxy(Server httpServer, long timeStamp) {
        this.httpServer = httpServer;
        this.timeStamp = timeStamp;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        QueryStringDecoder query = new QueryStringDecoder(request.uri());
        Map<String, List<String>> paramMap = query.parameters();
        for (Map.Entry<String, List<String>> attr : paramMap.entrySet()) {
            for (String attrVal : attr.getValue()) {
                //responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
            }
        }

        RequestParser requestParser = httpServer.getRequestParser();
        RequestHandler requestHandler = requestParser.parse(request);
        requestHandler.setRequest(request);
        requestHandler.setTimeStamp(timeStamp);
        requestHandler.setChannel(ctx.channel());
        httpServer.getExecutorService().submit(requestHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        HttpUtil.sendHttpResponse(ctx.channel(), cause, timeStamp);
    }
}
