package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "buy_item")
public class BuyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "buy_item_seq", allocationSize = 1)
    private long id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
}
