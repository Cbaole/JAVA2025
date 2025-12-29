package com.example.perm.repo;

import com.example.perm.entity.RoleModulePermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleModulePermRepository extends JpaRepository<RoleModulePermEntity, String> {
    Optional<RoleModulePermEntity> findByRole_IdAndModule_Id(String roleId, String moduleId);
    List<RoleModulePermEntity> findByRole_Id(String roleId);
    Optional<RoleModulePermEntity> findByRole_IdAndModule_PermKey(String roleId, String permKey);
}

