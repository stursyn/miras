package kz.satpaev.sunkar.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import kz.satpaev.sunkar.callbacks.ShowSaleDetailButtonCallback;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.model.entity.PaymentType;
import kz.satpaev.sunkar.model.entity.Sale;
import kz.satpaev.sunkar.model.projection.SaleSummaryProjection;
import kz.satpaev.sunkar.repository.SaleRepository;
import kz.satpaev.sunkar.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static kz.satpaev.sunkar.util.Constants.TENGE_SUFFIX;
import static kz.satpaev.sunkar.util.Constants.ruDateTimeFormatter;

@Component
public class SalesController implements Initializable {
  private static int PAGE_SIZE = 30;
  public Supplier<StackPane> rootStackPane = () -> null;
  public LocalDate workingDate = LocalDate.now();

  @Autowired
  private SaleRepository saleRepository;

  @FXML
  private Pagination pagination;
  @FXML
  private TableView<SaleDto> salesTable;
  @FXML
  public TableColumn<SaleDto, Long> id;
  @FXML
  public TableColumn<SaleDto, String> saleTime;
  @FXML
  public TableColumn<SaleDto, Double> saleAmount;
  @FXML
  public TableColumn<SaleDto, String> paymentType;
  @FXML
  public TableColumn<SaleDto, String> operation;
  @FXML
  public Button cancelButton;
  @FXML
  public Label title;
  @FXML
  public Label totalAmount;
  @FXML
  public Label totalAmountNote;
  @FXML
  public Label avgReceipt;
  @FXML
  public Label cash;
  @FXML
  public Label kaspi;
  @FXML
  public Label halyk;
  @FXML
  public Label duty;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    id.setCellValueFactory(new PropertyValueFactory<>("id"));
    saleTime.setCellValueFactory(new PropertyValueFactory<>("SaleTime"));
    saleAmount.setCellValueFactory(new PropertyValueFactory<>("SaleAmount"));
    paymentType.setCellValueFactory(new PropertyValueFactory<>("PaymentType"));

    operation.setCellFactory(new ShowSaleDetailButtonCallback(() -> rootStackPane.get(), () -> null, saleRepository));

    id.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
    saleTime.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.4));
    saleAmount.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));
    paymentType.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));
    operation.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
  }

  public void loadSales(LocalDate date) {
    workingDate = date;
    title.setText("Продажи за " + ruDateTimeFormatter.format(date));

    List<SaleSummaryProjection> saleSummary = saleRepository.saleSummaryByPaymentType(date.atStartOfDay(),
        date.atTime(LocalTime.MAX));

    BigDecimal amount = saleSummary.stream().map(SaleSummaryProjection::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    int count = saleSummary.stream().map(SaleSummaryProjection::getTotalCount).reduce(0, Integer::sum);
    totalAmount.setText(String.format("%.1f", amount) + TENGE_SUFFIX);
    totalAmountNote.setText(String.format("в рамках %d транзакции", count));
    if (count > 0) {
      avgReceipt.setText(String.format("%.1f", amount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)) + Constants.TENGE_SUFFIX);
    }
    for (SaleSummaryProjection saleSummaryProjection : saleSummary) {
      if (saleSummaryProjection.getPaymentType() == PaymentType.CASH) {
        cash.setText(String.format("%.1f", saleSummaryProjection.getTotalAmount()) + Constants.TENGE_SUFFIX);
      }
      if (saleSummaryProjection.getPaymentType() == PaymentType.KASPI) {
        kaspi.setText(String.format("%.1f", saleSummaryProjection.getTotalAmount()) + Constants.TENGE_SUFFIX);
      }
      if (saleSummaryProjection.getPaymentType() == PaymentType.HALYK) {
        halyk.setText(String.format("%.1f", saleSummaryProjection.getTotalAmount()) + Constants.TENGE_SUFFIX);
      }
      if (saleSummaryProjection.getPaymentType() == PaymentType.DUTY) {
        duty.setText(String.format("%.1f", saleSummaryProjection.getTotalAmount()) + Constants.TENGE_SUFFIX);
      }
    }

    int pages = (int) Math.ceil(count * 1.0 / PAGE_SIZE);
    pagination.setPageCount(pages);
    pagination.setPageFactory(this::loadPage);
  }

  private Node loadPage(int pageIndex) {
    Page<Sale> dbSales = saleRepository.findAllBySaleTimeBetween(workingDate.atStartOfDay(),
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
                  saleDto.setPaymentType(item.getPaymentType());
                  saleDto.setSaleAmount(item.getAmount());
                  return saleDto;
                })
                .toList()

        )
    );

    return salesTable;
  }
}
