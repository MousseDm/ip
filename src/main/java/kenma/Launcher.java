package kenma;

import javafx.application.Application;

/**
 * Standalone launcher to avoid JavaFX classpath issues when packaging.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
