package zad1;

import static zad1.Server.HOST;
import static zad1.Server.PORT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class Publisher extends Application {
    @FXML
    private Button publishButton;

    @FXML
    private Button unpublishButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextField topicField;

    @FXML
    private ListView<String> topicView;

    @FXML
    private TextField messageField;

    // non-FXML fields
    private SocketChannel toServer;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void addTopic(String topic) {
        topicView.getItems().add(topic);
    }

    @FXML
    public void removeTopic(String topic) {
        topicView.getItems().remove(topic);
    }

    @FXML
    public void publish(ActionEvent e) throws IOException {
        String topic = topicField.getText();
        if (topicView.getItems().contains(topic))
            return;
        String message = "PUBLISH;" + topic;
        ChannelHelper.writeToChannel(toServer, message);
        addTopic(topic);
        System.out.println(this + " Published topic \"" + topic + "\"");
    }

    @FXML
    public void unpublish(ActionEvent e) throws IOException {
        String topic = topicView.getSelectionModel().getSelectedItem();
        String message = "UNPUBLISH;" + topic;
        ChannelHelper.writeToChannel(toServer, message);
        removeTopic(topic);
        System.out.println(this + " Unpublished topic \"" + topic + "\"");
    }

    @FXML
    public void send(ActionEvent e) throws IOException {
        String message = messageField.getText();
        String topic = topicView.getSelectionModel().getSelectedItem();
        if (topic == null) {
            System.out.println(this + " Select a topic before sending a message!");
            return;
        }
        message = "MESSAGE;" + topic + "`" + message;
        ChannelHelper.writeToChannel(toServer, message);
        System.out.println(this + " Sent message \"" + message + "\" on topic \"" + topic + "\"");
    }

    public void initialize() throws IOException, InterruptedException {
        // initialize topic view
        topicView.setItems(FXCollections.observableArrayList());

        // disable send button in case of no topic selection
        sendButton.disableProperty().bind(
                Bindings.isNull(topicView.getSelectionModel().selectedItemProperty()));

        // init socket channel and wait for connection
        Thread.sleep(1000);
        toServer = SocketChannel.open();
        toServer.configureBlocking(false);
        toServer.connect(new InetSocketAddress(HOST, PORT));
        System.out.println(this + " Connecting to server...");

        while (!toServer.finishConnect()) {
            // progress bar or other operations until connected
        }
        new Thread(new PublisherTask(toServer)).start();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        Parent root = FXMLLoader.load(getClass().getResource("Publisher.fxml"));
        stage.setTitle("Publisher");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public String toString() {
        return "(Publisher)";
    }
}
