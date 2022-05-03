package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static zad1.Server.HOST;
import static zad1.Server.PORT;


public class Publisher extends Application {
    @FXML
    private Button publishButton;

    @FXML
    private Button unpublishButton;

    @FXML
    private TextField topicField;

    @FXML
    private ListView<String> topicView;

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

    public void initialize() throws IOException, InterruptedException {
        topicView.setItems(FXCollections.observableArrayList());

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
