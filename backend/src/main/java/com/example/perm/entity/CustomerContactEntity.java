package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import com.example.perm.common.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "crm_customer_contact")
public class CustomerContactEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "code", length = 32)
    private String code;

    @Column(name = "nickname", length = 64)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 16)
    private Gender gender = Gender.UNKNOWN;

    @Column(name = "birthday")
    private LocalDate birthday;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_option_id")
    private OptionEntity postOption;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "duty_option_id")
    private OptionEntity dutyOption;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "supervisor", length = 64)
    private String supervisor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marital_option_id")
    private OptionEntity maritalOption;

    @Column(name = "hobby", length = 256)
    private String hobby;

    @Column(name = "remark", length = 512)
    private String remark;

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public OptionEntity getPostOption() {
        return postOption;
    }

    public void setPostOption(OptionEntity postOption) {
        this.postOption = postOption;
    }

    public OptionEntity getDutyOption() {
        return dutyOption;
    }

    public void setDutyOption(OptionEntity dutyOption) {
        this.dutyOption = dutyOption;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public OptionEntity getMaritalOption() {
        return maritalOption;
    }

    public void setMaritalOption(OptionEntity maritalOption) {
        this.maritalOption = maritalOption;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
