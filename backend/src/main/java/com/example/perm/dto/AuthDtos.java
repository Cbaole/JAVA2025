package com.example.perm.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String token, UserProfile profile, List<ModuleNode> menu, Map<String, RoleModulePerm> perms) {
    }

    public record UserProfile(String id, String name, String phone, String username, String roleId, String roleName) {
    }

    public record ModuleNode(String id, String cnName, String enName, Integer level, Integer orderNo, String icon, String groupName,
                             String permKey, String path, String parentId, boolean isParent, boolean expanded, List<ModuleNode> children) {
    }

    public record RoleModulePerm(String moduleId, boolean canRead, boolean canAdd, boolean canUpdate, boolean canSee) {
    }

    public record RegisterCheckResponse(boolean registered, String name, String username) {
    }

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank String idCard,
                                  @NotBlank String phone,
                                  LocalDate birthday,
                                  String gender,
                                  String postOptionId,
                                  String areaOptionId) {
    }
}
