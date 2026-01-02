package com.example.perm.repo;

import com.example.perm.entity.CustomerContactEntity;
import com.example.perm.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerContactRepository extends JpaRepository<CustomerContactEntity, String> {
    List<CustomerContactEntity> findByCustomerOrderByCreateTimeAsc(CustomerEntity customer);
}
