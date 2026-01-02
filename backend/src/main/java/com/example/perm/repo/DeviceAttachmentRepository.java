package com.example.perm.repo;

import com.example.perm.entity.DeviceAttachmentEntity;
import com.example.perm.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceAttachmentRepository extends JpaRepository<DeviceAttachmentEntity, String> {
    List<DeviceAttachmentEntity> findByDeviceOrderByCreateTimeDesc(DeviceEntity device);
}
