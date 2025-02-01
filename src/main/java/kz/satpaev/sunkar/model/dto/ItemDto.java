package kz.satpaev.sunkar.model.dto;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ItemDto {
  private SimpleStringProperty barcode = new SimpleStringProperty();
  private SimpleStringProperty itemName = new SimpleStringProperty();
  private SimpleDoubleProperty price = new SimpleDoubleProperty(0);
  private SimpleIntegerProperty count = new SimpleIntegerProperty(0);
  private SimpleDoubleProperty totalPrice = new SimpleDoubleProperty(0);

  public String getBarcode() {
    return barcode.get();
  }

  public void setBarcode(String barcode) {
    this.barcode = new SimpleStringProperty(barcode);
  }

  public String getItemName() {
    return itemName.get();
  }

  public void setItemName(String itemName) {
    this.itemName = new SimpleStringProperty(itemName);
  }

  public double getPrice() {
    return price.get();
  }

  public void setPrice(double price) {
    this.price = new SimpleDoubleProperty(price);
  }

  public void setCount(int count) {
    this.count = new SimpleIntegerProperty(count);
  }

  public int getCount() {
    return count.get();
  }

  public double getTotalPrice() {
    return totalPrice.get();
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = new SimpleDoubleProperty(totalPrice);
  }
}
