package com.ctrip.ttd.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by j_zhan on 2016/11/2.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private FullHttpRequest request;
    private Channel channel;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory();
    private StringBuilder responseContent = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        this.request = msg;
        this.channel = ctx.channel();
        responseContent.setLength(0);
        responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
        responseContent.append("===================================\r\n");

        responseContent.append("VERSION: " + request.protocolVersion().text() + "\r\n");
        responseContent.append("REQUEST_URI: " + request.uri() + "\r\n\r\n");
        responseContent.append("\r\n\r\n");
        for (Map.Entry<String, String> entry : request.headers()) {
            responseContent.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
        }
        responseContent.append("\r\n\r\n");



        HttpMethod method = request.method();
        if (method.equals(HttpMethod.POST)) {
            doGet();
        } else if (method.equals(HttpMethod.GET)) {
            doPost();
        } else {
            return;
        }
        writeResponse();
    }

    private void doGet() {
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
        for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
            for (String attrVal : attr.getValue()) {
                responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
            }
        }
        responseContent.append("\r\n\r\n");
        responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
    }

    private void doPost(){
        //post请求
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
        boolean readingChunks = HttpUtil.isTransferEncodingChunked(request);
        responseContent.append("Is Chunked: " + readingChunks + "\r\n");
        responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");

        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    try {
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
            responseContent.append("\r\n\r\nEND OF POST CONTENT\r\n\r\n");
        }
    }

    private void writeHttpData(InterfaceHttpData data) {

        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                e1.printStackTrace();
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":" + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return;
            }
            if (value.length() > 100) {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":" + attribute.getName() + " data too long\r\n");
            } else {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":" + attribute.toString() + "\r\n");
            }
        }
    }


    private void writeResponse() {
        ByteBuf buf = Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);

        // Decide whether to close the connection or not.
        boolean close = request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
                                || request.protocolVersion().equals(HttpVersion.HTTP_1_0)
                                           && !request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        }


        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
