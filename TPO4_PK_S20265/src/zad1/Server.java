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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    public static final String HOST = "localhost";
    public static final int PORT = 9000;

    private static HashMap<String, List<SocketChannel>> channelsByTopic = new HashMap<>();
    private static HashMap<SocketChannel, Queue<String>> messagesByChannel = new HashMap<>();

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

            // *** main server loop *** //
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
                    // if there's a new client connection
                    if (key.isAcceptable()) {
                        System.out.println(this + " got a new connection");
                        System.out.println(key.channel());
                        // create a channel to communicate with client
                        // "accept() is non-blocking since client's already waiting"
                        var socketChannel = serverSocketChannel.accept();

                        // configure and register the channel
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, OP_READ | OP_WRITE);

                        // create a queue for this channel
                        messagesByChannel.put((SocketChannel) key.channel(),
                                new LinkedBlockingQueue<>());

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
                        var socketChannel = (SocketChannel) key.channel();
                        var queue = messagesByChannel.get(socketChannel);
                        String message = queue.poll();
                        if (message != null) {
                            writeToChannel(socketChannel, message);
                        }
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
                    if (c == '\r' || c == '\n') {
                        reading = false;
                        break;
                    }
                    sb.append(c);
                }
            }

            var requestArray = sb.toString().split(";");
            String command = requestArray[0];
            String arguments = "";
            if (requestArray.length > 1) {
                arguments = requestArray[1];
            }

            System.out.println(this + " got request: \"" + command + "\" " + arguments);

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
                case "ADD_TOPIC" -> {
                    String topic = arguments;
                    channelsByTopic.put(topic, new ArrayList<>());
                    System.out.println(this + "Added new topic \"" + topic + "\"");

                    var allChannels = messagesByChannel.keySet();
                    for (SocketChannel sc : allChannels) {
                        messagesByChannel.get(sc).add("ADD_TOPIC;" + topic);
                    }
                }
                case "REMOVE_TOPIC" -> {
                    String topic = arguments;
                    channelsByTopic.remove("key");
                    System.out.println(this + "Removed  topic \"" + topic + "\"");

                    var allChannels = messagesByChannel.keySet();
                    for (SocketChannel sc : allChannels) {
                        messagesByChannel.get(sc).add("REMOVE_TOPIC;" + topic);
                    }
                }
                case "MESSAGE" -> {
                    var argArray = arguments.split("`");
                    String topic = argArray[0];
                    String message = argArray[1];
                    var subscribers = channelsByTopic.get(topic);
                    for (SocketChannel sc : subscribers) {
                        messagesByChannel.get(sc).add(message);
                    }
                    System.out.println(this + "Added message \"" + message + "\"" +
                            "to topic \"" + topic + "\"");
                }
                case "SUBSCRIBE" -> {
                    String topic = arguments;
                    var channels = channelsByTopic.get("topic");
                    channels.add(socketChannel);
                    writeToChannel(socketChannel,
                            "Successfully subscribed to \"" + topic + "\"");
                }
                case "UNSUBSRIBE" -> {
                    String topic = arguments;
                    var channels = channelsByTopic.get("topic");
                    channels.remove(socketChannel);
                    writeToChannel(socketChannel,
                            "Successfully unsubscribed from \"" + topic + "\"");
                }
                default -> {
                    writeToChannel(socketChannel, command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int writeToChannel(SocketChannel socketChannel, String message) throws IOException {
        return socketChannel.write(CHARSET.encode(CharBuffer.wrap(message)));
    }

    @Override
    public String toString() {
        return "(Server)";
    }
}
