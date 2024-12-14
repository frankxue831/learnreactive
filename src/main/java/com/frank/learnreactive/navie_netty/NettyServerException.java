package com.frank.learnreactive.navie_netty;

public class NettyServerException extends Exception {
    private static final long serialVersionUID = 1L;

    public NettyServerException(String message) {
        super(message);
    }

    public NettyServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
