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
        engine = new Kenma("data/kenma.txt");

        FXMLLoader fxml = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxml.load();
        assert root != null;

        MainWindow controller = fxml.getController();
        assert controller != null;

        controller.showGreeting(engine.getGreeting());
        controller.setResponder(engine::getResponse);

        stage.setScene(new Scene(root));
        stage.setTitle("Kenma");
        stage.show();
    }

}
