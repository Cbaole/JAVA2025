package com.example.perm.repo;

import com.example.perm.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {
}
