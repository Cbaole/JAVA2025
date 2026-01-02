package com.example.perm.repo;

import com.example.perm.entity.SparePartAttachmentEntity;
import com.example.perm.entity.SparePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SparePartAttachmentRepository extends JpaRepository<SparePartAttachmentEntity, String> {
    List<SparePartAttachmentEntity> findBySparePartOrderByCreateTimeDesc(SparePartEntity sparePart);
}
