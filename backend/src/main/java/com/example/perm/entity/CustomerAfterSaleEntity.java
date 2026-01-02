package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_customer_aftersale")
public class CustomerAfterSaleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "contract_code", length = 64)
    private String contractCode;

    @Column(name = "contract_name", length = 128)
    private String contractName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_option_id")
    private OptionEntity areaOption;

    @Column(name = "staff_name", length = 64)
    private String staffName;

    @Column(name = "remark", length = 512)
    private String remark;

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }
    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }
    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }
    public OptionEntity getAreaOption() { return areaOption; }
    public void setAreaOption(OptionEntity areaOption) { this.areaOption = areaOption; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
