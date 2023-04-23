package NoMathExpectation.cs209a.chatting.client.gui;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Login implements Initializable {
    @Getter
    private static Login instance;
    private static final @NonNull Stage stage = new Stage();

    static {
        stage.setTitle("Login");
        stage.setResizable(false);
        try {
            stage.setScene(new Scene(new FXMLLoader(Login.class.getResource("login.fxml")).load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    TextField host;
    @FXML
    TextField port;
    @FXML
    TextField name;
    @FXML
    Label status;
    @FXML
    Button login;

    public Login() {
        if (instance != null) {
            throw new IllegalStateException("Login already initialized.");
        }

        instance = this;
    }

    public static void show() {
        stage.show();
    }

    public static void hide() {
        stage.hide();
    }

    public static void setStatusText(String s) {
        instance.status.setText(s);
    }

    public static void loginSuccessCallback() {
        hide();
    }

    public static void loginFailedCallback(String reason) {
        setStatusText(reason);
        instance.login.setDisable(false);
    }

    public @NonNull String getName() {
        return name.getText();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void login(ActionEvent actionEvent) {
        login.setDisable(true);

        int port;
        try {
            port = Integer.parseInt(this.port.getText());
        } catch (NumberFormatException e) {
            loginFailedCallback("Invalid port.");
            return;
        }

        val connector = new ConnectorImpl(host.getText(), port, name.getText());
        val thread = new Thread(connector);
        thread.setDaemon(true);
        thread.start();
    }

    public static boolean isLoggingIn() {
        return stage.isShowing();
    }
}
