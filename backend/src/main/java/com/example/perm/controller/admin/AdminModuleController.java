package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AdminDtos;
import com.example.perm.entity.ModuleEntity;
import com.example.perm.repo.ModuleRepository;
import com.example.perm.service.ModuleTreeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/modules")
public class AdminModuleController {
    private final ModuleRepository moduleRepository;
    private final ModuleTreeService moduleTreeService;

    public AdminModuleController(ModuleRepository moduleRepository, ModuleTreeService moduleTreeService) {
        this.moduleRepository = moduleRepository;
        this.moduleTreeService = moduleTreeService;
    }

    @GetMapping("/tree")
    @PreAuthorize("@perm.has(authentication,'au/module','read')")
    public ApiResponse<Object> tree() {
        return ApiResponse.ok(moduleTreeService.getAllModuleTree());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/module','add')")
    public ApiResponse<Object> create(@Valid @RequestBody AdminDtos.ModuleUpsertRequest req) {
        if (moduleRepository.existsByPermKey(req.permKey())) {
            throw new IllegalArgumentException("权限标识不能重复");
        }
        var m = new ModuleEntity();
        apply(m, req);
        return ApiResponse.ok(moduleRepository.save(m));
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/module','update')")
    public ApiResponse<Object> update(@PathVariable String id, @Valid @RequestBody AdminDtos.ModuleUpsertRequest req) {
        var m = moduleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("模块不存在"));
        if (!m.getPermKey().equals(req.permKey()) && moduleRepository.existsByPermKey(req.permKey())) {
            throw new IllegalArgumentException("权限标识不能重复");
        }
        apply(m, req);
        return ApiResponse.ok(m);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/module','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        if (moduleRepository.countByParent_Id(id) > 0) {
            throw new IllegalArgumentException("请先删除子模块");
        }
        moduleRepository.deleteById(id);
        return ApiResponse.ok();
    }

    private void apply(ModuleEntity m, AdminDtos.ModuleUpsertRequest req) {
        m.setCnName(req.cnName());
        m.setEnName(req.enName());
        m.setLevel(req.level());
        m.setOrderNo(req.orderNo());
        m.setIcon(req.icon());
        m.setGroupName(req.groupName());
        m.setPermKey(req.permKey());
        m.setPath(req.path());
        m.setIsParent(req.isParent());
        m.setExpanded(req.expanded());
        if (req.parentId() == null || req.parentId().isBlank()) {
            m.setParent(null);
        } else {
            m.setParent(moduleRepository.findById(req.parentId()).orElseThrow(() -> new IllegalArgumentException("父模块不存在")));
        }
    }
}

