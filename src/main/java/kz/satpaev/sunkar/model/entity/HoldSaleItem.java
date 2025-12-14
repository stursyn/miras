package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "hold_sale_item")
@IdClass(HoldSaleItemId.class)
public class HoldSaleItem {
    @Id
    private Long holdSaleId;
    @Id
    private String itemBarcode;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
