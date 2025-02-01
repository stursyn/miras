package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.repository.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ItemController implements Initializable {

  @Autowired
  private ItemRepository itemRepository;

  @FXML
  public TextField barcode;
  @FXML
  public TextField name;
  @FXML
  public TextField sellPrice;
  @FXML
  public TextField quantity;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void save() {
    Item newItem = itemRepository.findItemByBarcode(barcode.getText());
    if(newItem == null) newItem = new Item();
    newItem.setBarcode(barcode.getText());
    newItem.setName(name.getText());
    if (sellPrice.getText() != null) {
      newItem.setSellPrice(new BigDecimal(sellPrice.getText()));
    }
    if (!StringUtils.isEmpty(quantity.getText())) {
      newItem.setCurrentQuantity(Integer.parseInt(quantity.getText()));
    } else {
      newItem.setCurrentQuantity(0);
    }

    itemRepository.save(newItem);

    Stage stage = (Stage) barcode.getScene().getWindow();
    stage.close();
  }
}
