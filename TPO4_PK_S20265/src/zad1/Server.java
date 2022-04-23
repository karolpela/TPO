package zad1;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final String HOST = "localhost";
    private static final int PORT = 9000;

    public static void main(String[] args) throws IOException {
        new Server();
    }

    Server() throws IOException {
        try (var serverSocketChannel = ServerSocketChannel.open()) {
            // create server socket channel and bind to specified address
            serverSocketChannel.bind(new InetSocketAddress(HOST, PORT));

            // set non-blocking mode
            serverSocketChannel.configureBlocking(false);

            // create and register selector to accept connections
            var selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println(this + " Waiting for connections...");

            // main server loop
            while (true) {
                // select a set of keys once there are ready ones
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                // iterate over the selected key set
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    // get the key and remove it from further iterations
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    /* perform main actions */

                    // if there's a new client connection
                    if (key.isAcceptable()) {
                        System.out.println(this + " got a new connection");

                        // create a channel to communicate with client
                        // "accept() is non-blocking since client's already waiting"
                        var socketChannel = serverSocketChannel.accept();

                        // configure and register the channel
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, OP_READ | OP_WRITE);

                        continue;
                    }

                    // if there's a ready-to-read channel
                    if (key.isReadable()) {
                        var socketChannel = (SocketChannel) key.channel();
                        handleRequest(socketChannel);
                        continue;
                    }

                    // if there's a ready-to-write channel
                    if (key.isWritable()) {
                        // var socketChannel = (SocketChannel) key.channel();

                        // TODO handle response to client

                        // broadcast here probably
                        continue;
                    }
                }
            }
        }
    }

    // buffer configuration
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
    // private StringBuffer stringBuffer = new StringBuffer();
    private StringBuilder sb = new StringBuilder();

    private void handleRequest(SocketChannel socketChannel) {
        // return if channel is closed
        if (!socketChannel.isOpen())
            return;

        System.out.println(this + " reading a client request...");

        // clear buffers
        bb.clear();
        sb.setLength(0);

        // read request

        boolean reading = true;
        try {
            while (reading) {
                int b = socketChannel.read(bb);

                // read until there's available data
                if (b <= 0)
                    continue;

                bb.flip();
                var charBuffer = CHARSET.decode(bb);

                while (charBuffer.hasRemaining()) {
                    char c = charBuffer.get();
                    System.out.println(c);
                    if (c == '\r' || c == '\n') {
                        reading = false;
                        break;
                    }
                    System.out.println(c);
                    sb.append(c);
                }
            }

            var requestArray = sb.toString().split(";");
            String command = requestArray[0];
            String arguments = requestArray[1];

            System.out.println(this + " got request: [" + command + "] " + arguments);

            switch (command) {
                case "Hi" -> {
                    writeToChannel(socketChannel, "Hi");
                }
                case "Bye" -> {
                    writeToChannel(socketChannel, "Bye");
                    System.out.println(this + " saying \"Bye\" to client...");

                    // close the channel and its socket
                    socketChannel.close();
                    socketChannel.socket().close();
                }
                case "SUBSCRIBE" -> {
                    //
                }
                case "UNSUBSRIBE" -> {
                    //

                }
                default -> {
                    // default
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private int writeToChannel(SocketChannel socketChannel, String message) throws IOException {
        return socketChannel.write(CHARSET.encode(CharBuffer.wrap(message)));
    }

    @Override
    public String toString() {
        return "Server";
    }
}
