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
  private SimpleDoubleProperty cashAmount = new SimpleDoubleProperty(0);
  private SimpleDoubleProperty kaspiAmount = new SimpleDoubleProperty(0);
  private SimpleDoubleProperty halykAmount = new SimpleDoubleProperty(0);
  private SimpleDoubleProperty dutyAmount = new SimpleDoubleProperty(0);
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

  public double getCashAmount() {
    return cashAmount.get();
  }

  public void setCashAmount(BigDecimal cashAmount) {
    if (cashAmount == null) return;
    this.cashAmount = new SimpleDoubleProperty(cashAmount.doubleValue());
  }

  public double getKaspiAmount() {
    return kaspiAmount.get();
  }

  public void setKaspiAmount(BigDecimal kaspiAmount) {
    if (kaspiAmount == null) return;
    this.kaspiAmount = new SimpleDoubleProperty(kaspiAmount.doubleValue());
  }

  public double getHalykAmount() {
    return halykAmount.get();
  }

  public void setHalykAmount(BigDecimal halykAmount) {
    if (halykAmount == null) return;
    this.halykAmount = new SimpleDoubleProperty(halykAmount.doubleValue());
  }

  public double getDutyAmount() {
    return dutyAmount.get();
  }

  public void setDutyAmount(BigDecimal dutyAmount) {
    if (dutyAmount == null) return;
    this.dutyAmount = new SimpleDoubleProperty(dutyAmount.doubleValue());
  }

  public String getPaymentType() {
    return paymentType.get();
  }

  public void setPaymentType(PaymentType paymentType) {
    if (paymentType == null) return;
    this.paymentType = new SimpleStringProperty(paymentType.getValue());
  }
}