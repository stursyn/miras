package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.HoldSaleItem;
import kz.satpaev.sunkar.model.entity.HoldSaleItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldSaleItemRepository extends JpaRepository<HoldSaleItem, HoldSaleItemId> {
  List<HoldSaleItem> findByHoldSaleId(Long holdSaleId);
}
