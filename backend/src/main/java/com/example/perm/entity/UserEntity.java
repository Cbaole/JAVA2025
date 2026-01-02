package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import com.example.perm.common.Gender;
import com.example.perm.common.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "sys_user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_idCard", columnNames = {"id_card"}),
        @UniqueConstraint(name = "uk_user_phone", columnNames = {"phone"}),
        @UniqueConstraint(name = "uk_user_username", columnNames = {"username"})
})
public class UserEntity extends BaseEntity {
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "id_card", nullable = false, length = 18)
    private String idCard;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 16)
    private Gender gender = Gender.UNKNOWN;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_option_id")
    private OptionEntity postOption;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_option_id")
    private OptionEntity areaOption;

    @Column(name = "staff_no", length = 32)
    private String staffNo;

    @Column(name = "remark", length = 512)
    private String remark;

    @Column(name = "username", length = 64)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 128)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus status = UserStatus.PENDING;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public OptionEntity getPostOption() {
        return postOption;
    }

    public void setPostOption(OptionEntity postOption) {
        this.postOption = postOption;
    }

    public OptionEntity getAreaOption() {
        return areaOption;
    }

    public void setAreaOption(OptionEntity areaOption) {
        this.areaOption = areaOption;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
