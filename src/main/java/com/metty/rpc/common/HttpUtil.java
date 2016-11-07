package com.metty.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

/**
 * Created by j_zhan on 2016/11/7.
 */
public class HttpUtil {


    public static void sendHttpResponse(Channel channel, Object response, long accessTime) {
        if (channel == null || !channel.isOpen()) {
            return;
        }
        HttpResponse httpResponse;
        if (response instanceof HttpResponse) {
            httpResponse = (HttpResponse)response;
        } else {
            HttpResponseStatus status = HttpResponseStatus.OK;
            if (response instanceof  Throwable) {
                status = response instanceof IllegalAccessException ? HttpResponseStatus.NOT_FOUND : HttpResponseStatus.BAD_REQUEST;
            }

            ByteBuf buffer = Unpooled.EMPTY_BUFFER;
            if (response != null) {
                if (response instanceof ByteBuf) {
                    buffer = Unpooled.copiedBuffer((ByteBuf)response);
                } else if (response instanceof byte[]) {
                    buffer = Unpooled.wrappedBuffer((byte[])response);
                } else {
                    buffer = Unpooled.wrappedBuffer(response.toString().getBytes(Charset.forName("UTF-8")));
                }
            }

            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.writerIndex() - buffer.readerIndex());

        }

        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        httpResponse.headers().set("Time-Used", (System.currentTimeMillis() - accessTime) + "ms");

        channel.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }
}
