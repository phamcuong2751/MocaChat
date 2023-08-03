package org.hcmus.chat;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 8080;
    private Map<String, Socket> connectedClients;

    public ChatServer() {
        connectedClients = new HashMap<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started and listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Authentication process
            out.println("Welcome to the chat server!");
            out.println("Enter your username: ");
            String username = in.readLine();

            synchronized (connectedClients) {
                connectedClients.put(username, clientSocket);
            }

            out.println("Hello, " + username + "! You are now connected.");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + ": " + message);

                // Broadcast the received message to all connected clients (excluding the sender)
                synchronized (connectedClients) {
                    for (Map.Entry<String, Socket> entry : connectedClients.entrySet()) {
                        String recipient = entry.getKey();
                        Socket friendSocket = entry.getValue();

                        if (!recipient.equals(username)) {
                            PrintWriter friendOut = new PrintWriter(friendSocket.getOutputStream(), true);
                            friendOut.println(username + ": " + message);
                        }
                    }
                }
            }

            // Clean up resources
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.startServer();
    }
}
