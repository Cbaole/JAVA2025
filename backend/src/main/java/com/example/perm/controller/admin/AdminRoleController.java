package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AdminDtos;
import com.example.perm.entity.RoleEntity;
import com.example.perm.repo.RoleRepository;
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
@RequestMapping("/api/admin/roles")
public class AdminRoleController {
    private final RoleRepository roleRepository;

    public AdminRoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'au/role','read')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(roleRepository.findAll());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/role','add')")
    public ApiResponse<Object> create(@Valid @RequestBody AdminDtos.RoleUpsertRequest req) {
        if (roleRepository.existsByRoleName(req.roleName())) {
            throw new IllegalArgumentException("角色名不能重复");
        }
        var role = new RoleEntity();
        role.setRoleName(req.roleName());
        role.setRoleDesc(req.roleDesc());
        return ApiResponse.ok(roleRepository.save(role));
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/role','update')")
    public ApiResponse<Object> update(@PathVariable String id, @Valid @RequestBody AdminDtos.RoleUpsertRequest req) {
        var role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        if (!role.getRoleName().equals(req.roleName()) && roleRepository.existsByRoleName(req.roleName())) {
            throw new IllegalArgumentException("角色名不能重复");
        }
        role.setRoleName(req.roleName());
        role.setRoleDesc(req.roleDesc());
        return ApiResponse.ok(role);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/role','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        roleRepository.deleteById(id);
        return ApiResponse.ok();
    }
}

