package ca.seneca.apd545.RXHgrandhotel.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        AppConfig.getInstance();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml")
        );

        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("RXH Grand Hotel - Self Check-In Kiosk");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        AppConfig.getInstance().shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
