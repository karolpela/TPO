package zad1;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Client extends Application {

    @FXML
    protected ListView<String> messageView;

    @FXML
    private TextField nicknameField;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    // JMS fields
    InitialContext context;
    TopicConnectionFactory factory;
    TopicConnection connection;
    Topic topic;
    TopicSession session;
    TopicSubscriber subscriber;
    TopicPublisher publisher;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        stage.setTitle("Client");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void initialize() throws NamingException, JMSException {
        // initialize list and view
        ObservableList<String> messageList = FXCollections.observableArrayList();
        messageView.setItems(messageList);

        // disable button in case of no message
        sendButton.disableProperty().bind(
                Bindings.isEmpty(messageField.textProperty()));

        // create JMS objects
        context = new InitialContext();
        factory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
        connection = factory.createTopicConnection();
        topic = (Topic) context.lookup("Chat");
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        subscriber = session.createSubscriber(topic);
        publisher = session.createPublisher(topic);

        // start receiver task
        new Thread(new ReceiverTask(this)).start();
    }

    @FXML
    private void sendMessage() throws JMSException {
        String text = nicknameField.getText() + ": " + messageField.getText();
        var message = session.createTextMessage();
        message.setText(text);
        publisher.send(message);
    }
}
