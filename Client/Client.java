package Client;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        // Try-with-resources ensures streams and socket are closed automatically
        try (
            Socket socket = new Socket("localhost", 8000);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connected to server.");

            // Thread to read messages from server
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = br.readLine()) != null) {
                        System.out.println("Server: " + msg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            // Send messages
            String text;
            while ((text = input.readLine()) != null) {
                out.println(text);
            }

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
