package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Sample Skeleton for 'client.fxml' Controller Class
 */

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    private static final String PROXY_ADDRESS = "127.0.0.1";
    private static final int PROXY_PORT = 9000;
    private static final int MAX_PORT = (int) (Math.pow(2, 16) - 1);

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private TextField wordField;

    @FXML
    private TextField langField;

    @FXML
    private TextField portField;

    @FXML
    private TextArea responseArea;

    @FXML
    public void sendRequest() {
        String request =
                wordField.getText() + "," + langField.getText() + "," + portField.getText();
        try {
            int requestedPort = Integer.parseInt(portField.getText());
            if (requestedPort < 0 || requestedPort > MAX_PORT) {
                responseArea.setText("<Port out of range>");
                return;
            }
        } catch (NumberFormatException e) {
            responseArea.setText("<Inproper port>");
            return;
        }

        try (Socket proxySocket = new Socket(InetAddress.getByName(PROXY_ADDRESS), PROXY_PORT);
                PrintWriter outToProxy = new PrintWriter(proxySocket.getOutputStream(), true,
                        StandardCharsets.UTF_8);
                BufferedReader inFromProxy =
                        new BufferedReader(new InputStreamReader(proxySocket.getInputStream(),
                                StandardCharsets.UTF_8));
                ServerSocket langServerListener =
                        new ServerSocket(Integer.parseInt(portField.getText()));) {
            proxySocket.setSoTimeout(3000);
            langServerListener.setSoTimeout(6000);
            outToProxy.println(request);

            String proxyResponse = inFromProxy.readLine();

            if (proxyResponse.startsWith("LANGERR")) {
                throw new NoSuchLanguageException();
            }
            if (proxyResponse.startsWith("ARGERR")) {
                throw new IllegalArgumentException();
            }

            try (Socket langServerSocket = langServerListener.accept()) {
                BufferedReader inFromLangServer =
                        new BufferedReader(new InputStreamReader(langServerSocket.getInputStream(),
                                StandardCharsets.UTF_8));
                String langResponse = inFromLangServer.readLine();
                responseArea.setText(langResponse);
            }

        } catch (NoSuchLanguageException ce) {
            responseArea.setText("<No server for requested language>");
        } catch (ConnectException ce) {
            responseArea.setText("<Unable to connect to proxy>");
        } catch (IllegalArgumentException iae) {
            responseArea.setText("<Bad request>");
        } catch (SocketTimeoutException ste) {
            responseArea.setText("<Translation request timed out>");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        // no initialization needed as above method handles all actions
    }

}
