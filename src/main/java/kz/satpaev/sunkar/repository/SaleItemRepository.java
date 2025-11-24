package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.SaleItem;
import kz.satpaev.sunkar.model.entity.SaleItemId;
import kz.satpaev.sunkar.model.projection.SaleDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, SaleItemId> {
  @Query("select i.barcode as barcode, i.name as name, s.quantity as quantity, s.unitPrice as price from SaleItem s " +
      " inner join Item i on i.barcode = s.itemBarcode " +
      " where s.saleId = :saleId")
  List<SaleDetailProjection> findSaleDetailBySaleId(Long saleId);
}
