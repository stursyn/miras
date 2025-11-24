package kz.satpaev.sunkar.model.dto;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import kz.satpaev.sunkar.model.entity.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kz.satpaev.sunkar.util.Constants.formatter;

public class SaleDto {
  private SimpleLongProperty id = new SimpleLongProperty(0L);
  private SimpleStringProperty saleTime = new SimpleStringProperty();
  private SimpleDoubleProperty saleAmount = new SimpleDoubleProperty(0);
  private SimpleStringProperty paymentType = new SimpleStringProperty();

  public Long getId() {
    return id.get();
  }

  public void setId(Long id) {
    this.id = new SimpleLongProperty(id);
  }

  public String getSaleTime() {
    return saleTime.get();
  }

  public void setSaleTime(LocalDateTime saleTime) {
    if (saleTime == null) return;
    try {
      this.saleTime = new SimpleStringProperty(formatter.format(saleTime));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public double getSaleAmount() {
    return saleAmount.get();
  }

  public void setSaleAmount(BigDecimal saleAmount) {
    if (saleAmount == null) return;
    this.saleAmount = new SimpleDoubleProperty(saleAmount.doubleValue());
  }

  public String getPaymentType() {
    return paymentType.get();
  }

  public void setPaymentType(PaymentType paymentType) {
    if (paymentType == null) return;
    this.paymentType = new SimpleStringProperty(paymentType.getValue());
  }
}