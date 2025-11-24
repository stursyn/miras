package kz.satpaev.sunkar.model.projection;

import java.math.BigDecimal;

public interface SaleDetailProjection {
  String getBarcode();
  String getName();
  BigDecimal getPrice();
  Integer getQuantity();
}
