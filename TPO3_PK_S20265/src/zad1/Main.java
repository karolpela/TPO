package zad1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(new ProxyServer(9000, 10)).start();
        Thread.sleep(1000);
        try (Stream<Path> dictStream = Files.list(Paths.get("TPO3_PK_S20265/data/"))) {
            List<Path> dictionaries = dictStream.toList();
            for (Path dict : dictionaries) {
                new Thread(new LangServer(10, dict)).start();
            }
        }
        Client.main(null);
    }
}
