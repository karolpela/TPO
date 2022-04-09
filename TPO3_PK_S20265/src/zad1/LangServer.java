package zad1;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class LangServer implements Runnable {
    private int poolSize;
    private int port;
    private Map<String, String> dictionary;

    public LangServer(int port, int poolSize, Path dictPath) throws IOException {
        this.port = port;
        this.poolSize = poolSize;
        try (Stream<String> lineStream = Files.lines(dictPath)) {
            dictionary = lineStream.collect(toMap(l -> l.split(" ")[0], l -> l.split(" ")[1]));
        } catch (IOException e) {
            System.out.println("[!] Unable to load dictionary file");
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
            while (true) {
                threadPool.execute(new LangHandler(serverSocket.accept(), dictionary));
                Thread.yield();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
