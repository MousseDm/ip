package kenma;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, String rowClass, String bubbleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        dialog.getStyleClass().addAll("bubble", bubbleClass);
        this.getStyleClass().addAll("dialog-row", rowClass);

        displayPicture.setImage(img);
        displayPicture.getStyleClass().add("avatar");

        // 圆形裁剪头像
        double r = 17.0;
        displayPicture.setClip(new Circle(r, r, r));

        dialog.setWrapText(true);
        this.setFillHeight(true);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img, "row-right", "user-bubble");
        db.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        return db;
    }

    public static DialogBox getDukeDialog(String text, Image img) {
        return new DialogBox(text, img, "row-left", "duke-bubble");
    }

    public static DialogBox getErrorDialog(String text, Image img) {
        return new DialogBox(text, img, "row-left", "error-bubble");
    }
}
