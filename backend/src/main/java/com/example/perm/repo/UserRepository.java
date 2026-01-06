package com.example.perm.repo;

import com.example.perm.common.UserStatus;
import com.example.perm.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByIdCard(String idCard);
    Optional<UserEntity> findByPhone(String phone);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByIdCard(String idCard);
    boolean existsByPhone(String phone);
    List<UserEntity> findByStatusOrderByCreateTimeDesc(UserStatus status);
    List<UserEntity> findAllByOrderByCreateTimeDesc();
}
