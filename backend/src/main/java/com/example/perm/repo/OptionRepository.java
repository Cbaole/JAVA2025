package com.example.perm.repo;

import com.example.perm.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<OptionEntity, String> {
    List<OptionEntity> findByGroupKeyOrderByOrderNoAsc(String groupKey);
}

