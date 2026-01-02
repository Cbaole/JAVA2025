package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "crm_customer_contract")
public class CustomerContractEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "contract_name", nullable = false, length = 128)
    private String contractName;

    @Column(name = "contract_code", nullable = false, length = 64)
    private String contractCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_option_id")
    private OptionEntity areaOption;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @Column(name = "remark", length = 512)
    private String remark;

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }
    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }
    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }
    public OptionEntity getAreaOption() { return areaOption; }
    public void setAreaOption(OptionEntity areaOption) { this.areaOption = areaOption; }
    public LocalDate getSignDate() { return signDate; }
    public void setSignDate(LocalDate signDate) { this.signDate = signDate; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
