package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  Item findItemByBarcode(String barCode);

  List<Item> findItemByNameLikeIgnoreCase(String name, Pageable pageable);
}
