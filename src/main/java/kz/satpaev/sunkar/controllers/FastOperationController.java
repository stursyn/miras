package kz.satpaev.sunkar.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kz.satpaev.sunkar.controllers.keyboardfx.Keyboard;
import kz.satpaev.sunkar.controllers.keyboardfx.skins.KeyView;
import kz.satpaev.sunkar.controllers.keyboardfx.skins.KeyViewBase;
import kz.satpaev.sunkar.controllers.keyboardfx.skins.SpecialKeyView;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.repository.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW;

@Component
public class FastOperationController implements Initializable {
    public Consumer<String> callback;

    @Autowired
    private ItemRepository itemRepository;

    @FXML
    private VBox keyboard;
    @FXML
    public TextField nameLike;
    @FXML
    public GridPane productGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLike.setOnKeyTyped(keyEvent -> searchAndRedraw(nameLike.getText()));
        keyboard.getChildren().add(KEYBOARD_VIEW);
    }

    public void searchAndRedraw(String text) {
        List<Item> itemByNameLikeIgnoreCase = itemRepository.findItemByNameLikeIgnoreCase("%" + text + "%", Pageable.ofSize(10));
        Platform.runLater(() -> drawGrid(itemByNameLikeIgnoreCase));
    }

    private void drawGrid(List<Item> items) {
        productGrid.getChildren().clear();

        int columnt = 0;
        int row = 0;
        for (Item item : items) {
            if (columnt == 5) {
                columnt = 0;
                row++;
            }
            Button button = new Button(item.getName());
            button.getStyleClass().add("fast-item-button");
            button.getStyleClass().add("wrap-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            button.setWrapText(true);
            button.setOnAction(event -> {
                callback.accept(item.getBarcode());
            });
            GridPane.setFillWidth(button, true);
            GridPane.setHgrow(button, Priority.ALWAYS);

            productGrid.add(button, columnt, row);
            columnt++;
        }
    }
}
