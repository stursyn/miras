package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hold_sale")
public class HoldSale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "hold_sale_seq")
    private Long id;
    private BigDecimal amount;
    private LocalDateTime saleTime;
}
