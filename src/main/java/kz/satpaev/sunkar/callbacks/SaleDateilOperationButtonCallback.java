package kz.satpaev.sunkar.callbacks;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import kz.satpaev.sunkar.controllers.AbstractController;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.repository.SaleRepository;

import java.util.function.Supplier;

public class SaleDateilOperationButtonCallback implements Callback<TableColumn<SaleDto, String>, TableCell<SaleDto, String>> {
  public Supplier<StackPane> rootStackPane;
  public Supplier<?> supplier;
  public SaleRepository saleRepository;
  public SaleDateilOperationButtonCallback(Supplier<StackPane> rootStackPane, Supplier<?> supplier, SaleRepository saleRepository) {
    super();
    this.rootStackPane = rootStackPane;
    this.supplier = supplier;
    this.saleRepository = saleRepository;
  }
  @Override
  public TableCell call(final TableColumn<SaleDto, String> param) {
    return new TableCell<SaleDto, String>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {

          final Image search = new Image("icons/search.png");
          final ImageView searchView = new ImageView(search);
          final Button searchButton = new Button();
          searchButton.getStyleClass().add("option-key");

          final HBox pane = new HBox();

          searchView.setFitHeight(20);
          searchView.setPreserveRatio(true);
          searchButton.setOnAction(event -> {
            SaleDto saleDto = getTableView().getItems().get(getIndex());
            supplier.get();
          }
          );
          searchButton.setPrefSize(50,50);
          searchButton.setGraphic(searchView);

          pane.getChildren().addAll(searchButton);
          pane.setSpacing(5);

          setGraphic(pane);
        }
          setText(null);
      }
    };
  }
}