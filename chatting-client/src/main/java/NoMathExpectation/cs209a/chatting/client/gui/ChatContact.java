package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j(topic = "ChatContact")
public class ChatContact implements Initializable {
    private static Map<UUID, ChatContact> chatContacts = new ConcurrentHashMap<>();

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
    ListView<Contact> members;
    @FXML
    ListView<MessageEvent> messages;
    @FXML
    SplitPane sidebarSplit;
    @FXML
    TextArea input;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messages.setCellFactory(param -> new MessageCell());
        members.setCellFactory(param -> new ContactListCell());
    }

    private void init(@NonNull Contact contact, @NonNull Stage stage) {
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
            if (contact instanceof Group) {
                stage.setTitle("Chatting in " + contact.getName());
            } else {
                stage.setTitle("Chatting with " + contact.getName());
            }
            stage.initOwner(Chat.getStage());
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

    public void addMembers(@NonNull List<? extends Contact> members) {
        this.members.getItems().addAll(members);
    }

    public void removeMember(@NonNull UUID id) {
        members.getItems().removeIf(contact -> contact.getId().equals(id));
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

        chatContacts.forEach((id, chatContact) -> {
            if (chatContact.getContact() instanceof Group) {
                chatContact.removeMember(contact.getId());
            }
        });
    }

    public static void setAllOffline() {
        chatContacts.values().forEach(ChatContact::setOffline);
    }

    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        if (input.getText().isBlank() || Connector.getInstance().getContacts().get(contact.getId()) == null) {
            return;
        }

        try {
            val messageEvent = contact.sendMessage(input.getText());
            if (contact instanceof User) {
                addMessage(messageEvent);
            }
            input.clear();
        } catch (Exception e) {
            log.error("Message send failed to contact " + contact, e);
        }
    }

    @FXML
    private void callInvite(ActionEvent actionEvent) {
        if (!(contact instanceof Group)) {
            return;
        }

        GroupInvite.of((Group) contact).show();
    }

    @FXML
    private void addSmileEmoji() {
        input.appendText("\uD83D\uDE42");
    }

    @FXML
    private void addLaughEmoji() {
        input.appendText("\uD83D\uDE01");
    }

    @FXML
    private void addUnhappyEmoji() {
        input.appendText("\uD83D\uDE41");
    }

    @FXML
    private void addSleepingEmoji() {
        input.appendText("\uD83D\uDE34");
    }

    @FXML
    private void addSadEmoji() {
        input.appendText("\uD83D\uDE2D");
    }

    @FXML
    private void addWinkEmoji() {
        input.appendText("\uD83D\uDE09");
    }

    @FXML
    private void addCoolEmoji() {
        input.appendText("\uD83D\uDE0E");
    }

    @FXML
    private void addCryEmoji() {
        input.appendText("\uD83D\uDE2D");
    }

    @FXML
    private void addAngryEmoji() {
        input.appendText("\uD83D\uDE20");
    }

    @FXML
    private void addScaryEmoji() {
        input.appendText("\uD83D\uDE28");
    }

    @FXML
    private void addUnbengableEmoji() {
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
