package kz.satpaev.sunkar.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class HoldSaleItemId implements Serializable {
    private String itemBarcode;
    private Long holdSaleId;
}
