package com.example.perm.repo;

import com.example.perm.entity.OptionEntity;
import com.example.perm.entity.PriceBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceBookRepository extends JpaRepository<PriceBookEntity, String> {
    List<PriceBookEntity> findByProductTypeOrderByCreateTimeDesc(String productType);
    Optional<PriceBookEntity> findByDeptOptionAndProductTypeAndProductId(OptionEntity deptOption, String productType, String productId);
}

