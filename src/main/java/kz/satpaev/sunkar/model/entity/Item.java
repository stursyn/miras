package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "item")
public class Item {
  @Id
  private String barcode;
  private String name;
  private String description;
  private BigDecimal sellPrice;
  private BigDecimal currentSupplierPrice;
  private Integer currentQuantity;
  private BigDecimal weight;
  @Column(nullable = false)
  private Boolean deleted = Boolean.FALSE;
  @Column(nullable = false)
  private Boolean canHaveSubItems = Boolean.FALSE;
}


