package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class LangHandler implements Runnable {
    private final Socket socket;
    private Map<String, String> dictionary;

    LangHandler(Socket socket, Map<String, String> dictionary) {
        this.socket = socket;
        this.dictionary = dictionary;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromProxy = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // process the request from proxy server
            String[] request = inFromProxy.readLine().split(",");
            String word = request[0];
            String clientIp = request[1];
            int clientPort = Integer.parseInt(request[2]);

            // open connection to client

            try (Socket clientSocket = new Socket(clientIp, clientPort);
                    PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String response = dictionary.getOrDefault(word, "No translation found");

                outToClient.println(response);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
