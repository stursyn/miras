package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.satpaev.sunkar.callbacks.ItemRemoveButtonCallback;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.repository.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.Main.applicationContext;

@Component
public class SellOperationController implements Initializable {

  StringBuilder sb = new StringBuilder();

  @Autowired
  private ItemRepository itemRepository;

  @FXML
  private TableView<ItemDto> itemTable;
  @FXML
  public TableColumn<ItemDto, String> barcode;
  @FXML
  public TableColumn<ItemDto, String> itemName;
  @FXML
  public TableColumn<ItemDto, Integer> price;
  @FXML
  public TableColumn<ItemDto, Integer> count;
  @FXML
  public TableColumn<ItemDto, Integer> totalPrice;
  @FXML
  public TableColumn<ItemDto, String> operation;
  @FXML
  private Label totalSum;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    barcode.setCellValueFactory(new PropertyValueFactory<>("Barcode"));
    itemName.setCellValueFactory(new PropertyValueFactory<>("ItemName"));
    price.setCellValueFactory(new PropertyValueFactory<>("Price"));
    count.setCellValueFactory(new PropertyValueFactory<>("Count"));
    totalPrice.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));

    operation.setCellFactory(new ItemRemoveButtonCallback());
  }

  public void keyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      String barCode = sb.toString();

      ItemDto foundItem = null;
      for (ItemDto item : itemTable.getItems()) {
        if (barCode.equals(item.getBarcode())) {
          item.setCount(item.getCount() + 1);
          item.setTotalPrice(item.getCount() * item.getPrice());
          foundItem = item;

          itemTable.refresh();
        }
      }

      if(foundItem == null) {
        itemTableAddNewItem(barCode);
      }

      sb.setLength(0);

      countTotalSum();
      return;
    }
    sb.append(event.getText());
  }

  private void itemTableAddNewItem(String barCode) {
    Item dbItem = itemRepository.findItemByBarcode(barCode);
    if (dbItem != null) {
      ItemDto displayItem = new ItemDto();
      displayItem.setBarcode(dbItem.getBarcode());
      displayItem.setItemName(dbItem.getName());
      if (dbItem.getSellPrice() != null) {
        displayItem.setPrice(dbItem.getSellPrice().doubleValue());
      }
      displayItem.setCount(1);
      displayItem.setTotalPrice(displayItem.getCount() * displayItem.getPrice());
      itemTable.getItems().add(displayItem);
    } else {
      dbAddNewItem("Неизвестный товар");
      itemTableAddNewItem(barCode);
    }
  }

  public void paidAll() {
    itemTable.getItems().clear();
    countTotalSum();
  }

  public void dbAddNewItemAction() {
    dbAddNewItem(null);
  }

  public void dbAddNewItem(String name) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Item.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      ItemController controller = loader.getController();
      controller.barcode.setText(sb.toString());
      if (!StringUtils.isEmpty(name)) {
        controller.name.setText(name);
      }
      controller.quantity.setText("1");
      controller.sellPrice.requestFocus();

      Stage stage = new Stage();
      stage.setTitle("Добавление продукта");
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void countTotalSum() {
    double total = 0;
    for (ItemDto item : itemTable.getItems()) {
      total += item.getTotalPrice();
    }
    totalSum.setText(total + "");
  }
}
