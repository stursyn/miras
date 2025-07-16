package kz.satpaev.sunkar.callbacks;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import kz.satpaev.sunkar.model.dto.ItemDto;

import java.util.function.Supplier;

public class ItemRemoveButtonCallback implements Callback<TableColumn<ItemDto, String>, TableCell<ItemDto, String>> {
  public Supplier<?> supplier;
  public ItemRemoveButtonCallback(Supplier<?> supplier) {
    super();
    this.supplier = supplier;
  }
  @Override
  public TableCell call(final TableColumn<ItemDto, String> param) {
    return new TableCell<ItemDto, String>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
          setText(null);
        } else {

          final Image delete = new Image("icons/delete.png");
          final ImageView deleteView = new ImageView(delete);
          final Button deleteBtn = new Button();

          final Image increase = new Image("icons/minus.png");
          final ImageView increaseView = new ImageView(increase);
          final Button increaseBtn = new Button();

          final HBox pane = new HBox();

          increaseView.setFitHeight(20);
          increaseView.setPreserveRatio(true);
          increaseBtn.setOnAction(event -> {
            var object = getTableView().getItems().get(getIndex());
            object.setCount((object.getCount() - 1));
            if (object.getCount() == 0) {
              getTableView().getItems().remove(getIndex());
            }
            object.setTotalPrice(object.getCount() * object.getPrice());

            getTableView().refresh();

            supplier.get();
          });

          increaseBtn.setPrefSize(20,20);
          increaseBtn.setGraphic(increaseView);

          deleteView.setFitHeight(20);
          deleteView.setPreserveRatio(true);
          deleteBtn.setOnAction(event -> {
                    getTableView().getItems().remove(getIndex());
                    supplier.get();
                  }
          );
          deleteBtn.setPrefSize(20,20);
          deleteBtn.setGraphic(deleteView);

          pane.getChildren().addAll(increaseBtn, deleteBtn);
          pane.setSpacing(5);

          setGraphic(pane);
          setText(null);
        }
      }
    };
  }
}