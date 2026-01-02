package com.example.perm.repo;

import com.example.perm.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
    Optional<CustomerEntity> findByName(String name);
}
