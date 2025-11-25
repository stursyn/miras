package kz.satpaev.sunkar.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StickerDTO {
  private String barcode;
  private String name;
  private BigDecimal price;
  private Integer weight;
  private LocalDate localDate;
}
