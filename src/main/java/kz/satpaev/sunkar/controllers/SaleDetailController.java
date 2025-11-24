package kz.satpaev.sunkar.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import kz.satpaev.sunkar.callbacks.ItemRemoveButtonCallback;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.model.entity.Sale;
import kz.satpaev.sunkar.model.projection.SaleDetailProjection;
import kz.satpaev.sunkar.repository.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

@Component
public class SaleDetailController implements Initializable {
  private static int PAGE_SIZE = 30;
  public Supplier<StackPane> rootStackPane = () -> null;
  public Supplier<Long> saleId = () -> null;

  @Autowired
  private SaleItemRepository saleItemRepository;

  @FXML
  private TableView<ItemDto> saleItemTable;
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
  public Button cancelButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    barcode.setCellValueFactory(new PropertyValueFactory<>("Barcode"));
    itemName.setCellValueFactory(new PropertyValueFactory<>("ItemName"));
    price.setCellValueFactory(new PropertyValueFactory<>("Price"));
    count.setCellValueFactory(new PropertyValueFactory<>("Count"));
    totalPrice.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));

//    operation.setCellFactory(new ItemRemoveButtonCallback(rootStackPane, () -> {
//      countTotalSum();
//      return null;
//    }, itemRepository));

    barcode.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.1));
    itemName.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.4));
    price.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.15));
    count.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.1));
    totalPrice.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.15));
    operation.prefWidthProperty().bind(saleItemTable.widthProperty().multiply(0.1));

  }

  public void loadTable() {
    List<SaleDetailProjection> saleDetails = saleItemRepository.findSaleDetailBySaleId(saleId.get());
    System.out.println("saleDetails: " + saleDetails + "; saleId: " + saleId.get());
    saleItemTable.setItems(FXCollections.observableArrayList(
        saleDetails
            .stream()
            .map(saleDetail -> {
              ItemDto itemDto = new ItemDto();
              itemDto.setBarcode(saleDetail.getBarcode());
              itemDto.setItemName(saleDetail.getName());
              if (saleDetail.getPrice() != null) {
                itemDto.setPrice(saleDetail.getPrice().doubleValue());
                if(saleDetail.getQuantity() != null) {
                  itemDto.setTotalPrice(saleDetail.getPrice().multiply(BigDecimal.valueOf(saleDetail.getQuantity())).doubleValue());
                  itemDto.setCount(saleDetail.getQuantity());
                }
              }
              return itemDto;
            })
            .toList()
    ));
  }
}
