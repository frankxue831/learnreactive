package com.frank.learnreactive.navie_netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImprovedNettyLikeServer {
    // Thread pool for handling client tasks
    private static final ExecutorService workerPool = Executors.newFixedThreadPool(10); // Adjust thread count as needed

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(8080));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("[Server] Improved Netty-Like Server started on port 8080");

            while (true) {
                System.out.println("[Server] Waiting for events...");
                selector.select();

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(selector, key);
                        } else if (key.isReadable()) {
                            // Delegate the read operation to a worker thread
                            handleReadWithWorkerThread(key);
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
        System.out.println("[Server] Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void handleReadWithWorkerThread(SelectionKey key) {
        // Submit the read task to the worker pool
        workerPool.submit(() -> {
            try {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                int bytesRead = clientChannel.read(buffer);

                if (bytesRead == -1) {
                    clientChannel.close();
                    System.out.println("[Server] Client disconnected: " + clientChannel.getRemoteAddress());
                    return;
                }

                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                String message = new String(data);

                System.out.println("[Worker Thread] Received: \"" + message + "\" from " + clientChannel.getRemoteAddress());

                // Prevent feedback loop by ignoring echoed messages
                if (message.startsWith("Echo:")) {
                    System.out.println("[Worker Thread] Ignoring echoed message: " + message);
                    buffer.clear();
                    return;
                }

                // Prepare the response
                buffer.clear();
                String response = "Echo: " + message;
                buffer.put(response.getBytes());
                buffer.flip();

                // Write the response back to the client
                while (buffer.hasRemaining()) {
                    clientChannel.write(buffer);
                }
                System.out.println("[Worker Thread] Sent: \"" + response + "\" to " + clientChannel.getRemoteAddress());
            } catch (IOException e) {
                System.err.println("[Worker Thread] Error handling client: " + e.getMessage());
            }
        });
    }
}
