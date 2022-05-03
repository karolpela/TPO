package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import javafx.concurrent.Task;
import static zad1.Server.HOST;
import static zad1.Server.PORT;

public class PublisherTask extends Task<Void> {

    private SocketChannel socketChannel;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public PublisherTask(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    protected Void call() throws Exception {
        try {


            System.out.println("(Publisher) Connected to server");

            var charset = StandardCharsets.UTF_8;

            // *** allocate the buffer ***
            // allocateDirect allows for use of hardware mechanisms
            // to speed up I/O operations
            // the buffer should only be allocated *once* and reused

            ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);
            CharBuffer charBuffer;

            System.out.println("(Publisher) Saying \"Hi\" to server");
            socketChannel.write(charset.encode("Hi" + '\n'));

            while (true) {
                // clear the buffer
                inBuffer.clear();

                // read new data
                int readBytes = socketChannel.read(inBuffer);

                if (readBytes == 0) {
                    // means there's no data
                    // short term operations, eg. elapsed time
                    continue;
                }
                if (readBytes == -1) {
                    // means the channel is closed from server side
                    break;
                }

                // if there's new data
                inBuffer.flip();
                charBuffer = charset.decode(inBuffer);
                String response = charBuffer.toString();

                System.out.println("(Publisher) Got text from server: \"" + response + "\"");
                charBuffer.clear();


                switch (response) {
                    case "OK" -> {
                        System.out.println("(Publisher) Topic successfully added");
                        break;
                    }
                    case "ERROR" -> {
                        System.out.println("(Publisher) Error adding topic!");
                        break;
                    }
                    default -> {
                        System.out.println("No action specified for response \"" + response + "\"");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}