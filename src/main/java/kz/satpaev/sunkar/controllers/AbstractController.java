package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.entity.Item;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static kz.satpaev.sunkar.Main.applicationContext;

public class AbstractController {

    public void dbAddNewItem(Item item) {
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

            Stage stage = new Stage();
            stage.setTitle("Добавление продукта");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            scene.setOnKeyReleased(controller::keyPressed);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
