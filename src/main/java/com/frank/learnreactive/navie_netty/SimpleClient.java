package com.frank.learnreactive.navie_netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class SimpleClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("Connected to the server.");

            // Input and Output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // Send messages to the server
            String message;
            while (true) {
                System.out.print("Enter message (or 'exit' to quit): ");
                message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Exiting...");
                    break;
                }

                try {
                    // Send the message to the server
                    out.println(message);

                    // Read the server's response
                    String response = in.readLine();
                    if (response == null) {
                        System.out.println("Server closed the connection.");
                        break;
                    }
                    System.out.println("Server responded: " + response);
                } catch (SocketException e) {
                    System.err.println("Connection lost to the server: " + e.getMessage());
                    break;
                }
            }

            System.out.println("Client is shutting down.");
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }
}
