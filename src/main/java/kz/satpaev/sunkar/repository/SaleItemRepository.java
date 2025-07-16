package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.SaleItem;
import kz.satpaev.sunkar.model.entity.SaleItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, SaleItemId> {
}
