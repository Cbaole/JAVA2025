package com.example.perm.entity;

import com.example.perm.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "sys_option", uniqueConstraints = {
        @UniqueConstraint(name = "uk_option_group_value", columnNames = {"group_key", "option_value"})
})
public class OptionEntity extends BaseEntity {
    @Column(name = "group_key", nullable = false, length = 64)
    private String groupKey;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "option_value", nullable = false, length = 128)
    private String value;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo = 0;

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}
