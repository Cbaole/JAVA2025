package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_device_attachment")
public class DeviceAttachmentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceEntity device;
    @Column(name = "file_name", nullable = false, length = 256)
    private String fileName;
    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;
    public DeviceEntity getDevice() { return device; }
    public void setDevice(DeviceEntity device) { this.device = device; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
