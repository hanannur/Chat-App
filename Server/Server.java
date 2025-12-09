package Server;
import java.io.*;
import java.net.*;

import java.util.*;

public class Server {
    private static final int PORT = 8000;
    private static ArrayList<Socket> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started... Waiting for clients...");

        while (true) {
            Socket socket = serverSocket.accept();
            clients.add(socket);

            System.out.println("New client joined: " + socket);

            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out;

            String message;
            while ((message = br.readLine()) != null) {
                System.out.println("Received: " + message);

                // Broadcast to all clients
                for (Socket s : clients) {
                    out = new PrintWriter(s.getOutputStream(), true);
                    out.println(message);
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }
}
