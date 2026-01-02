package com.example.perm.repo;

import com.example.perm.entity.EquipmentPackageAttachmentEntity;
import com.example.perm.entity.EquipmentPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentPackageAttachmentRepository extends JpaRepository<EquipmentPackageAttachmentEntity, String> {
    List<EquipmentPackageAttachmentEntity> findByEquipmentPackageOrderByCreateTimeDesc(EquipmentPackageEntity equipmentPackage);
}
