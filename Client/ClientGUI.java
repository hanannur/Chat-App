package Client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("Chat Client");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // CHAT DISPLAY AREA
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);
        add(scroll, BorderLayout.CENTER);

        // INPUT FIELD
        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);

        // Send message on ENTER
        inputField.addActionListener(e -> {
            String msg = inputField.getText();

            if (out != null) {
                out.println(msg);
            } else {
                chatArea.append("Not connected to server.\n");
            }

            inputField.setText("");
        });

        connectToServer();
        setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 8000);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            // START message listener thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String msg;
                    try {
                        while ((msg = br.readLine()) != null) {
                            final String finalMsg = msg;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatArea.append(finalMsg + "\n");
                                }
                            });
                        }
                    } catch (IOException e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                chatArea.append("Disconnected from server.\n");
                            }
                        });
                    }
                }
            }).start();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    chatArea.append("Connected to server.\n");
                }
            });

        } catch (IOException e) {
            final String errorMsg = e.getMessage();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    chatArea.append("Error connecting: " + errorMsg + "\n");
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }
}
