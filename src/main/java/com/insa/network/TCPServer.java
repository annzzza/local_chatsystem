package com.insa.network;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer extends Thread {

    /** Observer interface for the TCP server **/
    public interface TCPServerObserver {
        /** Called when a new message is received **/
        void onNewMessage(TCPMessage message);
    }

    private final ServerSocket socket;
    private static final List<TCPServerObserver> observerList = new ArrayList<>();

    public TCPServer(int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    /** Adds a new observer to the class, for which the onNewMessage method will be called when a new message is received **/
    public void addObserver(TCPServerObserver observer) {
        synchronized (observerList) {
            observerList.add(observer);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Accept new client
                new ClientHandler(socket.accept()).start();
            } catch (IOException e) {
                System.err.println("Error while accepting client: " + e.getMessage());
            }
        }
    }

    /**
     * Class handling a client connection
     */
    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                // Open streams
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                // Read message
                String inputLine = in.readLine();
                TCPMessage receivedMessage = new Gson().fromJson(inputLine, TCPMessage.class);

                // Notify observers
                synchronized (observerList) {
                    for (TCPServerObserver observer : observerList) {
                        observer.onNewMessage(receivedMessage);
                    }
                }

                // Close everything
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error while handling client: " + e.getMessage());
            }
        }
    }
}
