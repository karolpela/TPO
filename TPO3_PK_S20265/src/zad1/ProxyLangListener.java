package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// This class listens for new language servers.
// When a new server connects, a socket is created
// and added to a map in the main server.

public class ProxyLangListener implements Runnable {
    private int listenerPort;
    private ProxyServer parent;

    public ProxyLangListener(int listenerPort, ProxyServer parent) {
        this.listenerPort = listenerPort;
        this.parent = parent;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listenerPort)) {
            while (true) {
                Socket langSocket = serverSocket.accept();
                BufferedReader inFromLangServer = new BufferedReader(
                        new InputStreamReader(langSocket.getInputStream()));
                String langServerName = inFromLangServer.readLine();
                System.out.println("[i] Language server \"" + langServerName + "\" connected");
                parent.addLangServer(langServerName, langSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
