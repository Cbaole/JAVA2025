package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.common.UserStatus;
import com.example.perm.repo.RoleRepository;
import com.example.perm.repo.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/pending")
    @PreAuthorize("@perm.has(authentication,'au/user','read')")
    public ApiResponse<Object> pending() {
        return ApiResponse.ok(userRepository.findByStatusOrderByCreateTimeDesc(UserStatus.PENDING));
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'au/user','read')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(userRepository.findAllByOrderByCreateTimeDesc());
    }

    @PostMapping("/{id}/approve")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/user','update')")
    public ApiResponse<Object> approve(@PathVariable String id, @RequestParam(required = false) String roleId) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException("用户状态不允许审核");
        }
        user.setUsername(user.getPhone());
        user.setPassword(passwordEncoder.encode("123456"));
        if (roleId != null && !roleId.isBlank()) {
            user.setRole(roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("角色不存在")));
        } else {
            user.setRole(roleRepository.findByRoleName("USER").orElseThrow(() -> new IllegalArgumentException("默认角色USER不存在")));
        }
        user.setStatus(UserStatus.ACTIVE);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reset-password")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/user','update')")
    public ApiResponse<Object> resetPassword(@PathVariable String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setPassword(passwordEncoder.encode("123456"));
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/set-role")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/user','update')")
    public ApiResponse<Object> setRole(@PathVariable String id, @RequestParam @NotBlank String roleId) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setRole(roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("角色不存在")));
        return ApiResponse.ok();
    }
}
