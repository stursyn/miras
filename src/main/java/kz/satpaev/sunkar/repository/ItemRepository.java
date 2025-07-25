package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  Item findItemByBarcode(String barCode);
}
