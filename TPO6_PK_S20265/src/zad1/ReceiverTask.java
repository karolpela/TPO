package zad1;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ReceiverTask extends Task<Void> {

    private Client parent;

    public ReceiverTask(Client parent) {
        this.parent = parent;
    }

    @Override
    protected Void call() throws NamingException, JMSException {
        parent.connection.start();

        parent.subscriber.setMessageListener(message -> {
            var textMessage = (TextMessage) message;
            Platform.runLater(() -> {
                if (message instanceof TextMessage) {
                    try {
                        parent.messageView.getItems().add(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else {
                    parent.messageView.getItems().add(message.toString());
                }
                parent.messageView.scrollTo(parent.messageView.getItems().size() - 1);
            });
        });

        return null;
    }
}
