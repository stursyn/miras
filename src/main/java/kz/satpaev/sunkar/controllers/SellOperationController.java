package kz.satpaev.sunkar.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import kz.satpaev.sunkar.callbacks.ItemRemoveButtonCallback;
import kz.satpaev.sunkar.controllers.keyboardfx.KeyboardView;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.entity.*;
import kz.satpaev.sunkar.repository.ItemRepository;
import kz.satpaev.sunkar.repository.SaleItemRepository;
import kz.satpaev.sunkar.repository.SaleRepository;
import kz.satpaev.sunkar.repository.SubItemRepository;
import kz.satpaev.sunkar.util.Constants;
import kz.satpaev.sunkar.util.UiControllerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static kz.satpaev.sunkar.Main.applicationContext;
import static kz.satpaev.sunkar.util.Constants.*;

@Component
public class SellOperationController implements Initializable {
  private StringBuilder sb = new StringBuilder();
  private long lastKeyTime = 0;
  private BigDecimal totalSumBD;

  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private SaleRepository saleRepository;
  @Autowired
  private SaleItemRepository sellItemRepository;
  @Autowired
  private SubItemRepository subItemRepository;

  @FXML
  private StackPane rootStackPane;
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
  private Label digitalClock;
  @FXML
  private Label totalSum;
  @FXML
  private Label paidAmount;
  @FXML
  private Label kaspiAmount;
  @FXML
  private Label halykAmount;
  @FXML
  private Label dutyAmount;
  @FXML
  private Label returnAmount;
  @FXML
  private Button cashButton;
  @FXML
  private Button combinedButton;
  @FXML
  private Button byCard;
  @FXML
  private Button qrKaspi;
  @FXML
  private Button duty;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    barcode.setCellValueFactory(new PropertyValueFactory<>("Barcode"));
    itemName.setCellValueFactory(new PropertyValueFactory<>("ItemName"));
    price.setCellValueFactory(new PropertyValueFactory<>("Price"));
    count.setCellValueFactory(new PropertyValueFactory<>("Count"));
    totalPrice.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));

    operation.setCellFactory(new ItemRemoveButtonCallback(rootStackPane, () -> {
      countTotalSum();
      return null;
    }, itemRepository));

    barcode.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
    itemName.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.4));
    price.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
    count.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
    totalPrice.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
    operation.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.2));
    combinedButton.setOnAction(event -> {
      combined();
    });
    cashButton.setOnAction(event -> {
      cash();
    });
    byCard.setOnAction(event -> {
      paidRegister(PaymentType.HALYK);
      halykAmount.setText(totalSumBD + TENGE_SUFFIX);
    });
    qrKaspi.setOnAction(event -> {
      paidRegister(PaymentType.KASPI);
      kaspiAmount.setText(totalSumBD + TENGE_SUFFIX);
    });
    duty.setOnAction(event -> {
      paidRegister(PaymentType.DUTY);
      dutyAmount.setText(totalSumBD + TENGE_SUFFIX);
    });

    countTotalSum();
    paidAmount.setText(0 + TENGE_SUFFIX);
    returnAmount.setText(0 + TENGE_SUFFIX);
    kaspiAmount.setText(0 + TENGE_SUFFIX);
    halykAmount.setText(0 + TENGE_SUFFIX);
    dutyAmount.setText(0 + TENGE_SUFFIX);

    digitalClock.setText(LocalDateTime.now().format(formatter_without_seconds));
    Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
      digitalClock.setText(LocalDateTime.now().format(formatter_without_seconds));
    }));
    timeline.setCycleCount(Timeline.INDEFINITE); // Make it run indefinitely
    timeline.play(); // Start the timeline

    KEYBOARD_VIEW.setMode(KeyboardView.Mode.STANDARD);
  }

  public void combined() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/CombinedPayment.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      CombinedPaymentController controller = loader.getController();

      Platform.runLater(() -> {
        controller.clearView();
        controller.cashAmount.requestFocus();
      });

      controller.submitButton.setOnAction(event -> {
        BigDecimal cash = StringUtils.isEmpty(controller.cashAmount.getText()) ? BigDecimal.ZERO : new BigDecimal(controller.cashAmount.getText());
        BigDecimal kaspi = StringUtils.isEmpty(controller.kaspiAmount.getText()) ? BigDecimal.ZERO : new BigDecimal(controller.kaspiAmount.getText());
        BigDecimal halyk = StringUtils.isEmpty(controller.halykAmount.getText()) ? BigDecimal.ZERO : new BigDecimal(controller.halykAmount.getText());
        BigDecimal duty = StringUtils.isEmpty(controller.dutyAmount.getText()) ? BigDecimal.ZERO : new BigDecimal(controller.dutyAmount.getText());

        BigDecimal remain = totalSumBD.subtract(cash)
            .subtract(kaspi)
            .subtract(halyk)
            .subtract(duty);

        if (BigDecimal.ZERO.compareTo(remain) > 0) {
          UiControllerUtil.show((Stage) rootStackPane.getScene().getWindow(), "Сумма платежа не может быть больше ожидаемой суммы платежа", 1000);
          return;
        }

        if (controller.kaspi.isSelected() && BigDecimal.ZERO.compareTo(kaspi) >= 0) {
          kaspi = remain;
        }
        if (controller.halyk.isSelected() && BigDecimal.ZERO.compareTo(halyk) >= 0) {
          halyk = remain;
        }
        if (controller.duty.isSelected() && BigDecimal.ZERO.compareTo(duty) >= 0) {
          duty = remain;
        }

        paidAmount.setText(cash + TENGE_SUFFIX);
        kaspiAmount.setText(kaspi + TENGE_SUFFIX);
        halykAmount.setText(halyk + TENGE_SUFFIX);
        dutyAmount.setText(duty + TENGE_SUFFIX);

        paidRegister(PaymentType.COMBINED, cash, kaspi, halyk, duty);
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void keyPressed(KeyEvent event) {
    if (UiControllerUtil.hasOpacityRectangle(rootStackPane)) return;
    long now = System.currentTimeMillis();

    // если пауза слишком длинная — начать заново
    if (now - lastKeyTime > 50) {
      sb.setLength(0);
    }
    lastKeyTime = now;

    // Добавляем только цифры
    if (event.getCode().isDigitKey()) {
      sb.append(event.getText());
    }

    if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
      String barCode = sb.toString();
      Platform.runLater(() -> itemTable.refresh());
      Platform.runLater(() -> rootStackPane.requestFocus());
      if (barCode.startsWith(MERCHANT_CODE+KZ_GS1_CODE)) {
        searchInSubItem(barCode);
      } else {
        redrawTableByBarcode(barCode);
      }
    }
  }

  private void searchInSubItem(String barCode) {
    Platform.runLater(() -> {
      SubItem subItem = subItemRepository.findItemByCode(barCode);
      if(subItem != null) {
        Item dbItem = itemRepository.findItemByBarcode(subItem.getParentBarCode());
        if (dbItem != null) {
          ItemDto displayItem = new ItemDto();
          displayItem.setBarcode(subItem.getCode());
          displayItem.setItemName(dbItem.getName());
          if (subItem.getSellPrice() != null) {
            displayItem.setPrice(subItem.getSellPrice().doubleValue());
          }
          displayItem.setCount(1);
          displayItem.setTotalPrice(displayItem.getCount() * displayItem.getPrice());
          itemTable.getItems().add(displayItem);
        }
      }
      countTotalSum();
    });
  }

  private void redrawTableByBarcode(String barCode) {
    Platform.runLater(() -> {

      ItemDto foundItem = null;
      for (ItemDto item : itemTable.getItems()) {
        if (barCode.equals(item.getBarcode())) {
          item.setCount(item.getCount() + 1);
          item.setTotalPrice(item.getCount() * item.getPrice());
          foundItem = item;

          itemTable.refresh();
        }
      }

      if (foundItem == null) {
        if (itemTableAddNewItem(barCode) == null) {
          dbAddNewItem("Неизвестный товар", barcodeText -> {
            itemTableAddNewItem(barcodeText);
            countTotalSum();
          });
        }
      }

      countTotalSum();
    });
  }

  private Item itemTableAddNewItem(String barCode) {
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
    }

    return dbItem;
  }

  public void addNewProduct() {
    dbAddNewItem(null, barcodeText -> countTotalSum());
  }

  public void saleHistory(LocalDate date) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Sales.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      SalesController controller = loader.getController();

      controller.rootStackPane = () -> rootStackPane;
      controller.loadSales(date);
      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void todayHistory() {
    saleHistory(LocalDate.now());
  }
  public void historyByDate() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DatePicker.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      DatePickerController controller = loader.getController();

      controller.myDatePicker.setOnAction(event -> {
        var pickedDate = controller.myDatePicker.getValue();
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);

        saleHistory(pickedDate);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void universalItem() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/UniversalItem.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      UniversalItemController controller = loader.getController();

      Platform.runLater(() -> controller.price.requestFocus());

      controller.submitButton.setOnAction(event -> {
        String text = controller.price.getText();
        if (StringUtils.isNotEmpty(text)) {
          try {
            BigDecimal price = new BigDecimal(text);
            var subItem = createSubItem(subItemRepository.subItemSeqNextVal(), price);
            ItemDto newItem = new ItemDto();
            newItem.setBarcode(subItem.getCode());
            newItem.setItemName(UNIVERSAL_PRODUCT_TITLE);
            newItem.setPrice(price.doubleValue());
            newItem.setCount(1);
            newItem.setTotalPrice(price.doubleValue() * newItem.getCount());
            Platform.runLater(()->{
              itemTable.getItems().add(newItem);
              itemTable.refresh();
              countTotalSum();
            });
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }

        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private SubItem createSubItem(Long seq, BigDecimal price) {
    SubItem subItem = new SubItem();
    subItem.setCode(UNIVERSAL_PRODUCT_BARCODE + "_" + seq);
    subItem.setCurrentQuantity(0);
    subItem.setSellPrice(price);
    subItem.setParentBarCode(UNIVERSAL_PRODUCT_BARCODE);
    return subItemRepository.save(subItem);
  }

  public void cash() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Payment.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      PaymentController controller = loader.getController();

      Platform.runLater(() -> controller.price.requestFocus());

      controller.submitButton.setOnAction(event -> {
        String text = controller.price.getText();
        if (StringUtils.isNotEmpty(text)) {
          try {
            BigDecimal price = new BigDecimal(text);
            paidAmount.setText(price + TENGE_SUFFIX);
            returnAmount.setText(totalSumBD.subtract(price).multiply(BigDecimal.valueOf(-1)) + TENGE_SUFFIX);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }

        paidRegister(PaymentType.CASH);
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void fastItem() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/FastOperation.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      FastOperationController controller = loader.getController();

      Platform.runLater(()->controller.nameLike.requestFocus());
      controller.callback = barCode -> {
        if (StringUtils.isNotEmpty(barCode)) {
          redrawTableByBarcode(barCode);
        }
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      };

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void paidRegister(PaymentType paymentType) {
    switch (paymentType) {
      case CASH:
        paidRegister(paymentType, totalSumBD, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        break;
      case DUTY:
        paidRegister(paymentType, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalSumBD);
        break;
      case KASPI:
        paidRegister(paymentType, BigDecimal.ZERO, totalSumBD, BigDecimal.ZERO, BigDecimal.ZERO);
        break;
      case HALYK:
        paidRegister(paymentType, BigDecimal.ZERO, BigDecimal.ZERO, totalSumBD, BigDecimal.ZERO);
        break;
    }
  }
  public void paidRegister(PaymentType paymentType,
                           BigDecimal cash, BigDecimal kaspi,
                           BigDecimal halyk, BigDecimal duty) {
    if (itemTable.getItems().size() <= 0) return;

    var sale = new Sale();
    sale.setSaleTime(LocalDateTime.now());
    sale.setAmount(totalSumBD);
    sale.setPaymentType(paymentType);
    sale.setCashAmount(cash);
    sale.setKaspiAmount(kaspi);
    sale.setHalykAmount(halyk);
    sale.setDutyAmount(duty);
    sale = saleRepository.save(sale);

    var saveList = new ArrayList<SaleItem>();
    for (ItemDto item : itemTable.getItems()) {
      var saleItem = new SaleItem();
      saleItem.setItemBarcode(item.getBarcode());
      saleItem.setSaleId(sale.getId());
      saleItem.setQuantity(item.getCount());
      saleItem.setUnitPrice(BigDecimal.valueOf(item.getPrice()));
      saveList.add(saleItem);
    }
    sellItemRepository.saveAll(saveList);

    itemTable.getItems().clear();
  }

  public void dbAddNewItem(String name, Consumer<String> save) {
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

      Platform.runLater(() -> controller.sellPrice.requestFocus());

      controller.save.setOnAction(event -> {
        String barcodeText = controller.barcode.getText();
        controller.itemSave();

        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);

        save.accept(barcodeText);
      });

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addSubItem() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/WeightedItem.fxml"));
      loader.setControllerFactory(applicationContext::getBean);
      Parent root = loader.load();
      WeightedItemController controller = loader.getController();

      Platform.runLater(() -> controller.barcode.requestFocus());

      controller.cancelButton.setOnAction(event -> {
        rootStackPane.getChildren().remove(root);
        UiControllerUtil.removeOpacityRectangle(rootStackPane);
      });

      UiControllerUtil.addOpacityRectangle(rootStackPane);
      rootStackPane.getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void clearAll() {
    itemTable.getItems().clear();
    countTotalSum();
  }

  private void countTotalSum() {
    sb.setLength(0);

    double total = 0;
    for (ItemDto item : itemTable.getItems()) {
      total += item.getTotalPrice();
    }
    this.totalSumBD = BigDecimal.valueOf(total);
    totalSum.setText(this.totalSumBD + TENGE_SUFFIX);

    returnAmount.setText(0 + TENGE_SUFFIX);
    paidAmount.setText(0 + TENGE_SUFFIX);
    kaspiAmount.setText(0 + TENGE_SUFFIX);
    halykAmount.setText(0 + TENGE_SUFFIX);
    dutyAmount.setText(0 + TENGE_SUFFIX);
  }
}
