package kz.satpaev.sunkar.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.model.entity.PaymentType;
import kz.satpaev.sunkar.util.UiControllerUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Consumer;

import static kz.satpaev.sunkar.Main.applicationContext;
import static kz.satpaev.sunkar.util.Constants.TENGE_SUFFIX;

public class AbstractController {

  public void dbAddNewItem(Item item, StackPane rootStackPane, Consumer<Item> callback) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Item.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      ItemController controller = loader.getController();
      controller.barcode.setText(item.getBarcode());
      controller.name.setText(item.getName());
      controller.quantity.setText(item.getCurrentQuantity() + "");
      controller.sellPrice.setText(item.getSellPrice() + "");
      controller.sellPrice.requestFocus();

      Platform.runLater(() -> controller.sellPrice.requestFocus());

      controller.save.setOnAction(event -> {
        Item ret = controller.itemSave();
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
        callback.accept(ret);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
        callback.accept(null);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void makeCountOfObject(StackPane rootStackPane, Consumer<Integer> callback) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Count.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      CountController controller = loader.getController();

      Platform.runLater(() -> controller.count.requestFocus());

      controller.submitButton.setOnAction(event -> {
        String text = controller.count.getText();
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
        if (StringUtils.isNotBlank(text)) {
          callback.accept(Integer.parseInt(text));
        } else {
          callback.accept(null);
        }
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);

        callback.accept(null);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void showSaleDetail(StackPane rootStackPane, SaleDto saleDto, Consumer<String> callback) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/SaleDetail.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      SaleDetailController controller = loader.getController();
      controller.saleId = saleDto::getId;
      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);

        callback.accept(null);
      });
      controller.loadTable();
      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
