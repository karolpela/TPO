package zad1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class LangHandler implements Runnable {
    private final String request;
    private final Map<String, String> dictionary;

    LangHandler(String request, Map<String, String> dictionary) {
        this.request = request;
        this.dictionary = dictionary;
    }

    @Override
    public void run() {
        try {
            // process the request from proxy server
            String[] parameters = request.split(",");
            String word = parameters[0];
            String clientIp = parameters[1];
            int clientPort = Integer.parseInt(parameters[2]);

            // open connection to client
            try (Socket clientSocket = new Socket(clientIp, clientPort);
                    PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String response = dictionary.getOrDefault(word, "<No translation found>");
                outToClient.println(response);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
