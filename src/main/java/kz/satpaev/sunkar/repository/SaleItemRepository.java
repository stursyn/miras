package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.SaleItem;
import kz.satpaev.sunkar.model.entity.SaleItemId;
import kz.satpaev.sunkar.model.projection.SaleDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, SaleItemId> {
  @Query("select s.itemBarcode as barcode, " +
      " case when i.name is null then pi.name else i.name end as name, " +
      " s.quantity as quantity, s.unitPrice as price " +
      " from SaleItem s " +
      " left join Item i on i.barcode = s.itemBarcode " +
      " left join SubItem si on si.code = s.itemBarcode " +
      " left join Item pi on pi.barcode = si.parentBarCode " +
      " where s.saleId = :saleId")
  List<SaleDetailProjection> findSaleDetailBySaleId(Long saleId);
}
