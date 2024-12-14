package com.frank.learnreactive.navie_netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Channel {
    private final SocketChannel socketChannel;

    public Channel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void write(String data) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        socketChannel.write(buffer);
    }

    public String read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            return new String(data);
        }
        return null;
    }
}

