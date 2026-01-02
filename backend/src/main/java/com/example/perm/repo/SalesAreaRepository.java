package com.example.perm.repo;

import com.example.perm.entity.SalesAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesAreaRepository extends JpaRepository<SalesAreaEntity, String> {
    boolean existsByCode(String code);
}
