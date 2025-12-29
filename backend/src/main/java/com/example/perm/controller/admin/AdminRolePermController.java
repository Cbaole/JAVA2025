package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AdminDtos;
import com.example.perm.repo.ModuleRepository;
import com.example.perm.repo.RoleModulePermRepository;
import com.example.perm.repo.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/admin/role-perms")
public class AdminRolePermController {
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final RoleModulePermRepository roleModulePermRepository;

    public AdminRolePermController(RoleRepository roleRepository,
                                   ModuleRepository moduleRepository,
                                   RoleModulePermRepository roleModulePermRepository) {
        this.roleRepository = roleRepository;
        this.moduleRepository = moduleRepository;
        this.roleModulePermRepository = roleModulePermRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'au/perm','read')")
    public ApiResponse<Object> get(@RequestParam String roleId) {
        var perms = roleModulePermRepository.findByRole_Id(roleId);
        var map = new LinkedHashMap<String, Object>();
        for (var p : perms) {
            map.put(p.getModule().getId(), new AdminDtos.RolePermItem(p.getModule().getId(), p.isCanRead(), p.isCanAdd(), p.isCanUpdate(), p.isCanSee()));
        }
        return ApiResponse.ok(map);
    }

    @PostMapping("/batch")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/perm','update')")
    public ApiResponse<Object> save(@Valid @RequestBody AdminDtos.RolePermBatchRequest req) {
        var role = roleRepository.findById(req.roleId()).orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        for (var item : req.items()) {
            var module = moduleRepository.findById(item.moduleId()).orElseThrow(() -> new IllegalArgumentException("模块不存在"));
            var record = roleModulePermRepository.findByRole_IdAndModule_Id(role.getId(), module.getId())
                    .orElseGet(() -> {
                        var r = new com.example.perm.entity.RoleModulePermEntity();
                        r.setRole(role);
                        r.setModule(module);
                        return r;
                    });
            record.setCanRead(item.canRead());
            record.setCanAdd(item.canAdd());
            record.setCanUpdate(item.canUpdate());
            record.setCanSee(item.canSee());
            roleModulePermRepository.save(record);
        }
        return ApiResponse.ok();
    }
}

