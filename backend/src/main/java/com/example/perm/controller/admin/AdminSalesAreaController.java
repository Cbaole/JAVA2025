package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.entity.SalesAreaEntity;
import com.example.perm.repo.OptionRepository;
import com.example.perm.repo.SalesAreaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/crm/sales-areas")
public class AdminSalesAreaController {
    private final SalesAreaRepository repository;
    private final OptionRepository optionRepository;

    public AdminSalesAreaController(SalesAreaRepository repository, OptionRepository optionRepository) {
        this.repository = repository;
        this.optionRepository = optionRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'crm/area','see')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has(authentication,'crm/area','read')")
    public ApiResponse<Object> detail(@PathVariable String id) {
        return ApiResponse.ok(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("片区不存在")));
    }

    @PostMapping("/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/area','add') or @perm.has(authentication,'crm/area','update')")
    public ApiResponse<Object> upsert(@RequestBody java.util.Map<String, Object> req) {
        SalesAreaEntity entity = (req.get("id") == null || ((String) req.get("id")).isBlank())
                ? new SalesAreaEntity()
                : repository.findById((String) req.get("id")).orElseThrow(() -> new IllegalArgumentException("片区不存在"));
        if (entity.getId() == null) {
            var code = (String) req.get("code");
            if (code == null || code.isBlank()) throw new IllegalArgumentException("片区编号不能为空");
            if (repository.existsByCode(code)) throw new IllegalArgumentException("片区编号已存在");
        }
        entity.setCode((String) req.get("code"));
        entity.setName((String) req.get("name"));
        var deptId = (String) req.get("deptOptionId");
        entity.setDeptOption(deptId != null && !deptId.isBlank() ? optionRepository.findById(deptId).orElse(null) : null);
        entity.setRemark((String) req.get("remark"));
        repository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/area','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ApiResponse.ok();
    }
}
