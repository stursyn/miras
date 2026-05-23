package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import kz.satpaev.sunkar.model.entity.BuyItem;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.repository.BuyItemRepository;
import kz.satpaev.sunkar.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW;
import static kz.satpaev.sunkar.util.Constants.NUMBER_ONLY_FILTER;

@Slf4j
@Component
public class ItemController implements Initializable {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BuyItemRepository buyItemRepository;

    @FXML
    private VBox keyboard;
    @FXML
    public TextField barcode;
    @FXML
    public TextField name;
    @FXML
    public Label currentQuantityLabel;
    @FXML
    public TextField comingQuantity;
    @FXML
    public Label currentSupplierPriceLabel;
    @FXML
    public TextField comingSupplierPrice;
    @FXML
    public Label currentSellPriceLabel;
    @FXML
    public TextField futureSellPrice;
    @FXML
    public Button save;
    @FXML
    public Button cancelButton;

    public void keyPressed(KeyEvent event) {
        if (event.getTarget() == barcode &&
            (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB)) {
            var item = itemRepository.findItemByBarcode(barcode.getText());
            populateCurrentLabels(item);
            if (item != null) {
                name.setText(item.getName());
            }
        }
    }

    public void populateCurrentLabels(Item item) {
        if (item == null) {
            currentQuantityLabel.setText("0");
            currentSupplierPriceLabel.setText("0");
            currentSellPriceLabel.setText("0");
            comingSupplierPrice.clear();
            futureSellPrice.clear();
            return;
        }
        currentQuantityLabel.setText(String.valueOf(item.getCurrentQuantity() != null ? item.getCurrentQuantity() : 0));
        currentSupplierPriceLabel.setText(item.getCurrentSupplierPrice() != null ? item.getCurrentSupplierPrice().toPlainString() : "0");
        currentSellPriceLabel.setText(item.getSellPrice() != null ? item.getSellPrice().toPlainString() : "0");
        comingSupplierPrice.setText(item.getCurrentSupplierPrice() != null ? item.getCurrentSupplierPrice().toPlainString() : "");
        futureSellPrice.setText(item.getSellPrice() != null ? item.getSellPrice().toPlainString() : "");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        barcode.setOnKeyPressed(this::keyPressed);

        comingQuantity.setTextFormatter(new TextFormatter<>(NUMBER_ONLY_FILTER));
        comingSupplierPrice.setTextFormatter(new TextFormatter<>(NUMBER_ONLY_FILTER));
        futureSellPrice.setTextFormatter(new TextFormatter<>(NUMBER_ONLY_FILTER));

        keyboard.getChildren().add(KEYBOARD_VIEW);
    }

    public Item itemSave() {
        if (StringUtils.isEmpty(barcode.getText())) return null;

        try {
            Item item = itemRepository.findItemByBarcode(barcode.getText());
            if (item == null) {
                item = new Item();
                item.setBarcode(barcode.getText());
            }
            item.setName(name.getText());

            Integer comingQty = StringUtils.isEmpty(comingQuantity.getText())
                ? 0
                : Integer.parseInt(comingQuantity.getText());
            BigDecimal comingBuy = StringUtils.isEmpty(comingSupplierPrice.getText())
                ? null
                : new BigDecimal(comingSupplierPrice.getText());
            BigDecimal futureSell = StringUtils.isEmpty(futureSellPrice.getText())
                ? null
                : new BigDecimal(futureSellPrice.getText());

            int existingQty = item.getCurrentQuantity() != null ? item.getCurrentQuantity() : 0;
            BigDecimal existingBuy = item.getCurrentSupplierPrice();

            item.setCurrentQuantity(existingQty + comingQty);

            if (futureSell != null) {
                item.setSellPrice(futureSell);
            }
            if (comingBuy != null) {
                item.setCurrentSupplierPrice(comingBuy);
            }

            Item savedItem = itemRepository.save(item);

            boolean priceChanged = (comingBuy == null) != (existingBuy == null) || comingBuy != null && comingBuy.compareTo(existingBuy) != 0;

            if (comingQty > 0 || priceChanged) {
                BuyItem buy = new BuyItem();
                buy.setItemBarcode(savedItem.getBarcode());
                buy.setQuantity(comingQty);
                buy.setLeavingQuantity(existingQty);
                buy.setPrice(comingBuy != null ? comingBuy : existingBuy);
                buy.setSellingPrice(savedItem.getSellPrice());
                buy.setCreatedAt(LocalDateTime.now());
                buyItemRepository.save(buy);
            }

            return savedItem;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
