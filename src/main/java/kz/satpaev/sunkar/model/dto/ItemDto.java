package kz.satpaev.sunkar.model.dto;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ItemDto {
  private final SimpleStringProperty barcode = new SimpleStringProperty();
  private final SimpleStringProperty itemName = new SimpleStringProperty();
  private final SimpleDoubleProperty price = new SimpleDoubleProperty(0);
  private final SimpleIntegerProperty count = new SimpleIntegerProperty(0);
  private final SimpleIntegerProperty discount = new SimpleIntegerProperty(0);
  private final SimpleDoubleProperty totalPrice = new SimpleDoubleProperty(0);

  public String getBarcode() {
    return barcode.get();
  }

  public void setBarcode(String barcode) {
    this.barcode.set(barcode);
  }

  public String getItemName() {
    return itemName.get();
  }

  public void setItemName(String itemName) {
    this.itemName.set(itemName);
  }

  public double getPrice() {
    return price.get();
  }

  public void setPrice(double price) {
    this.price.set(price);
  }

  public int getCount() {
    return count.get();
  }

  public void setCount(int count) {
    this.count.set(count);
  }

  public int getDiscount() {
    return discount.get();
  }

  public void setDiscount(int discount) {
    this.discount.set(discount);
  }

  public double getTotalPrice() {
    return totalPrice.get();
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice.set(totalPrice);
  }

  public void recomputeTotalPrice() {
    setTotalPrice(Math.ceil(getCount() * getPrice() * (100 - getDiscount()) / 100.0));
  }
}
