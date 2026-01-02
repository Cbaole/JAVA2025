package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_spare_part")
public class SparePartEntity extends BaseEntity {
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "model", length = 128)
    private String model;

    @Column(name = "params", length = 1024)
    private String params;

    @Column(name = "price", length = 64)
    private String price;

    @Column(name = "weight", length = 64)
    private String weight;

    @Column(name = "lead_time", length = 64)
    private String leadTime;

    @Column(name = "remark", length = 512)
    private String remark;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    public String getLeadTime() { return leadTime; }
    public void setLeadTime(String leadTime) { this.leadTime = leadTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
