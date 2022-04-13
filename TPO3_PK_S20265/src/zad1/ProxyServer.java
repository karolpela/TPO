package zad1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyServer implements Runnable {
    public static final int LISTENER_PORT = 8999;

    private int poolSize;
    private int port;
    private final Map<String, Socket> langServers;

    public ProxyServer(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
        langServers = new HashMap<>();
    }

    @Override
    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
        // handle requests from clients
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            threadPool.execute(new ProxyLangListener(LISTENER_PORT, this));
            while (!Thread.currentThread().isInterrupted()) {
                threadPool.execute(new ProxyClientHandler(serverSocket.accept(), this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    public void addLangServer(String name, Socket socket) {
        langServers.put(name, socket);
    }

    public Socket getLangServer(String name) {
        return langServers.get(name);
    }

    public void removeLangServer(String name) {
        langServers.remove(name);
    }

}
