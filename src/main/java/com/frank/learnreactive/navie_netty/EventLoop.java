package com.frank.learnreactive.navie_netty;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

public class EventLoop implements Runnable {
    private final Selector selector;

    public EventLoop() throws IOException {
        this.selector = Selector.open();
    }

    public void registerChannel(ServerSocketChannel serverChannel) throws IOException {
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        System.out.println("Connection Accepted: " + client.getRemoteAddress());
                    }
                }
                selector.selectedKeys().clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

