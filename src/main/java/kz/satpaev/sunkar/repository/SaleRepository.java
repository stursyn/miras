package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.Sale;
import kz.satpaev.sunkar.model.projection.SaleSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
  Page<Sale> findAllBySaleTimeBetween(LocalDateTime saleTimeAfter, LocalDateTime saleTimeBefore, Pageable pageable);

  @Query("select paymentType as paymentType, count(1) as totalCount, sum(amount) as totalAmount, " +
      " sum(kaspiAmount) as kaspiAmount, sum(cashAmount) as cashAmount, sum(halykAmount) as halykAmount, " +
      " sum(dutyAmount) as dutyAmount " +
      "  from Sale where saleTime between :saleTimeAfter and  :saleTimeBefore group by paymentType")
  List<SaleSummaryProjection> saleSummaryByPaymentType(LocalDateTime saleTimeAfter, LocalDateTime saleTimeBefore);
}
