package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.repository.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW;

@Component
public class ItemController implements Initializable {

    @Autowired
    private ItemRepository itemRepository;

    @FXML
    private VBox keyboard;
    @FXML
    public TextField barcode;
    @FXML
    public TextField name;
    @FXML
    public TextField sellPrice;
    @FXML
    public TextField quantity;
    @FXML
    public Button save;

    public void keyPressed(KeyEvent event) {
        if (event.getTarget() == barcode &&
            (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB)) {
            var item = itemRepository.findItemByBarcode(barcode.getText());
            if (item != null) {
                name.setText(item.getName());
                if (item.getSellPrice() != null) {
                    sellPrice.setText(item.getSellPrice().toString());
                }
                if (item.getCurrentQuantity() != null) {
                    quantity.setText(item.getCurrentQuantity().toString());
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        barcode.setOnKeyPressed(this::keyPressed);
        keyboard.getChildren().add(KEYBOARD_VIEW);
    }

    public Item itemSave() {
        if (StringUtils.isEmpty(barcode.getText())) return null;
        if (StringUtils.isEmpty(sellPrice.getText())) return null;

        try {
            Item newItem = itemRepository.findItemByBarcode(barcode.getText());
            if (newItem == null) newItem = new Item();
            newItem.setBarcode(barcode.getText());
            newItem.setName(name.getText());
            if (sellPrice.getText() != null) {
                newItem.setSellPrice(new BigDecimal(sellPrice.getText()));
            }
            if (!StringUtils.isEmpty(quantity.getText()) && !"null".equals(quantity.getText())) {
                newItem.setCurrentQuantity(Integer.parseInt(quantity.getText()));
            } else {
                newItem.setCurrentQuantity(0);
            }

            return itemRepository.save(newItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
