package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import lombok.val;

import java.util.Objects;

public class ContactListCell extends ListCell<Contact> {
    public ContactListCell() {
        super();
        setEditable(false);
    }

    @Override
    protected void updateItem(Contact contact, boolean empty) {
        super.updateItem(contact, empty);
        if (empty || Objects.isNull(contact)) {
            getChildren().clear();
            return;
        }

        val hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        val name = new Label(contact.getName());
        name.setPrefSize(200, 20);
        name.setPadding(new Insets(0, 20, 0, 0));

        hBox.getChildren().add(name);

        hBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ChatContact.of(contact).getStage().show();
            }
        });

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(hBox);
    }
}
