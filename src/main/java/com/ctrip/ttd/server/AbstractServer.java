package com.ctrip.ttd.server;

import com.ctrip.ttd.server.handler.ChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by j_zhan on 2016/11/2.
 */
public abstract class AbstractServer implements Server {

    private ServerBootstrap server = new ServerBootstrap();
    private EventLoopGroup parentGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    public AbstractServer() {
        server.group(parentGroup, workGroup).channel(NioServerSocketChannel.class);
        server.childHandler(new ChannelHandler());
    }

    @Override
    public void init() {

    }

    @Override
    public void start(int port) {
        try {
            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}