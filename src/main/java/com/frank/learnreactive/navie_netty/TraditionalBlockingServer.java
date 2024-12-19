package com.frank.learnreactive.navie_netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TraditionalBlockingServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("[Server] Traditional Blocking Server started on port 8080");

            while (true) {
                System.out.println("[Server] Waiting for a client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Accepted connection from " + clientSocket.getInetAddress() + ", Thread: " + Thread.currentThread().getName());

                // Handle the client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        System.out.println("[Server] Thread started for client: " + clientSocket.getInetAddress() + ", Thread: " + Thread.currentThread().getName());
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            System.out.println("[Server] Handling client: " + clientSocket.getInetAddress() + ", Thread: " + Thread.currentThread().getName());

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[Server] Received from client: " + message + ", Thread: " + Thread.currentThread().getName());
                String response = "Echo: " + message;
                out.println(response);
                System.out.println("[Server] Sent to client: " + response + ", Thread: " + Thread.currentThread().getName());
            }

            System.out.println("[Server] Client disconnected: " + clientSocket.getInetAddress() + ", Thread: " + Thread.currentThread().getName());
        } catch (IOException e) {
            System.err.println("[Server] Error handling client: " + e.getMessage() + ", Thread: " + Thread.currentThread().getName());
        } finally {
            try {
                clientSocket.close();
                System.out.println("[Server] Connection closed for client: " + clientSocket.getInetAddress() + ", Thread: " + Thread.currentThread().getName());
            } catch (IOException e) {
                System.err.println("[Server] Error closing client socket: " + e.getMessage() + ", Thread: " + Thread.currentThread().getName());
            }
        }
    }
}
