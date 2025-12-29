package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "sys_role", uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_name", columnNames = {"role_name"})
})
public class RoleEntity extends BaseEntity {
    @Column(name = "role_name", nullable = false, length = 64)
    private String roleName;

    @Column(name = "role_desc", nullable = false, length = 256)
    private String roleDesc;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
}
