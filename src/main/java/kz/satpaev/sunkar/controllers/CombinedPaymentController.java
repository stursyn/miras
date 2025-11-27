package kz.satpaev.sunkar.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.util.Constants.KEYBOARD_VIEW_NUMERIC;

@Component
public class CombinedPaymentController implements Initializable {
  private static int checkedElementCount = 0;
  @FXML
  private VBox keyboard;
  @FXML
  public TextField cashAmount;
  @FXML
  public TextField kaspiAmount;
  @FXML
  public TextField halykAmount;
  @FXML
  public TextField dutyAmount;
  @FXML
  public CheckBox kaspi;
  @FXML
  public CheckBox halyk;
  @FXML
  public CheckBox duty;
  @FXML
  public Button submitButton;
  @FXML
  public Button cancelButton;
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    kaspi.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        checkedElementCount++;
        if (checkedElementCount > 1) {
          kaspiAmount.setVisible(true);
        }
      } else {
        kaspiAmount.setText("");
        kaspiAmount.setVisible(false);
        checkedElementCount--;
      }
      submitButtonLogic();
    });
    halyk.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        checkedElementCount++;
        if (checkedElementCount > 1) {
          halykAmount.setVisible(true);
        }
      } else {
        halykAmount.setText("");
        halykAmount.setVisible(false);
        checkedElementCount--;
      }
      submitButtonLogic();
    });
    duty.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        checkedElementCount++;
        if (checkedElementCount > 1) {
          dutyAmount.setVisible(true);
        }
      } else {
        dutyAmount.setText("");
        dutyAmount.setVisible(false);
        checkedElementCount--;
      }
      submitButtonLogic();
    });

    keyboard.getChildren().add(KEYBOARD_VIEW_NUMERIC);
  }

  public void clearView() {
    cashAmount.clear();
    kaspiAmount.clear();
    halykAmount.clear();
    dutyAmount.clear();
    kaspi.setSelected(false);
    halyk.setSelected(false);
    duty.setSelected(false);
    checkedElementCount = 0;
  }

  private void submitButtonLogic() {
    if (checkedElementCount > 0) {
      submitButton.setDisable(false);
    } else {
      submitButton.setDisable(true);
    }
  }
}
