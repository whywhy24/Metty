package com.metty.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ExecutorService;

/**
 * Created by j_zhan on 2016/11/2.
 */
public abstract class AbstractServer implements Server {

    private ServerBootstrap server = new ServerBootstrap();
    private EventLoopGroup parentGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    protected ExecutorService executorService;

    public AbstractServer() {
        init();
        server.group(parentGroup, workGroup).channel(NioServerSocketChannel.class);
        server.childHandler(new ChannelHandlerInitializer());
        server.handler(new LoggingHandler(LogLevel.INFO));
    }

    @Override
    public void start(int port) {
        try {
            ChannelFuture future = server.bind(port);
            future.sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class ChannelHandlerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            long timeStamp = System.currentTimeMillis();
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("decoder", new HttpRequestDecoder());
            pipeline.addLast("encoder", new HttpResponseEncoder());
            pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
            /**
             * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
             * while respecting the "Accept-Encoding" header.
             * If there is no matching encoding, no compression is done.
             */
            pipeline.addLast("compressor", new HttpContentCompressor());
            pipeline.addLast("httpHandler", new HttpHandlerProxy(AbstractServer.this, timeStamp));
        }
    }
}
