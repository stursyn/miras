package kz.satpaev.sunkar.repository;

import kz.satpaev.sunkar.model.entity.SubItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubItemRepository extends JpaRepository<SubItem, String> {
  SubItem findItemByCode(String code);

  @Query(value = "SELECT nextval('sub_item_seq')", nativeQuery = true)
  Long subItemSeqNextVal();
}
