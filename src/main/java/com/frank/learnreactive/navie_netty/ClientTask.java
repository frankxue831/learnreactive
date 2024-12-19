package com.frank.learnreactive.navie_netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTask implements Runnable {
    private final int clientId;

    public ClientTask(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("Client " + clientId + " connected to the server.");

            // Input and Output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send and receive a few messages
            for (int i = 0; i < 3; i++) {
                String message = "Hello from client " + clientId + ", message " + (i + 1);
                out.println(message);

                // Wait for the server's response
                String response = in.readLine();
                System.out.println("Client " + clientId + " received: " + response);
            }

            System.out.println("Client " + clientId + " shutting down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
