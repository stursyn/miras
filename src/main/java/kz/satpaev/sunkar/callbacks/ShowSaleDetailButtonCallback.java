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
import kz.satpaev.sunkar.util.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public class ShowSaleDetailButtonCallback implements Callback<TableColumn<SaleDto, String>, TableCell<SaleDto, String>> {
  public Supplier<StackPane> rootStackPane;
  public Supplier<?> supplier;
  public SaleRepository saleRepository;
  public ShowSaleDetailButtonCallback(Supplier<StackPane> rootStackPane, Supplier<?> supplier, SaleRepository saleRepository) {
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
          SaleDto saleDto = getTableView().getItems().get(getIndex());

          final HBox pane = new HBox();
          pane.getChildren().add(getSearchButton(saleDto));
          if (!Boolean.TRUE.equals(saleDto.getDeleted()) &&
              LocalDate.now().equals(LocalDateTime.parse(saleDto.getSaleTime(), Constants.formatter).toLocalDate())) {
            pane.getChildren().add(getDeactivateButton(saleDto));
          }
          pane.setSpacing(5);

          setGraphic(pane);
        }
          setText(null);
      }

      private Button getSearchButton(SaleDto saleDto) {
        final Image search = new Image("icons/search.png");
        final ImageView searchView = new ImageView(search);
        final Button searchButton = new Button();
        searchButton.getStyleClass().add("option-key");

        searchView.setFitHeight(40);
        searchView.setPreserveRatio(true);
        searchButton.setOnAction(event -> {
          new AbstractController().showSaleDetail(rootStackPane.get(), saleDto, sale -> {
            supplier.get();
          });
          supplier.get();
        }
        );
        searchButton.setPrefSize(40,40);
        searchButton.setGraphic(searchView);
        return searchButton;
      }

      private Button getDeactivateButton(SaleDto saleDto) {
        final Image img = new Image("icons/deactivate.png");
        final ImageView imgView = new ImageView(img);
        final Button button = new Button();
        button.getStyleClass().add("option-key");

        imgView.setFitHeight(40);
        imgView.setPreserveRatio(true);
        button.setOnAction(event -> {
              saleRepository.findById(saleDto.getId())
                  .ifPresent(sale -> {
                    sale.setDeleted(Boolean.TRUE);
                    saleRepository.save(sale);
                  });
              supplier.get();
            }
        );
        button.setPrefSize(40,40);
        button.setGraphic(imgView);
        return button;
      }
    };
  }
}