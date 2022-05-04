package zad1;

import static zad1.Server.HOST;
import static zad1.Server.PORT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Client extends Application {

    @FXML
    protected ListView<String> availableView;

    @FXML
    protected ListView<String> subscribedView;

    @FXML
    private Button subscribeButton;

    @FXML
    private Button unsubscribeButton;

    @FXML
    protected TextArea messageArea;

    @FXML
    protected TextField messageField;

    // non-FXML fields

    protected SocketChannel toServer;

    @FXML
    public void subscribe(ActionEvent e) throws IOException {
        String topic = availableView.getSelectionModel().getSelectedItem();
        String message = "SUBSCRIBE;" + topic;
        ChannelHelper.writeToChannel(toServer, message);
        subscribedView.getItems().add(topic);
        availableView.getItems().remove(topic);
        System.out.println(this + " Subscribed to \"" + topic + "\"");
    }

    @FXML
    public void unsubscribe(ActionEvent e) throws IOException {
        String topic = subscribedView.getSelectionModel().getSelectedItem();
        String message = "UNSUBSCRIBE;" + topic;
        ChannelHelper.writeToChannel(toServer, message);
        subscribedView.getItems().remove(topic);
        availableView.getItems().add(topic);
        System.out.println(this + " Unsubscribed from \"" + topic + "\"");

    }

    public void initialize() throws IOException, InterruptedException {
        // initialize lists and views
        ObservableList<String> availableList = FXCollections.observableArrayList();
        ObservableList<String> subscribedList = FXCollections.observableArrayList();
        availableView.setItems(availableList);
        subscribedView.setItems(subscribedList);

        // disable buttons in case of no selection
        subscribeButton.disableProperty().bind(
                Bindings.isNull(availableView.getSelectionModel().selectedItemProperty()));
        unsubscribeButton.disableProperty().bind(
                Bindings.isNull(subscribedView.getSelectionModel().selectedItemProperty()));

        // initialize socket channel and wait for connection
        Thread.sleep(1000);
        toServer = SocketChannel.open();
        toServer.configureBlocking(false);
        toServer.connect(new InetSocketAddress(HOST, PORT));
        System.out.println(this + " Connecting to server...");

        while (!toServer.finishConnect()) {
            // progress bar or other operations until connected
        }
        new Thread(new ClientTask(this)).start();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        stage.setTitle("Client");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public String toString() {
        return "(Client)"; // maybe add singleton id
    }
}
