package zad1;

import static zad1.Server.HOST;
import static zad1.Server.PORT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class PublisherThread implements Runnable {

    // private socketChannel;

    // public PublisherThread(SocketChannel socketChannel) {
    // this.socketChannel = socketChannel;
    // }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            System.out.println("(Publisher) Connecting to server...");

            while (!socketChannel.finishConnect()) {
                // progress bar or other operations until connected
            }

            System.out.println("(Publisher) Connected to server");

            var charset = StandardCharsets.UTF_8;
            // var scanner = new Scanner(System.in);

            // *** allocate the buffer ***
            // allocateDirect allows for use of hardware mechanisms
            // to speed up I/O operations
            // the buffer should only be allocated *once* and reused

            ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);
            CharBuffer charBuffer;

            System.out.println("(Client) Saying \"Hi\" to server");
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

                System.out.println("(Client) Got text from server: \"" + response + "\"");
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
