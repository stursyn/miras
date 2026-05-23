package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "buy_item")
public class BuyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "buy_item_seq", allocationSize = 1)
    private long id;
    @Column(name = "item_barcode")
    private String itemBarcode;
    private int quantity;
    @Column(name = "leaving_quantity")
    private Integer leavingQuantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
