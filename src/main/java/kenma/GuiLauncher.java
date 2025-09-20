package kenma;

/**
 * GUI launcher that avoids compile-time JavaFX imports.
 * It reflectively launches your JavaFX Application class if present.
 *
 * Expected GUI app class name (change if yours differs): kenma.MainApp
 * class kenma.MainApp extends javafx.application.Application { ... }
 */
public class GuiLauncher {
    public static void boot(String[] args) {
        try {
            // Look for your JavaFX Application subclass
            Class<?> appClass = Class.forName("kenma.MainApp");

            // Call: javafx.application.Application.launch(MainApp.class, args)
            Class<?> applicationClass = Class.forName("javafx.application.Application");
            applicationClass
                    .getMethod("launch", Class.class, String[].class)
                    .invoke(null, appClass, (Object) args);

        } catch (Throwable t) {
            // Propagate so Launcher can fall back to CLI
            throw new RuntimeException("GUI entrypoint not found or JavaFX unavailable", t);
        }
    }
}
