package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Initializable {
    static Chat instance;
    static @NonNull Stage stage = new Stage();

    static {
        stage.setTitle("Contacts");
        try {
            stage.setScene(new Scene(new FXMLLoader(Chat.class.getResource("chat.fxml")).load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    MenuItem connectButton;
    @FXML
    Label name;
    @FXML
    Label id;
    @FXML
    ListView<Contact> contacts;

    String host;
    int port;

    boolean connected = true;

    public Chat() {
        instance = this;
    }

    public static void show() {
        stage.show();
    }

    public static void connectedCallback(@NonNull String host, int port, @NonNull String name, @NonNull String id) {
        instance.host = host;
        instance.port = port;

        instance.name.setText(name);
        instance.id.setText(id);
        instance.connectButton.setText("Disconnect");

        stage.show();

        instance.connected = true;
    }

    public static void disconnectedCallback(String reason) {
        instance.id.setText("Disconnected");
        instance.connectButton.setText("Reconnect");

        instance.connected = false;

        if (reason != null) {
            val alert = new Alert(Alert.AlertType.ERROR, reason);
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void callCreateNewGroup(ActionEvent actionEvent) {
        // TODO: 2023/4/17 create a new group
    }

    @FXML
    void callConnectButton(ActionEvent actionEvent) {
        if (connected) {
            try {
                Connector.getInstance().close();
            } catch (IOException ignored) {
            }
            contacts.getItems().clear();
        } else {
            val connector = new ConnectorImpl(host, port, name.getText());
            val thread = new Thread(connector);
            thread.setDaemon(true);
            thread.start();
        }
    }
}
