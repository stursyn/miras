package kz.satpaev.sunkar.util;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

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
}
