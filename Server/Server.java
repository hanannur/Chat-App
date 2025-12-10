
package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 8000;
    // Thread-safe list for client writers
    private static CopyOnWriteArrayList<PrintWriter> writers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server starting...");
        System.out.println("Listening on port " + PORT);

        // Thread to read server console input and broadcast to clients
        new Thread(() -> {
            try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = console.readLine()) != null) {
                    broadcast("Server: " + line);
                }
            } catch (IOException e) {
                System.out.println("Console reader stopped: " + e.getMessage());
            }
        }, "Console-Broadcaster").start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started... Waiting for clients...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client joined: " + socket);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                writers.add(out);

                // Handle client in a new thread
                new Thread(() -> handleClient(socket, out)).start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket, PrintWriter out) {
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String message;
            while ((message = br.readLine()) != null) {
                System.out.println("Received from " + socket.getPort() + ": " + message);
                broadcast("Client " + socket.getPort() + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
            writers.remove(out);
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter w : writers) {
            try {
                w.println(message);
            } catch (Exception ignored) {}
        }
    }
}