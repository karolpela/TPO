package zad1;

import java.io.IOException;
import java.net.ServerSocket;

public class ProxyListener implements Runnable {
    private int listenerPort;
    private ProxyServer parent;

    public ProxyListener(int listenerPort, ProxyServer parent) {
        this.listenerPort = listenerPort;
        this.parent = parent;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listenerPort)){
            while (true) {

                Thread.yield();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
