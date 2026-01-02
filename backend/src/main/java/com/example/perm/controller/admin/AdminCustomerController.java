package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.common.Gender;
import com.example.perm.dto.CrmDtos;
import com.example.perm.entity.CustomerContactEntity;
import com.example.perm.entity.CustomerEntity;
import com.example.perm.entity.CustomerAfterSaleEntity;
import com.example.perm.entity.CustomerContractEntity;
import com.example.perm.entity.CustomerVisitEntity;
import com.example.perm.repo.CustomerAfterSaleRepository;
import com.example.perm.repo.CustomerContractRepository;
import com.example.perm.repo.CustomerContactRepository;
import com.example.perm.repo.CustomerVisitRepository;
import com.example.perm.repo.CustomerRepository;
import com.example.perm.repo.OptionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/crm/customers")
public class AdminCustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerContactRepository contactRepository;
    private final OptionRepository optionRepository;
    private final CustomerContractRepository contractRepository;
    private final CustomerAfterSaleRepository afterSaleRepository;
    private final CustomerVisitRepository visitRepository;

    public AdminCustomerController(CustomerRepository customerRepository, CustomerContactRepository contactRepository, OptionRepository optionRepository, CustomerContractRepository contractRepository, CustomerAfterSaleRepository afterSaleRepository, CustomerVisitRepository visitRepository) {
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.optionRepository = optionRepository;
        this.contractRepository = contractRepository;
        this.afterSaleRepository = afterSaleRepository;
        this.visitRepository = visitRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'crm/customer','see')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(customerRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has(authentication,'crm/customer','read')")
    public ApiResponse<Object> detail(@PathVariable String id) {
        var customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        var contacts = contactRepository.findByCustomerOrderByCreateTimeAsc(customer);
        return ApiResponse.ok(new Object() {
            public final CustomerEntity base = customer;
            public final java.util.List<CustomerContactEntity> contactList = contacts;
            public final java.util.List<CustomerContractEntity> contractList = contractRepository.findByCustomerOrderByCreateTimeDesc(customer);
            public final java.util.List<CustomerAfterSaleEntity> afterSaleList = afterSaleRepository.findByCustomerOrderByCreateTimeDesc(customer);
            public final java.util.List<CustomerVisitEntity> visitList = visitRepository.findByCustomerOrderByCreateTimeDesc(customer);
        });
    }

    @PostMapping("/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','add') or @perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> upsert(@RequestBody CrmDtos.CustomerUpsertRequest req) {
        if (req.id == null || req.id.isBlank()) {
            if (req.name == null || req.name.isBlank()) throw new IllegalArgumentException("客户名称不能为空");
            if (customerRepository.existsByName(req.name)) throw new IllegalArgumentException("客户名称已存在");
        }
        CustomerEntity entity = req.id == null || req.id.isBlank()
                ? new CustomerEntity()
                : customerRepository.findById(req.id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        entity.setCode(req.code);
        entity.setName(req.name);
        entity.setCompanyAddress(req.companyAddress);
        entity.setInvoicePhone(req.invoicePhone);
        entity.setInvoiceAddress(req.invoiceAddress);
        entity.setInvoiceBank(req.invoiceBank);
        entity.setInvoiceTaxNo(req.invoiceTaxNo);
        entity.setCreditLevel(req.creditLevel);
        entity.setDebtAmount(req.debtAmount);
        if (req.areaOptionId != null && !req.areaOptionId.isBlank()) {
            entity.setAreaOption(optionRepository.findById(req.areaOptionId).orElse(null));
        } else {
            entity.setAreaOption(null);
        }
        if (req.industryOptionId != null && !req.industryOptionId.isBlank()) {
            entity.setIndustryOption(optionRepository.findById(req.industryOptionId).orElse(null));
        } else {
            entity.setIndustryOption(null);
        }
        if (req.buyerAttrOptionId != null && !req.buyerAttrOptionId.isBlank()) {
            entity.setBuyerAttrOption(optionRepository.findById(req.buyerAttrOptionId).orElse(null));
        } else {
            entity.setBuyerAttrOption(null);
        }
        customerRepository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/{id}/contract/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','add') or @perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> contractUpsert(@PathVariable String id, @RequestBody java.util.Map<String, Object> req) {
        var customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        var entity = (req.get("id") == null || ((String) req.get("id")).isBlank())
                ? new CustomerContractEntity()
                : contractRepository.findById((String) req.get("id")).orElseThrow(() -> new IllegalArgumentException("合同不存在"));
        entity.setCustomer(customer);
        entity.setContractName((String) req.get("contractName"));
        entity.setContractCode((String) req.get("contractCode"));
        var areaId = (String) req.get("areaOptionId");
        entity.setAreaOption(areaId != null && !areaId.isBlank() ? optionRepository.findById(areaId).orElse(null) : null);
        var dateStr = (String) req.get("signDate");
        entity.setSignDate(dateStr != null && !dateStr.isBlank() ? java.time.LocalDate.parse(dateStr) : null);
        entity.setRemark((String) req.get("remark"));
        contractRepository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/contracts/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> contractDelete(@PathVariable String id) {
        contractRepository.deleteById(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/aftersale/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','add') or @perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> afterSaleUpsert(@PathVariable String id, @RequestBody java.util.Map<String, Object> req) {
        var customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        var entity = (req.get("id") == null || ((String) req.get("id")).isBlank())
                ? new CustomerAfterSaleEntity()
                : afterSaleRepository.findById((String) req.get("id")).orElseThrow(() -> new IllegalArgumentException("售后记录不存在"));
        entity.setCustomer(customer);
        entity.setContractCode((String) req.get("contractCode"));
        entity.setContractName((String) req.get("contractName"));
        var areaId = (String) req.get("areaOptionId");
        entity.setAreaOption(areaId != null && !areaId.isBlank() ? optionRepository.findById(areaId).orElse(null) : null);
        entity.setStaffName((String) req.get("staffName"));
        entity.setRemark((String) req.get("remark"));
        afterSaleRepository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/aftersales/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> afterSaleDelete(@PathVariable String id) {
        afterSaleRepository.deleteById(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/visit/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','add') or @perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> visitUpsert(@PathVariable String id, @RequestBody java.util.Map<String, Object> req) {
        var customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        var entity = (req.get("id") == null || ((String) req.get("id")).isBlank())
                ? new CustomerVisitEntity()
                : visitRepository.findById((String) req.get("id")).orElseThrow(() -> new IllegalArgumentException("来访记录不存在"));
        entity.setCustomer(customer);
        entity.setCustomerCode((String) req.get("customerCode"));
        entity.setCustomerName((String) req.get("customerName"));
        entity.setStatus((String) req.get("status"));
        var dateStr = (String) req.get("visitDate");
        entity.setVisitDate(dateStr != null && !dateStr.isBlank() ? java.time.LocalDate.parse(dateStr) : null);
        entity.setRemark((String) req.get("remark"));
        visitRepository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/visits/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> visitDelete(@PathVariable String id) {
        visitRepository.deleteById(id);
        return ApiResponse.ok();
    }
    @PostMapping("/{id}/contact/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','add') or @perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> contactUpsert(@PathVariable String id, @RequestBody CrmDtos.ContactUpsertRequest req) {
        var customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        CustomerContactEntity entity = req.id == null || req.id.isBlank()
                ? new CustomerContactEntity()
                : contactRepository.findById(req.id).orElseThrow(() -> new IllegalArgumentException("联系人不存在"));
        entity.setCustomer(customer);
        entity.setName(req.name);
        entity.setCode(req.code);
        entity.setNickname(req.nickname);
        if (req.gender != null && !req.gender.isBlank()) {
            try { entity.setGender(Gender.valueOf(req.gender)); } catch (Exception ignored) {}
        }
        if (req.birthday != null && !req.birthday.isBlank()) {
            entity.setBirthday(LocalDate.parse(req.birthday));
        } else {
            entity.setBirthday(null);
        }
        if (req.postOptionId != null && !req.postOptionId.isBlank()) {
            entity.setPostOption(optionRepository.findById(req.postOptionId).orElse(null));
        } else {
            entity.setPostOption(null);
        }
        if (req.dutyOptionId != null && !req.dutyOptionId.isBlank()) {
            entity.setDutyOption(optionRepository.findById(req.dutyOptionId).orElse(null));
        } else {
            entity.setDutyOption(null);
        }
        entity.setPhone(req.phone);
        entity.setPrimary(Boolean.TRUE.equals(req.primary));
        entity.setSupervisor(req.supervisor);
        if (req.maritalOptionId != null && !req.maritalOptionId.isBlank()) {
            entity.setMaritalOption(optionRepository.findById(req.maritalOptionId).orElse(null));
        } else {
            entity.setMaritalOption(null);
        }
        entity.setHobby(req.hobby);
        entity.setRemark(req.remark);
        contactRepository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        customerRepository.deleteById(id);
        return ApiResponse.ok();
    }

    @PostMapping("/contacts/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/customer','update')")
    public ApiResponse<Object> deleteContact(@PathVariable String id) {
        contactRepository.deleteById(id);
        return ApiResponse.ok();
    }
}
