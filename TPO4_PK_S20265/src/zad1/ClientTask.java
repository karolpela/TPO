package zad1;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientTask extends Task<Void> {

    private Client parent;

    public ClientTask(Client parent) {
        this.parent = parent;
    }

    @Override
    protected Void call() throws Exception {
        try {
            // initialize fields
            SocketChannel socketChannel = parent.toServer;
            List<String> availableList = parent.availableView.getItems();
            List<String> subscribedList = parent.subscribedView.getItems();
            TextArea messageArea = parent.messageArea;
            TextField messageField = parent.messageField;

            System.out.println(this + " Connected to server");

            var charset = StandardCharsets.UTF_8;

            // ==== allocate the buffer ==== //
            // allocateDirect allows for use of hardware mechanisms
            // to speed up I/O operations
            // the buffer should only be allocated *once* and reused

            ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);
            CharBuffer charBuffer;

            System.out.println(this + " Saying \"Hi\" to server");
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

                System.out.println(this + " Server: \"" + fromServer + "\"");
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
                    case "MESSAGE" -> {
                        String message = arguments;
                        Platform.runLater(() -> {
                            if (!messageField.getText().equals("")) {
                                if (messageArea.getText().equals("")) {
                                    messageArea.setText(
                                            messageField.getText());
                                } else {
                                    messageArea.setText(
                                            messageArea.getText() + '\n' + messageField.getText());
                                }
                            }
                            messageField.setText(message);
                        });
                        System.out.println("(Client) Got new message \"" + message + "\"");
                        continue;
                    }
                    case "ADD_TOPIC" -> {
                        String topic = arguments;
                        Platform.runLater(() -> {
                            availableList.add(topic);
                        });
                        System.out.println("(Client) Added new topic \"" + topic + "\"");
                        continue;
                    }
                    case "REMOVE_TOPIC" -> {
                        String topic = arguments;
                        Platform.runLater(() -> {
                            // remove from available
                            availableList.remove(topic);
                            System.out.println("(Client) Removed topic \"" + topic + "\"");

                            // unsubscribe
                            subscribedList.remove(topic);
                            System.out.println("(Client) Unsubscribed from topic \""
                                    + topic + "\"");
                        });
                        continue;
                    }
                    case "INFO" -> {
                        System.out.println("(Client) Info from server: \"" + arguments + "\"");
                    }
                    default -> {
                        System.out.println(
                                "(Client) No action specified for command \"" + command + "\"");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "(Client Task)";
    }
}
