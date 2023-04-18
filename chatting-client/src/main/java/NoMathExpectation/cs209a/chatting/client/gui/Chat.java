package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Initializable {
    static Chat instance;
    static @NonNull Stage stage = new Stage();

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

    public static void connectedCallback(@NonNull String host, int port, @NonNull String name, @NonNull String id, ObservableList<Contact> contacts) {
        instance.host = host;
        instance.port = port;

        instance.name.setText(name);
        instance.id.setText(id);
        instance.connectButton.setText("Disconnect");

        instance.contacts.setItems(contacts);

        stage.show();

        instance.connected = true;
    }

    public static void disconnectedCallback(String reason) {
        instance.contacts.getItems().clear();

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

class ContactListCell extends ListCell<Contact> {
    public ContactListCell() {
        super();
        setEditable(false);
        setOnMouseClicked(this::onMouseClick);
    }

    public void onMouseClick(MouseEvent mouseEvent) {

    }

    @Override
    protected void updateItem(Contact contact, boolean empty) {
        super.updateItem(contact, empty);
        if (empty || Objects.isNull(contact)) {
            return;
        }

        val hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(contact.getName());
        name.setPrefSize(200, 20);
        name.setPadding(new Insets(0, 20, 0, 0));

        hBox.getChildren().add(name);

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(hBox);
    }
}
