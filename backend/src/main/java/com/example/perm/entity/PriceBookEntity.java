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
@Table(name = "crm_price_book", uniqueConstraints = {
        @UniqueConstraint(name = "uk_price_dept_type_product", columnNames = {"dept_option_id", "product_type", "product_id"})
})
public class PriceBookEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_option_id", nullable = false)
    private OptionEntity deptOption;

    @Column(name = "product_type", nullable = false, length = 16)
    private String productType;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price", length = 64)
    private String price;

    @Column(name = "remark", length = 512)
    private String remark;

    public OptionEntity getDeptOption() { return deptOption; }
    public void setDeptOption(OptionEntity deptOption) { this.deptOption = deptOption; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}

