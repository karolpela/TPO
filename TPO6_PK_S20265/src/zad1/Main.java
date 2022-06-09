package zad1;

import javax.jms.*;
import javax.naming.*;

public class Main {
    public static void main(String[] args) throws NamingException, JMSException {
        var context = new InitialContext();
        var factory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
        var connection = factory.createTopicConnection();
        var topic = (Topic) context.lookup("MyTopic");
        var session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        var subscriber = session.createSubscriber(topic);

        connection.start();
        var message = (TextMessage) subscriber.receive();
        System.out.println(message.getText());
    }
}
