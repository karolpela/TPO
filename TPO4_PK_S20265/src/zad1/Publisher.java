package zad1;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import javafx.application.Application;
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
    private ListView<String> topicView;

    @FXML
    private TextField topicField;

    @FXML
    private Button unpublishButton;

    public static void main(String[] args) {
        launch();
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
        ChannelHelper.writeToChannel(PublisherTask.getSocketChannel(), message);
        addTopic(topic);
        System.out.println("published");
    }

    @FXML
    public void unpublish(ActionEvent e) throws IOException {
        String topic = topicView.getSelectionModel().getSelectedItem();
        String message = "UNPUBLISH;" + topic;
        ChannelHelper.writeToChannel(PublisherTask.getSocketChannel(), message);
        removeTopic(topic);
        System.out.println("unpublished");
    }

    @Override
    public void init() throws Exception {
        topicView = new ListView<>();
        topicView.setItems(FXCollections.observableArrayList());
        Thread.sleep(1000);
        PublisherTask.setSocketChannel(SocketChannel.open());
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        Parent root = FXMLLoader.load(getClass().getResource("Publisher.fxml"));
        stage.setScene(new Scene(root));
        stage.show();

        new Thread(new PublisherTask()).start();
    }
}
