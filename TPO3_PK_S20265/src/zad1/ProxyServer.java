package zad1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyServer implements Runnable{
    private static final int LISTENER_PORT = 8999;

    private int poolSize;
    private int port;
    private final Map<String, Socket> langServers;

    public ProxyServer(int port, int poolSize) throws IOException {
        this.port = port;
        this.poolSize = poolSize;
        langServers = new HashMap<>();
    }

    @Override
    public void run() {
        Thread listenerThread = new Thread(new ProxyListener(LISTENER_PORT, this));

        try (ServerSocket serverSocket = new ServerSocket(port)){
            listenerThread.start();
            ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
            while (true) {
                threadPool.execute(new ProxyHandler(serverSocket.accept()));
                Thread.yield();
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

    public void removeLangServer(String name) {
        langServers.remove(name);
    }

}
