package com.example.perm.repo;

import com.example.perm.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<ModuleEntity, String> {
    List<ModuleEntity> findAllByOrderByLevelAscOrderNoAsc();
    Optional<ModuleEntity> findByPermKey(String permKey);
    boolean existsByPermKey(String permKey);
    List<ModuleEntity> findByParent_IdOrderByOrderNoAsc(String parentId);
    long countByParent_Id(String parentId);
}

