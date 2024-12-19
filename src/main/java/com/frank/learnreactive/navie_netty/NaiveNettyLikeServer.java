package com.frank.learnreactive.navie_netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NaiveNettyLikeServer {
    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(8080));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("[Server] Naive Netty-Like Server started on port 8080");

            while (true) {
                System.out.println("[Server] Waiting for events...");
                selector.select();

                printSelectorInfo(selector); // Log selector information

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(selector, key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                    } catch (IOException e) {
                        System.err.println("[Server] Error handling key: " + e.getMessage());
                        key.channel().close();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Critical error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = server.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        System.out.println("[Server] Accepted connection from " + clientChannel.getRemoteAddress() +
                " (Thread: " + Thread.currentThread().getName() + ")");
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("[Server] Client disconnected: " + clientChannel.getRemoteAddress() +
                    " (Thread: " + Thread.currentThread().getName() + ")");
            return;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        String message = new String(data);

        // Prevent feedback loop by ignoring echoed messages
        if (message.startsWith("Echo:")) {
            System.out.println("[Server] Ignoring echoed message: " + message);
            buffer.clear();
            return;
        }

        System.out.println("[Server] Received: \"" + message + "\" from " + clientChannel.getRemoteAddress() +
                " (Thread: " + Thread.currentThread().getName() + ")");

        // Echo the message back to the client
        buffer.clear();
        String response = "Echo: " + message;
        buffer.put(response.getBytes());
        buffer.flip();

        try {
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
            System.out.println("[Server] Sent: \"" + response + "\" to " + clientChannel.getRemoteAddress() +
                    " (Thread: " + Thread.currentThread().getName() + ")");
        } catch (IOException e) {
            System.err.println("[Server] Error writing to client: " + clientChannel.getRemoteAddress());
            clientChannel.close();
        }
    }

    private static void printSelectorInfo(Selector selector) {
        Set<SelectionKey> keys = selector.keys();
        System.out.println("[Selector] Number of registered channels: " + keys.size());
        for (SelectionKey key : keys) {
            String channelType = key.channel() instanceof ServerSocketChannel ? "ServerSocketChannel" : "SocketChannel";
            System.out.println("[Selector] Channel: " + key.channel() +
                    ", Type: " + channelType +
                    ", Interest Ops: " + opsToString(key.interestOps()) +
                    ", Ready Ops: " + opsToString(key.readyOps()));
        }
    }

    private static String opsToString(int ops) {
        StringBuilder sb = new StringBuilder();
        if ((ops & SelectionKey.OP_ACCEPT) != 0) sb.append("OP_ACCEPT ");
        if ((ops & SelectionKey.OP_CONNECT) != 0) sb.append("OP_CONNECT ");
        if ((ops & SelectionKey.OP_READ) != 0) sb.append("OP_READ ");
        if ((ops & SelectionKey.OP_WRITE) != 0) sb.append("OP_WRITE ");
        return sb.toString().trim();
    }
}
