package kz.satpaev.sunkar.callbacks;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import kz.satpaev.sunkar.model.dto.ItemDto;

public class ItemRemoveButtonCallback implements Callback<TableColumn<ItemDto, String>, TableCell<ItemDto, String>> {
  @Override
  public TableCell call(final TableColumn<ItemDto, String> param) {
    return new TableCell<ItemDto, String>() {

      final Image delete = new Image("icons/delete.png");
      final ImageView deleteView = new ImageView(delete);
      final Button deleteBtn = new Button();

      final Image increase = new Image("icons/minus.png");
      final ImageView increaseView = new ImageView(increase);
      final Button increaseBtn = new Button();

      final Pane pane = new Pane();

      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
          setText(null);
        } else {

          increaseView.setFitHeight(20);
          increaseView.setPreserveRatio(true);
          increaseBtn.setOnAction(event ->
                  getTableView().getItems().remove(getIndex())
          );
          increaseBtn.setPrefSize(20,20);
          increaseBtn.setGraphic(increaseView);

          deleteView.setFitHeight(20);
          deleteView.setPreserveRatio(true);
          deleteBtn.setOnAction(event ->
            getTableView().getItems().remove(getIndex())
          );
          deleteBtn.setPrefSize(20,20);
          deleteBtn.setGraphic(deleteView);

          pane.getChildren().add(increaseBtn);
          pane.getChildren().add(deleteBtn);

          setGraphic(pane);
          setText(null);
        }
      }
    };
  }
}