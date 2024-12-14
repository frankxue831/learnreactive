package com.frank.learnreactive.navie_netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class ServerBootstrap {
    private final EventLoop eventLoop;

    public ServerBootstrap() throws IOException {
        this.eventLoop = new EventLoop();
    }

    public void bind(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        eventLoop.registerChannel(serverChannel);
        new Thread(eventLoop).start();
        System.out.println("Server started on port " + port);
    }

    public static void main(String[] args) throws IOException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.bind(8080);
    }
}

