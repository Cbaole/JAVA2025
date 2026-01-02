package com.example.perm.repo;

import com.example.perm.entity.CustomerAfterSaleEntity;
import com.example.perm.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerAfterSaleRepository extends JpaRepository<CustomerAfterSaleEntity, String> {
    List<CustomerAfterSaleEntity> findByCustomerOrderByCreateTimeDesc(CustomerEntity customer);
}
