package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
  Page<Sale> findAllBySaleTimeBetween(LocalDateTime saleTimeAfter, LocalDateTime saleTimeBefore, Pageable pageable);
  int countAllBySaleTimeBetween(LocalDateTime saleTimeAfter, LocalDateTime saleTimeBefore);
}
