package kz.satpaev.sunkar.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.util.UiControllerUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.function.Consumer;

import static kz.satpaev.sunkar.Main.applicationContext;

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
            UiControllerUtil.addOpacityRectangle(rootStackPane);
            rootStackPane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
