package kz.satpaev.sunkar.callbacks;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import kz.satpaev.sunkar.controllers.AbstractController;
import kz.satpaev.sunkar.model.dto.ItemDto;

import java.util.function.Supplier;

public class ItemRemoveButtonCallback implements Callback<TableColumn<ItemDto, String>, TableCell<ItemDto, String>> {
  private static final int[] DISCOUNT_OPTIONS = {3, 5, 10};

  public StackPane rootStackPane;
  public Supplier<?> supplier;
  public ItemRemoveButtonCallback(StackPane rootStackPane, Supplier<?> supplier) {
    super();
    this.rootStackPane = rootStackPane;
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
        } else {
          final HBox pane = new HBox();
          pane.getChildren().addAll(getDecreaseBtn(), getCountButton(), getDiscountMenu());
          pane.setSpacing(5);

          setGraphic(pane);
        }
          setText(null);
      }

      private Button getDecreaseBtn() {
        final Image decrease = new Image("icons/minus.png");
        final ImageView decreaseView = new ImageView(decrease);
        final Button decreaseBtn = new Button();
        decreaseBtn.getStyleClass().add("option-key");

        decreaseView.setFitHeight(40);
        decreaseView.setPreserveRatio(true);
        decreaseBtn.setOnAction(event -> {
          var object = getTableView().getItems().get(getIndex());
          object.setCount((object.getCount() - 1));
          if (object.getCount() == 0) {
            getTableView().getItems().remove(getIndex());
          }
          object.recomputeTotalPrice();

          getTableView().refresh();

          supplier.get();
        });

        decreaseBtn.setPrefSize(40,40);
        decreaseBtn.setGraphic(decreaseView);
        return decreaseBtn;
      }

      private Button getCountButton() {
        final Image count = new Image("icons/inventory.png");
        final ImageView countView = new ImageView(count);
        final Button countBtn = new Button();
        countBtn.getStyleClass().add("option-key");

        countView.setFitHeight(40);
        countView.setPreserveRatio(true);
        countBtn.setOnAction(event -> {
          ItemDto itemDto = getTableView().getItems().get(getIndex());
          new AbstractController().makeCountOfObject(rootStackPane, itemCount -> {
            if (itemCount == null) {
              supplier.get();
              return;
            }
            Platform.runLater(() -> {
              itemDto.setCount(itemCount);

              if (itemDto.getCount() == 0) {
                getTableView().getItems().remove(getIndex());
              }
              itemDto.recomputeTotalPrice();

              getTableView().refresh();

              supplier.get();
            });
          });
          supplier.get();
        }
        );
        countBtn.setPrefSize(40,40);
        countBtn.setGraphic(countView);
        return countBtn;
      }

      private MenuButton getDiscountMenu() {
        final Image discountIcon = new Image("icons/discount.png");
        final ImageView discountView = new ImageView(discountIcon);
        discountView.setFitHeight(40);
        discountView.setPreserveRatio(true);

        final MenuButton discountBtn = new MenuButton();
        discountBtn.getStyleClass().add("option-key");
        discountBtn.setPrefSize(60, 40);
        discountBtn.setGraphic(discountView);

        MenuItem clearItem = new MenuItem("Без скидки");
        clearItem.setOnAction(e -> applyDiscount(0));
        discountBtn.getItems().add(clearItem);

        for (int percent : DISCOUNT_OPTIONS) {
          final int value = percent;
          MenuItem mi = new MenuItem(percent + "%");
          mi.setOnAction(e -> applyDiscount(value));
          discountBtn.getItems().add(mi);
        }

        return discountBtn;
      }

      private void applyDiscount(int percent) {
        ItemDto itemDto = getTableView().getItems().get(getIndex());
        int next = (percent != 0 && itemDto.getDiscount() == percent) ? 0 : percent;
        itemDto.setDiscount(next);
        itemDto.recomputeTotalPrice();
        getTableView().refresh();
        supplier.get();
      }
    };
  }
}
