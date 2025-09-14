package kenma;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point. Loads the UI and wires a single Kenma engine instance.
 */
public class Main extends Application {

    /** Single engine instance reused for all user inputs (efficient). */
    private Kenma engine;

    @Override
    public void start(Stage stage) throws Exception {
        // Init core
        engine = new Kenma("data/kenma.txt");

        // Load UI
        FXMLLoader fxml = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxml.load();
        MainWindow controller = fxml.getController();

        // Wire controller
        controller.setResponder(engine::getResponse);
        controller.setTitle("Kenma"); // header label text
        controller.showGreeting(engine.getGreeting()); // initial bot message

        // Stage/scene
        Scene scene = new Scene(root);
        stage.setTitle("Kenma");
        stage.setMinWidth(360);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
