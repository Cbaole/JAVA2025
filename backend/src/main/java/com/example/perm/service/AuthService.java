package com.example.perm.service;

import com.example.perm.common.UserStatus;
import com.example.perm.dto.AuthDtos;
import com.example.perm.entity.ModuleEntity;
import com.example.perm.repo.RoleModulePermRepository;
import com.example.perm.repo.UserRepository;
import com.example.perm.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleModulePermRepository roleModulePermRepository;
    private final ModuleTreeService moduleTreeService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RoleModulePermRepository roleModulePermRepository,
                       ModuleTreeService moduleTreeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleModulePermRepository = roleModulePermRepository;
        this.moduleTreeService = moduleTreeService;
    }

    public AuthDtos.LoginResponse login(String username, String password) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("账号或密码错误"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账号未启用");
        }
        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("账号未分配角色");
        }

        var token = jwtService.issueToken(user.getId(), user.getUsername(), user.getRole().getId(), user.getRole().getRoleName());

        var profile = new AuthDtos.UserProfile(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getUsername(),
                user.getRole().getId(),
                user.getRole().getRoleName()
        );

        var permMap = new LinkedHashMap<String, AuthDtos.RoleModulePerm>();
        var rolePerms = roleModulePermRepository.findByRole_Id(user.getRole().getId());
        for (var rp : rolePerms) {
            permMap.put(rp.getModule().getPermKey(), new AuthDtos.RoleModulePerm(
                    rp.getModule().getId(),
                    rp.isCanRead(),
                    rp.isCanAdd(),
                    rp.isCanUpdate(),
                    rp.isCanSee()
            ));
        }

        var menuSet = new LinkedHashSet<ModuleEntity>();
        for (var rp : rolePerms) {
            if (!rp.isCanSee()) {
                continue;
            }
            var m = rp.getModule();
            while (m != null) {
                menuSet.add(m);
                m = m.getParent();
            }
        }
        var menuTree = moduleTreeService.buildTree(menuSet.stream().toList());

        return new AuthDtos.LoginResponse(token, profile, menuTree, permMap);
    }
}
