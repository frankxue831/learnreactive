package com.frank.learnreactive.navie_netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // Handle the message
            ctx.writeAndFlush(msg);
        } finally {
            // Release the message when done
            // ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Channel error occurred", cause);
        ctx.close();
    }
}