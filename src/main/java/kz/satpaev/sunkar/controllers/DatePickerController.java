package kz.satpaev.sunkar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW_NUMERIC;

@Component
public class DatePickerController implements Initializable {
  @FXML
  public DatePicker myDatePicker;
  @FXML
  public Button cancelButton;
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }
}
