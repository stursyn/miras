package kz.satpaev.sunkar.model.projection;

import kz.satpaev.sunkar.model.entity.PaymentType;

import java.math.BigDecimal;

public interface SaleSummaryProjection {
  PaymentType getPaymentType();
  BigDecimal getTotalAmount();
  Integer getTotalCount();
}
