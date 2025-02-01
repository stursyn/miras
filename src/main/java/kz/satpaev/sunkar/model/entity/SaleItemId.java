package kz.satpaev.sunkar.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaleItemId  implements Serializable {
    private Long itemId;
    private Long saleId;
}
