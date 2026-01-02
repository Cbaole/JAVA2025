package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "crm_sales_area", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sales_area_code", columnNames = {"code"})
})
public class SalesAreaEntity extends BaseEntity {
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_option_id")
    private OptionEntity deptOption;

    @Column(name = "remark", length = 512)
    private String remark;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public OptionEntity getDeptOption() { return deptOption; }
    public void setDeptOption(OptionEntity deptOption) { this.deptOption = deptOption; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
