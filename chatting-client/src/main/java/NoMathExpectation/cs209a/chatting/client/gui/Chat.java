package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.GroupCreateEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

@Slf4j(topic = "Chat")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Initializable {
    private static Chat instance;
    @Getter
    private static final @NonNull Stage stage = new Stage();

    static {
        stage.setTitle("Contacts");
        stage.setResizable(false);
        try {
            stage.setScene(new Scene(new FXMLLoader(Chat.class.getResource("chat.fxml")).load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    MenuItem newGroupButton;
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
        if (instance != null) {
            throw new IllegalStateException("Chat already initialized.");
        }

        instance = this;
    }

    public static void connectedCallback(@NonNull String host, int port, @NonNull String name, @NonNull String id, ObservableList<Contact> contacts) {
        instance.host = host;
        instance.port = port;

        instance.name.setText(name);
        instance.id.setText(id);
        instance.connectButton.setText("Disconnect");

        instance.contacts.setItems(contacts);

        instance.newGroupButton.setDisable(false);

        stage.show();

        instance.connected = true;
    }

    public static void disconnectedCallback(String reason) {
        instance.contacts.getItems().clear();

        instance.newGroupButton.setDisable(true);

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

    public static void addContact(@NonNull Contact contact) {
        instance.contacts.getItems().add(contact);
    }

    public static void removeContact(@NonNull UUID id) {
        instance.contacts.getItems().removeIf(contact -> Objects.equals(contact.getId(), id));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contacts.setCellFactory(param -> new ContactListCell());
    }

    @FXML
    private void callCreateNewGroup(ActionEvent actionEvent) {
        if (!instance.connected) {
            return;
        }

        val inputDialog = new TextInputDialog();
        inputDialog.initOwner(stage);
        inputDialog.initModality(Modality.WINDOW_MODAL);
        inputDialog.setTitle("Create new group");
        inputDialog.setHeaderText("Create new group");
        inputDialog.setContentText("Please enter group name:");

        String result;
        do {
            inputDialog.showAndWait();

            result = inputDialog.getResult();
            if (result == null) {
                return;
            }
            if (result.isBlank()) {
                inputDialog.setContentText("Group name cannot be blank.");
            }
        } while (result.isBlank());

        val connector = ConnectorImpl.getClientInstance();
        try {
            connector.sendEvent(new GroupCreateEvent(result, connector.getUser()));
        } catch (IOException e) {
            log.error("Failed to create group.", e);
            val alert = new Alert(Alert.AlertType.ERROR, "Failed to create group.");
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.show();
        }
    }

    @FXML
    private void callConnectButton(ActionEvent actionEvent) {
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
