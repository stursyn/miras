package kz.satpaev.sunkar.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import kz.satpaev.sunkar.callbacks.ShowSaleDetailButtonCallback;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.model.entity.Sale;
import kz.satpaev.sunkar.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.function.Supplier;

@Component
public class SalesController implements Initializable {
  private static int PAGE_SIZE = 30;
  public Supplier<StackPane> rootStackPane = () -> null;

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

    int countAll = saleRepository.countAllBySaleTimeBetween(LocalDate.now().atStartOfDay(),
        LocalDate.now().atTime(LocalTime.MAX));
    int pages = (int) Math.ceil(countAll * 1.0 / PAGE_SIZE);
    pagination.setPageCount(pages);
    pagination.setPageFactory(this::loadPage);
  }

  private Node loadPage(int pageIndex) {
    System.out.println("Loading page " + pageIndex);
    Page<Sale> dbSales = saleRepository.findAllBySaleTimeBetween(LocalDate.now().atStartOfDay(),
        LocalDate.now().atTime(LocalTime.MAX), PageRequest.of(
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
