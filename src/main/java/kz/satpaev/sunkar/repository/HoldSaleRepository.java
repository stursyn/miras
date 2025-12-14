package kz.satpaev.sunkar.repository;

import jakarta.transaction.Transactional;
import kz.satpaev.sunkar.model.entity.HoldSale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HoldSaleRepository extends JpaRepository<HoldSale, Long> {
  Page<HoldSale> findAllBySaleTimeBetween(LocalDateTime saleTimeAfter, LocalDateTime saleTimeBefore, Pageable pageable);
  @Transactional
  void removeById(Long id);
}
