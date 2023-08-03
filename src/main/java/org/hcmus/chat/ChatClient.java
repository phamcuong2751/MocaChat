package org.hcmus.chat;

import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public void startClient() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Read the welcome message from the server
            System.out.println(in.readLine());

            System.out.print("Enter your username: ");
            String username = userInput.readLine();
            out.println(username);

            final String[] serverMessage = {""};
            Thread receiverThread = new Thread(() -> {
                try {
                    while ((serverMessage[0] = in.readLine()) != null) {
                        System.out.println(serverMessage[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            // Read user input and send messages to the server
            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                out.println(userInputMessage);
            }

            // Clean up resources
            in.close();
            out.close();
            userInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.startClient();
    }
}
