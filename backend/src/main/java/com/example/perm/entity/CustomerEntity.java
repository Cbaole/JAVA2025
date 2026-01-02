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
@Table(name = "crm_customer", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_code", columnNames = {"code"}),
        @UniqueConstraint(name = "uk_customer_name", columnNames = {"name"})
})
public class CustomerEntity extends BaseEntity {
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "company_address", length = 256)
    private String companyAddress;

    @Column(name = "invoice_phone", length = 32)
    private String invoicePhone;

    @Column(name = "invoice_address", length = 256)
    private String invoiceAddress;

    @Column(name = "invoice_bank", length = 128)
    private String invoiceBank;

    @Column(name = "invoice_tax_no", length = 64)
    private String invoiceTaxNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_option_id")
    private OptionEntity areaOption;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "industry_option_id")
    private OptionEntity industryOption;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_attr_option_id")
    private OptionEntity buyerAttrOption;

    @Column(name = "credit_level", length = 32)
    private String creditLevel;

    @Column(name = "debt_amount")
    private Long debtAmount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getInvoicePhone() {
        return invoicePhone;
    }

    public void setInvoicePhone(String invoicePhone) {
        this.invoicePhone = invoicePhone;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getInvoiceBank() {
        return invoiceBank;
    }

    public void setInvoiceBank(String invoiceBank) {
        this.invoiceBank = invoiceBank;
    }

    public String getInvoiceTaxNo() {
        return invoiceTaxNo;
    }

    public void setInvoiceTaxNo(String invoiceTaxNo) {
        this.invoiceTaxNo = invoiceTaxNo;
    }

    public OptionEntity getAreaOption() {
        return areaOption;
    }

    public void setAreaOption(OptionEntity areaOption) {
        this.areaOption = areaOption;
    }

    public OptionEntity getIndustryOption() {
        return industryOption;
    }

    public void setIndustryOption(OptionEntity industryOption) {
        this.industryOption = industryOption;
    }

    public OptionEntity getBuyerAttrOption() {
        return buyerAttrOption;
    }

    public void setBuyerAttrOption(OptionEntity buyerAttrOption) {
        this.buyerAttrOption = buyerAttrOption;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public Long getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(Long debtAmount) {
        this.debtAmount = debtAmount;
    }
}
