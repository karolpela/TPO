package zad1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static zad1.Server.HOST;
import static zad1.Server.PORT;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(2000);
        var topics = new ArrayList<>();
        try (var socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            System.out.println("(Client) Connecting to server...");

            while (!socketChannel.finishConnect()) {
                // progress bar or other operations until connected
            }

            System.out.println("(Client) Connected to server");

            var charset = StandardCharsets.UTF_8;
            var scanner = new Scanner(System.in);

            // *** allocate the buffer ***
            // allocateDirect allows for use of hardware mechanisms
            // to speed up I/O operations
            // the buffer should only be allocated *once* and reused

            ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);
            CharBuffer charBuffer;

            System.out.println("(Client) Saying \"Hi\" to server");
            socketChannel.write(charset.encode("Hi"));

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
                String fromServer = charBuffer.toString();
                var fromServerArray = fromServer.split(";");

                System.out.println("(Client) Got text from server: \"" + fromServer + "\"");
                charBuffer.clear();

                String command = fromServerArray[0];
                String arguments = null;
                if (fromServerArray.length > 1) {
                    arguments = fromServerArray[1];
                }

                switch (command) {
                    case "Bye" -> {
                        break;
                    }
                    case "ADD_TOPIC" -> {
                        String topic = arguments;
                        topics.add(topic);
                        System.out.println("(Client) Added new topic \"" + topic + "\"");
                    }
                    case "REMOVE_TOPIC" -> {
                        String topic = arguments;
                        topics.remove(topic);
                        System.out.println("(Client) Removed topic \"" + topic + "\"");

                    }
                    default -> {
                        System.out.println(
                                "(Client) No action specified for command \"" + command + "\"");
                    }
                }

                // prepare response
                String input = scanner.nextLine();
                charBuffer = CharBuffer.wrap(input + '\n');
                ByteBuffer outBuffer = charset.encode(charBuffer);

                // send response
                socketChannel.write(outBuffer);
                System.out.println("(Client) Writing to server: \"" + input + "\"");

            }

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
