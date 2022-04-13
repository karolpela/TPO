package zad1;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class LangServer implements Runnable {
    private final int poolSize;
    private String language;
    private Map<String, String> dictionary;

    public LangServer(int poolSize, Path dictPath) throws IOException {
        this.poolSize = poolSize;
        try (Stream<String> lineStream = Files.lines(dictPath)) {
            language = dictPath.getFileName().toString().replaceFirst("[.][^.]+$", "");
            dictionary = lineStream.collect(toMap(l -> l.split(" ")[0], l -> l.split(" ")[1]));
        } catch (IOException e) {
            System.out.println("[!] Unable to load dictionary file");
        }
    }

    @Override
    public void run() {
        try (Socket proxySocket = new Socket("127.0.0.1", ProxyServer.LISTENER_PORT);
                PrintWriter outToProxy = new PrintWriter(proxySocket.getOutputStream(), true);
                BufferedReader inFromProxy =
                        new BufferedReader(new InputStreamReader(proxySocket.getInputStream(),
                                StandardCharsets.UTF_8))) {
            outToProxy.println(language);
            ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
            while (!Thread.currentThread().isInterrupted()) {
                String request = inFromProxy.readLine();
                System.out.println("[" + language + "] Got translation request: " + request);
                threadPool.execute(new LangHandler(request, dictionary));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
