package Client;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8000);
        System.out.println("Connected to server.");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Thread to read messages from server
        new Thread(() -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {}
        }).start();

        // Send messages
        String text;
        while ((text = input.readLine()) != null) {
            out.println(text);
        }
    }
}
