package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.GroupInviteEvent;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "GroupInvite")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupInvite implements Initializable {
    private static final @NonNull Map<UUID, GroupInvite> instances = new HashMap<>();

    Stage stage;

    Group group;

    @FXML
    ListView<Contact> users;

    @Getter
    final List<Contact> invitees = new ArrayList<>();

    private void init(@NonNull Stage stage, @NonNull Group group) {
        this.stage = stage;
        this.group = group;
        users.setCellFactory(params -> new ContactListChooseCell(this));
    }

    private void setUsers(List<Contact> users) {
        this.users.setItems(FXCollections.observableList(users));
        invitees.clear();
    }

    public static @NonNull GroupInvite of(@NonNull UUID id) {
        val group = Connector.getInstance().getContacts().get(id);
        if (!(group instanceof Group)) {
            throw new IllegalArgumentException("Contact is not a group.");
        }

        val instance = instances.computeIfAbsent(id, uuid -> {
            val stage = new Stage();
            stage.setTitle("Invite Users To " + group.getName());
            stage.setResizable(false);

            val loader = new FXMLLoader(Login.class.getResource("invite_users.fxml"));
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            GroupInvite controller = loader.getController();
            controller.init(stage, ((Group) group));

            return controller;
        });

        instance.setUsers(Connector.getInstance()
                .getContacts()
                .values()
                .parallelStream().filter(contact -> contact instanceof User && !((Group) group).getMembers().contains(contact))
                .map(contact -> (User) contact)
                .collect(Collectors.toList()));

        return instance;
    }

    public static @NonNull GroupInvite of(@NonNull Group group) {
        return of(group.getId());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void show() {
        stage.show();
    }

    @FXML
    private void callInvite(ActionEvent actionEvent) {
        val invitees = getInvitees()
                .parallelStream()
                .map(x -> (User) x)
                .collect(Collectors.toList());

        if (invitees.isEmpty()) {
            return;
        }

        stage.hide();

        try {
            Connector.getInstance().sendEvent(new GroupInviteEvent(group, invitees));
        } catch (IOException e) {
            log.error("Failed to send invite.", e);
        }
    }
}

class ContactListChooseCell extends ListCell<Contact> {
    private final @NonNull GroupInvite invite;

    private CheckBox check;
    @Getter
    private Contact contact;

    public ContactListChooseCell(@NonNull GroupInvite invite) {
        super();
        setEditable(false);
        this.invite = invite;
    }

    @Override
    protected void updateItem(Contact contact, boolean empty) {
        super.updateItem(contact, empty);
        if (empty || Objects.isNull(contact)) {
            contact = null;
            getChildren().clear();
            return;
        }

        this.contact = contact;

        val hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        check = new CheckBox(contact.getName());
        check.setPrefSize(200, 20);
        check.setPadding(new Insets(0, 20, 0, 0));

        val finalContact = contact;
        check.setOnAction(actionEvent -> {
            if (check.isSelected()) {
                invite.getInvitees().add(finalContact);
            } else {
                invite.getInvitees().remove(finalContact);
            }
        });

        hBox.getChildren().add(check);

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(hBox);
    }
}
