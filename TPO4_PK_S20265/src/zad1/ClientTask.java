package zad1;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ClientTask extends Task<Void> {

    private Client parent;

    public ClientTask(Client parent) {
        this.parent = parent;
    }

    @Override
    protected Void call() throws Exception {
        try {
            System.out.println(this + " Connected to server");

            var charset = StandardCharsets.UTF_8;

            // ==== allocate the buffer ==== //
            // allocateDirect allows for use of hardware mechanisms
            // to speed up I/O operations
            // the buffer should only be allocated *once* and reused

            ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);
            CharBuffer charBuffer;

            System.out.println(this + " Saying \"Hi\" to server");
            parent.toServer.write(charset.encode("Hi"));

            while (true) {
                // clear the buffer
                inBuffer.clear();

                // read new data
                int readBytes = parent.toServer.read(inBuffer);

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
                        System.out.println(this + " Disconnecting...");
                        break;
                    }
                    case "MESSAGE" -> {
                        handleMessage(arguments);
                        continue;
                    }
                    case "ADD_TOPIC" -> {
                        addTopic(arguments);
                        continue;
                    }
                    case "ADD_TOPICS" -> {
                        addTopics(arguments);
                        continue;
                    }
                    case "REMOVE_TOPIC" -> {
                        removeTopic(arguments);
                        continue;
                    }
                    case "INFO" -> {
                        System.out.println(this + " Info from server: \"" + arguments + "\"");
                    }
                    default -> {
                        System.out.println(
                                this + " No action specified for command \"" + command + "\"");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addTopic(String topic) {
        Platform.runLater(() -> parent.availableView.getItems().add(topic));
        System.out.println(this + " Added new topic \"" + topic + "\"");
    }

    private void addTopics(String topics) {
        var topicArray = topics.split("`");
        Platform.runLater(() -> {
            for (String topic : topicArray) {
                parent.availableView.getItems().add(topic);
                System.out.println(this + " Added new topic \"" + topic + "\"");
            }
        });
    }

    private void removeTopic(String topic) {
        Platform.runLater(() -> {
            // remove from available
            parent.availableView.getItems().remove(topic);
            System.out.println(this + " Removed topic \"" + topic + "\"");

            // unsubscribe
            parent.subscribedView.getItems().remove(topic);
            System.out.println(this + " Unsubscribed from topic \""
                    + topic + "\"");
        });
    }

    private void handleMessage(String message) {
        Platform.runLater(() -> {
            if (!parent.messageField.getText().equals("")) {
                if (parent.messageArea.getText().equals("")) {
                    parent.messageArea.setText(
                            parent.messageField.getText());
                } else {
                    parent.messageArea.setText(
                            parent.messageArea.getText() + '\n' + parent.messageField.getText());
                }
            }
            parent.messageField.setText(message);
        });
        System.out.println(this + " Got new message \"" + message + "\"");
    }

    @Override
    public String toString() {
        return "(Client Task)";
    }
}
