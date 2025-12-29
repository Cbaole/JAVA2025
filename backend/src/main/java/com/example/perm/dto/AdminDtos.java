package com.example.perm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AdminDtos {
    public record OptionUpsertRequest(@NotBlank String groupKey, @NotBlank String title, @NotBlank String value, @NotNull Integer orderNo) {
    }

    public record RoleUpsertRequest(@NotBlank String roleName, @NotBlank String roleDesc) {
    }

    public record ModuleUpsertRequest(@NotBlank String cnName,
                                      @NotBlank String enName,
                                      @NotNull Integer level,
                                      @NotNull Integer orderNo,
                                      String icon,
                                      String groupName,
                                      @NotBlank String permKey,
                                      String path,
                                      String parentId,
                                      @NotNull Boolean isParent,
                                      @NotNull Boolean expanded) {
    }

    public record RolePermItem(@NotBlank String moduleId, boolean canRead, boolean canAdd, boolean canUpdate, boolean canSee) {
    }

    public record RolePermBatchRequest(@NotBlank String roleId, @NotNull List<RolePermItem> items) {
    }
}

