package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW_NUMERIC;

@Component
public class PaymentController implements Initializable {
  @FXML
  private VBox keyboard;
  @FXML
  public TextField price;
  @FXML
  public Button submitButton;
  @FXML
  public Button cancelButton;
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    keyboard.getChildren().add(KEYBOARD_VIEW_NUMERIC);
  }
}
