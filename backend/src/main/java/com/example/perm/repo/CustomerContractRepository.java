package com.example.perm.repo;

import com.example.perm.entity.CustomerContractEntity;
import com.example.perm.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerContractRepository extends JpaRepository<CustomerContractEntity, String> {
    List<CustomerContractEntity> findByCustomerOrderByCreateTimeDesc(CustomerEntity customer);
}
