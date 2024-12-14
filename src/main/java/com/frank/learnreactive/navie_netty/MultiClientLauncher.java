package com.frank.learnreactive.navie_netty;

public class MultiClientLauncher {
    public static void main(String[] args) {
        int numberOfClients = 5; // Specify how many clients to start

        for (int i = 0; i < numberOfClients; i++) {
            // Start each client in a separate thread
            new Thread(new ClientTask(i + 1)).start();
        }
    }
}
