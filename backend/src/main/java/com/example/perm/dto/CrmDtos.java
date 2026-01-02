package com.example.perm.dto;

public class CrmDtos {
    public static class CustomerUpsertRequest {
        public String id;
        public String code;
        public String name;
        public String companyAddress;
        public String invoicePhone;
        public String invoiceAddress;
        public String invoiceBank;
        public String invoiceTaxNo;
        public String areaOptionId;
        public String industryOptionId;
        public String buyerAttrOptionId;
        public String creditLevel;
        public Long debtAmount;
    }

    public static class ContactUpsertRequest {
        public String id;
        public String customerId;
        public String name;
        public String code;
        public String nickname;
        public String gender;
        public String birthday;
        public String postOptionId;
        public String dutyOptionId;
        public String phone;
        public Boolean primary;
        public String supervisor;
        public String maritalOptionId;
        public String hobby;
        public String remark;
    }
}
