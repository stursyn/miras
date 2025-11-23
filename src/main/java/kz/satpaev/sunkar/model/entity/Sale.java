package kz.satpaev.sunkar.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sale_seq")
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private BigDecimal amount;
    private LocalDateTime saleTime;
}
