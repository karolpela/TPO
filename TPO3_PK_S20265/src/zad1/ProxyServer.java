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
        // create a thread to listen for new lang server connections
        Thread listenerThread = new Thread(new ProxyLangListener(LISTENER_PORT, this));

        // handle requests from clients
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // start the listener thread
            listenerThread.start();
            // create a thread pool and listen for client connections
            ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
            while (true) {
                threadPool.execute(new ProxyClientHandler(serverSocket.accept(), this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            listenerThread.interrupt();
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
