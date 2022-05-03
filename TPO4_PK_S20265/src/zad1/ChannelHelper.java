package zad1;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChannelHelper {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private ChannelHelper() {}

    public static int writeToChannel(SocketChannel socketChannel, String message)
            throws IOException {
        return socketChannel.write(CHARSET.encode(CharBuffer.wrap(message) + "\n"));
    }
}
