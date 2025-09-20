package kenma;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    public DialogBox(String text, Image img) {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxml.setController(this);
            fxml.setRoot(this);
            fxml.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        dialog.setWrapText(true);
        dialog.setMaxWidth(520); // let long messages wrap

        displayPicture.setImage(img);
        // circular avatar (radius matches typical 34px image)
        double r = 17.0;
        displayPicture.setClip(new Circle(r, r, r));

        setSpacing(10);
        setPadding(new Insets(8, 12, 8, 12));
        setMaxWidth(Double.MAX_VALUE); // let row stretch to cell width
    }

    /** Bot (left) bubble. */
    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.alignLeft();
        db.getStyleClass().add("row-left");
        db.dialog.getStyleClass().addAll("bubble", "duke-bubble");
        return db;
    }

    /** User (right) bubble. */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.alignRight();
        db.getStyleClass().add("row-right");
        db.dialog.getStyleClass().addAll("bubble", "user-bubble");
        return db;
    }

    /** Error (left, red) bubble. */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.alignLeft();
        db.getStyleClass().add("row-left");
        db.dialog.getStyleClass().addAll("bubble", "error-bubble");
        return db;
    }

    /* -------- layout helpers -------- */

    /** Left: [avatar][text] */
    private void alignLeft() {
        getChildren().setAll(displayPicture, dialog);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Right: [spacer][text][avatar] */
    private void alignRight() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().setAll(spacer, dialog, displayPicture);
        setAlignment(Pos.TOP_RIGHT);
    }
}
