package kz.satpaev.sunkar.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import static kz.satpaev.sunkar.util.Constants.OPACITY_RECTANGLE_ID;

public class UiControllerUtil {
  public static void addOpacityRectangle(StackPane root) {
    Rectangle dim = new Rectangle();
    dim.setId(OPACITY_RECTANGLE_ID);
    dim.widthProperty().bind(root.widthProperty());
    dim.heightProperty().bind(root.heightProperty());
    dim.setStyle("-fx-fill: rgba(0, 0, 0, 0.45);");
    root.getChildren().add(dim);
  }

  public static void removeOpacityRectangle(StackPane root) {
    Node lookup = null;
    for (Node node : root.getChildren()) {
      if (OPACITY_RECTANGLE_ID.equals(node.getId())) {
        lookup = node;
      }
    }

    if (lookup != null) {
      root.getChildren().remove(lookup);
    }
  }

  public static boolean hasOpacityRectangle(StackPane root) {
    Node lookup = null;
    for (Node node : root.getChildren()) {
      if (OPACITY_RECTANGLE_ID.equals(node.getId())) {
        lookup = node;
      }
    }

    return lookup != null;
  }

  public static void show(Stage ownerStage, String message, int durationMs) {
    Label label = new Label(message);
    label.setStyle(
        "-fx-background-radius: 10;" +
            "-fx-background-color: rgba(0,0,0,0.85);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-font-size: 14px;"
    );

    Popup popup = new Popup();
    popup.getContent().add(label);
    popup.setAutoFix(true);
    popup.setAutoHide(true);

    // Показ под главным окном
    popup.show(ownerStage);

    // Позиция внизу по центру
    Scene scene = ownerStage.getScene();
    double x = ownerStage.getX() + (scene.getWidth() - label.getWidth()) / 2;
    double y = ownerStage.getY() + scene.getHeight() - 70;
    popup.setX(x);
    popup.setY(y);

    // Fade In
    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), label);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    // Hold
    PauseTransition stay = new PauseTransition(Duration.millis(durationMs));

    // Fade Out
    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), label);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    fadeOut.setOnFinished(e -> popup.hide());

    fadeIn.play();
    fadeIn.setOnFinished(e -> stay.play());
    stay.setOnFinished(e -> fadeOut.play());
  }
}
