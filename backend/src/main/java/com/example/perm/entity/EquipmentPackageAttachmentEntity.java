package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_equipment_package_attachment")
public class EquipmentPackageAttachmentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    private EquipmentPackageEntity equipmentPackage;

    @Column(name = "file_name", nullable = false, length = 256)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    public EquipmentPackageEntity getEquipmentPackage() { return equipmentPackage; }
    public void setEquipmentPackage(EquipmentPackageEntity equipmentPackage) { this.equipmentPackage = equipmentPackage; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
