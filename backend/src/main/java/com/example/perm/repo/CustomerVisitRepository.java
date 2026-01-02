package com.example.perm.repo;

import com.example.perm.entity.CustomerEntity;
import com.example.perm.entity.CustomerVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerVisitRepository extends JpaRepository<CustomerVisitEntity, String> {
    List<CustomerVisitEntity> findByCustomerOrderByCreateTimeDesc(CustomerEntity customer);
}
