package com.example.perm.security;

import com.example.perm.repo.RoleModulePermRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("perm")
public class PermChecker {
    private final RoleModulePermRepository roleModulePermRepository;

    public PermChecker(RoleModulePermRepository roleModulePermRepository) {
        this.roleModulePermRepository = roleModulePermRepository;
    }

    public boolean has(Authentication authentication, String permKey, String action) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof AuthUserPrincipal principal)) {
            return false;
        }
        if ("ADMIN".equalsIgnoreCase(principal.getRoleName())) {
            return true;
        }
        var recordOpt = roleModulePermRepository.findByRole_IdAndModule_PermKey(principal.getRoleId(), permKey);
        if (recordOpt.isEmpty()) {
            return false;
        }
        var record = recordOpt.get();
        return switch (action) {
            case "read" -> record.isCanRead();
            case "add" -> record.isCanAdd();
            case "update" -> record.isCanUpdate();
            case "see" -> record.isCanSee();
            default -> false;
        };
    }
}

