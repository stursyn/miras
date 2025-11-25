package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import kz.satpaev.sunkar.model.dto.StickerDTO;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.model.entity.SubItem;
import kz.satpaev.sunkar.repository.ItemRepository;
import kz.satpaev.sunkar.repository.SubItemRepository;
import kz.satpaev.sunkar.service.NiimbotB1PrinterService;
import kz.satpaev.sunkar.service.StickerService;
import kz.satpaev.sunkar.util.AppUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW;

@Component
public class WeightedItemController implements Initializable {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private SubItemRepository subItemRepository;
  @Autowired
  private StickerService stickerService;

  @FXML
  private VBox keyboard;
  @FXML
  public TextField barcode;
  @FXML
  public Label name;
  @FXML
  public TextField sellPrice;
  @FXML
  public TextField weight;
  @FXML
  public Button print;
  @FXML
  public Button cancelButton;

  public void keyPressed(KeyEvent event) {
    if (event.getTarget() == barcode &&
        (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB)) {
      var item = itemRepository.findItemByBarcode(barcode.getText());
      if (item != null) {
        name.setText(item.getName());
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    barcode.setOnKeyPressed(this::keyPressed);
    keyboard.getChildren().add(KEYBOARD_VIEW);
  }

  public void saveAndPrint() {
    String subItemBarcode = AppUtil.generateBarcode(subItemRepository.subItemSeqNextVal());
    var item = itemSave(subItemBarcode);
    if (item != null) {
      var parentItem = itemRepository.findItemByBarcode(barcode.getText());
      try (var printer = new NiimbotB1PrinterService("COM3", 9600)) {
        printer.print(stickerService.renderSticker(StickerDTO.builder()
            .barcode(item.getCode())
            .price(item.getSellPrice())
            .name(parentItem.getName())
            .localDate(LocalDate.now())
            .weight(item.getWeight() == null ? null : item.getWeight().intValue())
            .build()));
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private SubItem itemSave(String subItemBarcode) {
    if (StringUtils.isEmpty(barcode.getText())) return null;
    if (StringUtils.isEmpty(subItemBarcode)) return null;
    if (StringUtils.isEmpty(sellPrice.getText())) return null;

    try {
      SubItem newItem = subItemRepository.findById(subItemBarcode).orElse(new SubItem());
      newItem.setCode(subItemBarcode);
      newItem.setParentBarCode(barcode.getText());
      if (sellPrice.getText() != null) {
        newItem.setSellPrice(new BigDecimal(sellPrice.getText()));
      }
      if (!StringUtils.isEmpty(weight.getText()) && !"null".equals(weight.getText())) {
        newItem.setWeight(new BigDecimal(weight.getText()));
      }

      return subItemRepository.save(newItem);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
