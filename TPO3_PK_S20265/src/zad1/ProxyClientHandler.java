package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProxyClientHandler implements Runnable {
    private final Socket socket;
    private final ProxyServer parent;

    ProxyClientHandler(Socket socket, ProxyServer parent) {
        this.socket = socket;
        this.parent = parent;
    }

    @Override
    public void run() {
        System.out.println(
                "[i] Client \"" + socket.getInetAddress().getHostAddress() + "\" connected");
        try (PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            String[] clientRequest = inFromClient.readLine().split(",");

            if (clientRequest == null) {
                outToClient.println("ARGERR: Bad request");
                throw new InterruptedException();
            }

            if (clientRequest.length != 3) {
                outToClient.println("ARGERR: Incorrect parameter count");
                throw new InterruptedException();
            }

            String word = clientRequest[0];
            String lang = clientRequest[1];
            int port = Integer.parseInt(clientRequest[2]);

            Socket langServerSocket = parent.getLangServer(lang);
            if (langServerSocket == null) {
                outToClient.println("LANGERR: No server for requested language");
                throw new InterruptedException();
            } else {
                outToClient.println("OK: Forwarding to language server");
            }

            String clientAddress = socket.getInetAddress().getHostAddress();
            PrintWriter outToLangServer = new PrintWriter(langServerSocket.getOutputStream(), true,
                    StandardCharsets.UTF_8);
            String langRequest = word + "," + clientAddress + "," + port;

            outToLangServer.println(langRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
