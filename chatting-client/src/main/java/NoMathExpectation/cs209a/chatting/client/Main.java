package NoMathExpectation.cs209a.chatting.client;

import NoMathExpectation.cs209a.chatting.client.gui.Login;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        Login.show();
    }
}
