package kz.satpaev.sunkar.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import kz.satpaev.sunkar.callbacks.HoldSaleOptionButtonCallback;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.model.entity.HoldSale;
import kz.satpaev.sunkar.model.entity.HoldSaleItem;
import kz.satpaev.sunkar.repository.HoldSaleItemRepository;
import kz.satpaev.sunkar.repository.HoldSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static kz.satpaev.sunkar.util.Constants.ruDateTimeFormatter;

@Component
public class HoldSalesController implements Initializable {
  private static int PAGE_SIZE = 30;
  public Supplier<StackPane> rootStackPane = () -> null;
  public Consumer<List<ItemDto>> saleCheckedConsumer = (tableItems) -> {};
  public LocalDate workingDate = LocalDate.now();

  @Autowired
  private HoldSaleRepository holdSaleRepository;
  @Autowired
  private HoldSaleItemRepository holdSaleItemRepository;

  @FXML
  private Pagination pagination;
  @FXML
  private TableView<SaleDto> salesTable;
  @FXML
  public TableColumn<SaleDto, String> saleTime;
  @FXML
  public TableColumn<SaleDto, Double> saleAmount;
  @FXML
  public TableColumn<SaleDto, String> operation;
  @FXML
  public Button cancelButton;
  @FXML
  public Label title;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    saleTime.setCellValueFactory(new PropertyValueFactory<>("SaleTime"));
    saleAmount.setCellValueFactory(new PropertyValueFactory<>("SaleAmount"));

    operation.setCellFactory(new HoldSaleOptionButtonCallback(() -> rootStackPane.get(), saleId -> {
      ArrayList<ItemDto> ret = new ArrayList<>();
      if(saleId == null) {
        saleCheckedConsumer.accept(ret);
        return;
      }
      List<HoldSaleItem> holdSaleItems = holdSaleItemRepository.findByHoldSaleId(saleId);
      holdSaleItems.forEach(holdSaleItem -> {
        ItemDto itemDto = new ItemDto();
        itemDto.setItemName(holdSaleItem.getItemName());
        itemDto.setCount(holdSaleItem.getQuantity());
        itemDto.setPrice(holdSaleItem.getUnitPrice().doubleValue());
        itemDto.setDiscount(holdSaleItem.getDiscountPercent() == null ? 0 : holdSaleItem.getDiscountPercent());
        itemDto.recomputeTotalPrice();
        itemDto.setBarcode(holdSaleItem.getItemBarcode());
        ret.add(itemDto);
      });
      holdSaleRepository.removeById(saleId);
      saleCheckedConsumer.accept(ret);
    }, holdSaleRepository));

    saleTime.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.4));
    saleAmount.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.4));
    operation.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));

    loadSales(workingDate);
    pagination.setCurrentPageIndex(0);
  }

  public void loadSales(LocalDate date) {
    workingDate = date;
    title.setText("Ожидающие покупки за " + ruDateTimeFormatter.format(date));

    pagination.setPageFactory(this::loadPage);
  }

  private Node loadPage(int pageIndex) {
    Page<HoldSale> dbSales = holdSaleRepository.findAllBySaleTimeBetween(workingDate.atStartOfDay(),
        workingDate.atTime(LocalTime.MAX), PageRequest.of(
            pageIndex, PAGE_SIZE,
            Sort.by("saleTime").descending()
        )
    );

    salesTable.setItems(
        FXCollections.observableArrayList(
            dbSales
                .stream()
                .map(item -> {
                  var saleDto = new SaleDto();
                  saleDto.setId(item.getId());
                  saleDto.setSaleTime(item.getSaleTime());
                  saleDto.setSaleAmount(item.getAmount());
                  return saleDto;
                })
                .toList()

        )
    );

    return salesTable;
  }
}
