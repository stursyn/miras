package kz.satpaev.sunkar.callbacks;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import kz.satpaev.sunkar.model.dto.SaleDto;
import kz.satpaev.sunkar.repository.HoldSaleRepository;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HoldSaleOptionButtonCallback implements Callback<TableColumn<SaleDto, String>, TableCell<SaleDto, String>> {
  public Supplier<StackPane> rootStackPane;
  public Consumer<Long> consumer;
  public HoldSaleRepository saleRepository;
  public HoldSaleOptionButtonCallback(Supplier<StackPane> rootStackPane, Consumer<Long> consumer, HoldSaleRepository saleRepository) {
    super();
    this.rootStackPane = rootStackPane;
    this.consumer = consumer;
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
          pane.getChildren().add(getActivateButton(saleDto));
          pane.getChildren().add(getDeactivateButton(saleDto));
          pane.setSpacing(5);

          setGraphic(pane);
        }
          setText(null);
      }

      private Button getActivateButton(SaleDto saleDto) {
        final Image img = new Image("icons/recovery.png");
        final ImageView imgView = new ImageView(img);
        final Button button = new Button();
        button.getStyleClass().add("option-key");

        imgView.setFitHeight(40);
        imgView.setPreserveRatio(true);
        button.setOnAction(event -> consumer.accept(saleDto.getId()));
        button.setPrefSize(40,40);
        button.setGraphic(imgView);
        return button;
      }
      private Button getDeactivateButton(SaleDto saleDto) {
        final Image img = new Image("icons/deactivate.png");
        final ImageView imgView = new ImageView(img);
        final Button button = new Button();
        button.getStyleClass().add("option-key");

        imgView.setFitHeight(40);
        imgView.setPreserveRatio(true);
        button.setOnAction(event -> {
              saleRepository.removeById(saleDto.getId());
              consumer.accept(null);
            }
        );
        button.setPrefSize(40,40);
        button.setGraphic(imgView);
        return button;
      }
    };
  }
}