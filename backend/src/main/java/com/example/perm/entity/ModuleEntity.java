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
@Table(name = "sys_module", uniqueConstraints = {
        @UniqueConstraint(name = "uk_module_permKey", columnNames = {"perm_key"})
})
public class ModuleEntity extends BaseEntity {
    @Column(name = "cn_name", nullable = false, length = 64)
    private String cnName;

    @Column(name = "en_name", nullable = false, length = 64)
    private String enName;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo = 0;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "group_name", length = 64)
    private String groupName;

    @Column(name = "perm_key", nullable = false, length = 128)
    private String permKey;

    @Column(name = "path", length = 256)
    private String path;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private ModuleEntity parent;

    @Column(name = "is_parent", nullable = false)
    private Boolean isParent = false;

    @Column(name = "expanded", nullable = false)
    private Boolean expanded = false;

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPermKey() {
        return permKey;
    }

    public void setPermKey(String permKey) {
        this.permKey = permKey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ModuleEntity getParent() {
        return parent;
    }

    public void setParent(ModuleEntity parent) {
        this.parent = parent;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean parent) {
        isParent = parent;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }
}
