package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.BuyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyItemRepository extends JpaRepository<BuyItem, Long> {
}
