package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "sale_item")
@IdClass(SaleItemId.class)
public class SaleItem {
    @Id
    private Long saleId;
    @Id
    private String itemBarcode;
    private Integer quantity;
    private BigDecimal unitPrice;
}
