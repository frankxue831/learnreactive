package com.frank.learnreactive.navie_netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;

    @PostConstruct
    public void start() throws NettyServerException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            channelFuture = bootstrap.bind(8080).sync();
            logger.info("Netty Server started on port 8080");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NettyServerException("Server startup interrupted", e);
        } catch (Exception e) {
            logger.error("Error starting Netty Server", e);
            throw new NettyServerException("Error starting Netty Server", e);
        }
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        try {
            if (channelFuture != null) {
                channelFuture.channel().close().sync();
            }
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            logger.error("Server shutdown interrupted", e);
            Thread.currentThread().interrupt();
            throw e; // Propagate the interruption
        }
    }

    public static class NettyServerException extends Exception {
        public NettyServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}