package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProxyHandler implements Runnable {
    private final Socket socket;

    private String word;
    private String lang;
    private int port;

    ProxyHandler(Socket socket) {this.socket = socket;}

    @Override
    public void run() {
        try (PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true)) {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String[] request = inFromClient.readLine().split(",");
            if(request.length != 3) {
                outToClient.println("Incorrect parameter count");
                throw new InterruptedException();
            }

            word = request[0];
            lang = request[1];
            port = Integer.parseInt(request[2]);

            // forward to language server //

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
    }
}
