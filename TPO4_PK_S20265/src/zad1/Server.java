package zad1;

import static zad1.ChannelHelper.writeToChannel;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        new Server();
    }

    Server() throws IOException, InterruptedException {
        try (var serverSocketChannel = ServerSocketChannel.open()) {
            // create server socket channel and bind to specified address
            serverSocketChannel.bind(new InetSocketAddress(HOST, PORT));

            // set non-blocking mode
            serverSocketChannel.configureBlocking(false);

            // create and register selector to accept connections
            var selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println(this + " Waiting for connections...");

            // ==== main server loop ==== //
            while (true) {
                // reduce resource usage
                Thread.sleep(500);

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
                        accept(serverSocketChannel, selector);
                        continue;
                    }

                    // if there's a ready-to-read channel
                    if (key.isReadable()) {
                        read(key);
                        continue;
                    }

                    // if there's a ready-to-write channel
                    if (key.isWritable()) {
                        write(key);
                        continue;
                    }
                }
            }
        }
    }

    private void write(SelectionKey key) throws IOException, InterruptedException {
        var socketChannel = (SocketChannel) key.channel();
        var queue = messagesByChannel.get(socketChannel);
        String message = queue.poll();
        if (message != null) {
            writeToChannel(socketChannel, message);
        }
    }

    private void read(SelectionKey key) {
        var socketChannel = (SocketChannel) key.channel();
        handleRequest(socketChannel);
    }

    private void accept(ServerSocketChannel serverSocketChannel, Selector selector)
            throws IOException, InterruptedException {
        System.out.println(this + " got a new connection");
        // create a channel to communicate with client
        // "accept() is non-blocking since client's already waiting"
        var socketChannel = serverSocketChannel.accept();

        // configure and register the channel
        socketChannel.configureBlocking(false);
        var clientKey = socketChannel.register(selector, OP_READ | OP_WRITE);

        // create a queue for this channel
        var clientQueue = new LinkedBlockingQueue<String>();
        messagesByChannel.put((SocketChannel) clientKey.channel(), clientQueue);

        // send a list of available topics
        var topics = channelsByTopic.keySet();
        if (!topics.isEmpty()) {
            String message = "ADD_TOPICS;";
            for (String topic : topics) {
                message += topic + '`';
            }
            clientQueue.put(message);
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

        // clear buffers
        bb.clear();
        sb.setLength(0);

        // read request
        try {
            int b = socketChannel.read(bb);

            // read until there's available data
            if (b <= 0)
                return;

            bb.flip();
            var charBuffer = CHARSET.decode(bb);

            while (charBuffer.hasRemaining()) {
                sb.append(charBuffer.get());
            }

            var requestArray = sb.toString().split(";");
            String command = requestArray[0];
            String arguments = "";
            if (requestArray.length > 1) {
                arguments = requestArray[1];
                arguments = arguments.replace("\n", "").replace("\r", "");
            }

            System.out.println(this + " Got request: " + command
                    + (arguments.equals("") ? "" : " \"" + arguments + "\""));

            switch (command) {
                case "Hi" -> {
                    writeToChannel(socketChannel, "Hi");
                }
                case "Bye" -> {
                    sayByeAndDisconnect(socketChannel);
                }
                case "PUBLISH" -> {
                    publishAndNotifySubs(socketChannel, arguments);
                }
                case "UNPUBLISH" -> {
                    unpublishAndNotifySubs(socketChannel, arguments);
                }
                case "MESSAGE" -> {
                    registerAndNotifySubs(socketChannel, arguments);
                }
                case "SUBSCRIBE" -> {
                    handleSubRequest(socketChannel, arguments);
                }
                case "UNSUBSCRIBE" -> {
                    handleUnsubRequest(socketChannel, arguments);
                }
                default -> {
                    System.out.println(command);
                    // echo
                    // writeToChannel(socketChannel, command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sayByeAndDisconnect(SocketChannel socketChannel) throws IOException {
        writeToChannel(socketChannel, "Bye");
        System.out.println(this + " Saying \"Bye\" to client...");

        // close the channel and its socket
        socketChannel.close();
        socketChannel.socket().close();
    }

    private void handleSubRequest(SocketChannel socketChannel, String arguments)
            throws IOException {
        String topic = arguments;
        var channels = channelsByTopic.get(topic);
        if (channels == null) {
            channelsByTopic.put(topic, Arrays.asList(socketChannel));
        } else {
            channels.add(socketChannel);
        }
        writeToChannel(socketChannel,
                "INFO;Successfully subscribed to \"" + topic + "\"");
    }

    private void handleUnsubRequest(SocketChannel socketChannel, String arguments)
            throws IOException {
        String topic = arguments;
        var channels = channelsByTopic.get(topic);
        if (channels == null) {
            writeToChannel(socketChannel, "No such topic \"" + topic + "\"");
        } else {
            channels.remove(socketChannel);

        }
        writeToChannel(socketChannel,
                "INFO;Successfully unsubscribed from \"" + topic + "\"");
    }

    private void publishAndNotifySubs(SocketChannel publisher, String arguments) {
        String topic = arguments;
        channelsByTopic.put(topic, new ArrayList<>());
        System.out.println(this + " Added new topic \"" + topic + "\"");

        var allChannels = messagesByChannel.keySet();
        for (SocketChannel sc : allChannels) {
            messagesByChannel.get(sc).add("ADD_TOPIC;" + topic);
        }
        messagesByChannel.get(publisher).add("OK");
    }

    private void unpublishAndNotifySubs(SocketChannel publisher, String arguments) {
        String topic = arguments;
        channelsByTopic.remove("key");
        // exclude handling of a nonexistent topic as requests
        // use a predefined list to choose from
        System.out.println(this + " Removed  topic \"" + topic + "\"");

        var allChannels = messagesByChannel.keySet();
        for (SocketChannel sc : allChannels) {
            messagesByChannel.get(sc).add("REMOVE_TOPIC;" + topic);
        }
        messagesByChannel.get(publisher).add("OK");
    }

    private void registerAndNotifySubs(SocketChannel publisher, String arguments) {
        var argArray = arguments.split("`");
        String topic = argArray[0];
        String message = "MESSAGE;" + argArray[1];
        var subscribers = channelsByTopic.get(topic);
        for (SocketChannel sc : subscribers) {
            messagesByChannel.get(sc).add(message);
        }
        messagesByChannel.get(publisher).add("OK");
        System.out.println(this + " Added message \"" + message + "\"" +
                "to topic \"" + topic + "\"");
    }


    @Override
    public String toString() {
        return "(Server)";
    }
}
