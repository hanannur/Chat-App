package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ServerGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerGUI() {
        setTitle("Chat Server");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input panel
        JPanel panel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        // Send message action
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        setVisible(true);

        // Start server in a new thread
        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(8000);
            chatArea.append("Server started. Waiting for client...\n");
            clientSocket = serverSocket.accept();
            chatArea.append("Client connected!\n");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String msg;
            while ((msg = in.readLine()) != null) {
                chatArea.append("Client: " + msg + "\n");
            }
        } catch (IOException e) {
            chatArea.append("Server error: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String msg = inputField.getText();
        if (msg.isEmpty() || out == null) return;
        chatArea.append("Server: " + msg + "\n");
        out.println(msg);
        inputField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
