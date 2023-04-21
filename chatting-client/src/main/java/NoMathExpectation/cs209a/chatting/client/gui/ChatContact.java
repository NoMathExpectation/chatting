package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j(topic = "ChatContact")
public class ChatContact implements Initializable {
    static Map<UUID, ChatContact> chatContacts = new ConcurrentHashMap<>();

    @FXML
    Button sendButton;
    @Getter
    Contact contact;
    @Getter
    Stage stage;

    @FXML
    Menu manageMenu;
    @FXML
    Menu emojiMenu;
    @FXML
    ListView<User> members;
    @FXML
    ListView<MessageEvent> messages;
    @FXML
    SplitPane sidebarSplit;
    @FXML
    TextArea input;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messages.setCellFactory(param -> new MessageCell());
    }

    void init(@NonNull Contact contact, @NonNull Stage stage) {
        this.contact = contact;
        this.stage = stage;

        if (contact instanceof User) {
            manageMenu.setDisable(true);
        }
    }

    public static ChatContact of(@NonNull UUID id) {
        return of(Connector.getInstance().getContacts().get(id));
    }

    public static ChatContact of(@NonNull Contact contact) {
        return chatContacts.computeIfAbsent(contact.getId(), id -> {
            val loader = new FXMLLoader(ChatContact.class.getResource("contact.fxml"));
            val stage = new Stage();
            stage.setTitle("Chatting with " + contact.getName());
            stage.initOwner(Chat.stage);
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                return null;
            }
            ChatContact chatContact = loader.getController();
            chatContact.init(contact, stage);
            return chatContact;
        });
    }

    public void addMessage(@NonNull MessageEvent messageEvent) {
        messages.getItems().add(messageEvent);
    }

    public void setOffline() {
        sendButton.setDisable(true);
        manageMenu.setDisable(true);
        emojiMenu.setDisable(true);
        input.setEditable(false);
        input.setText("Contact is offline.");
    }

    public static void setAllOffline() {
        chatContacts.values().forEach(ChatContact::setOffline);
    }

    @FXML
    void sendMessage(ActionEvent actionEvent) {
        if (input.getText().isBlank() || Connector.getInstance().getContacts().get(contact.getId()) == null) {
            return;
        }

        try {
            addMessage(contact.sendMessage(input.getText()));
            input.clear();
        } catch (Exception e) {
            log.error("Message send failed to contact " + contact, e);
        }
    }

    @FXML
    void addSmileEmoji() {
        input.appendText("\uD83D\uDE42");
    }

    @FXML
    void addLaughEmoji() {
        input.appendText("\uD83D\uDE01");
    }

    @FXML
    void addUnhappyEmoji() {
        input.appendText("\uD83D\uDE41");
    }

    @FXML
    void addSleepingEmoji() {
        input.appendText("\uD83D\uDE34");
    }

    @FXML
    void addSadEmoji() {
        input.appendText("\uD83D\uDE2D");
    }

    @FXML
    void addWinkEmoji() {
        input.appendText("\uD83D\uDE09");
    }

    @FXML
    void addCoolEmoji() {
        input.appendText("\uD83D\uDE0E");
    }

    @FXML
    void addCryEmoji() {
        input.appendText("\uD83D\uDE2D");
    }

    @FXML
    void addAngryEmoji() {
        input.appendText("\uD83D\uDE20");
    }

    @FXML
    void addScaryEmoji() {
        input.appendText("\uD83D\uDE28");
    }

    @FXML
    void addUnbengableEmoji() {
        input.appendText("\uD83D\uDE05");
    }
}

class MessageCell extends ListCell<MessageEvent> {
    public MessageCell() {
        super();
        setEditable(false);
    }

    @Override
    public void updateItem(MessageEvent msg, boolean empty) {
        super.updateItem(msg, empty);
        if (empty || Objects.isNull(msg)) {
            return;
        }

        HBox wrapper = new HBox();
        Label nameLabel = new Label(msg.getSentBy().getName() + " " + LocalDateTime.ofInstant(msg.getTime(), ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        Label msgLabel = new Label(msg.getMessage());

        nameLabel.setPrefHeight(20);
        nameLabel.setMinWidth(200);
        nameLabel.setMaxWidth(400);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        if (ConnectorImpl.getClientInstance().getId().equals(msg.getSentBy().getId())) {
            wrapper.setAlignment(Pos.TOP_RIGHT);
            wrapper.getChildren().addAll(msgLabel, nameLabel);
            msgLabel.setPadding(new Insets(0, 20, 0, 0));
        } else {
            wrapper.setAlignment(Pos.TOP_LEFT);
            wrapper.getChildren().addAll(nameLabel, msgLabel);
            msgLabel.setPadding(new Insets(0, 0, 0, 20));
        }

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(wrapper);
    }
}
