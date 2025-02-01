package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "buy_item")
public class BuyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "buy_item_seq")
    private long id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
}
