package com.example.perm.service;

import com.example.perm.dto.AuthDtos;
import com.example.perm.entity.ModuleEntity;
import com.example.perm.repo.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ModuleTreeService {
    private final ModuleRepository moduleRepository;

    public ModuleTreeService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<AuthDtos.ModuleNode> buildTree(List<ModuleEntity> all) {
        var nodes = new HashMap<String, AuthDtos.ModuleNode>();
        var childrenMap = new HashMap<String, List<AuthDtos.ModuleNode>>();

        for (var m : all) {
            var parentId = m.getParent() == null ? null : m.getParent().getId();
            var node = new AuthDtos.ModuleNode(
                    m.getId(),
                    m.getCnName(),
                    m.getEnName(),
                    m.getLevel(),
                    m.getOrderNo(),
                    m.getIcon(),
                    m.getGroupName(),
                    m.getPermKey(),
                    m.getPath(),
                    parentId,
                    Boolean.TRUE.equals(m.getIsParent()),
                    Boolean.TRUE.equals(m.getExpanded()),
                    new ArrayList<>()
            );
            nodes.put(node.id(), node);
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }

        for (var list : childrenMap.values()) {
            list.sort((a, b) -> {
                int byLevel = Integer.compare(a.level(), b.level());
                if (byLevel != 0) {
                    return byLevel;
                }
                return Integer.compare(a.orderNo(), b.orderNo());
            });
        }

        for (var entry : childrenMap.entrySet()) {
            var parentId = entry.getKey();
            if (parentId == null) {
                continue;
            }
            var parent = nodes.get(parentId);
            if (parent != null) {
                parent.children().addAll(entry.getValue());
            }
        }

        return childrenMap.getOrDefault(null, List.of());
    }

    public List<AuthDtos.ModuleNode> getAllModuleTree() {
        return buildTree(moduleRepository.findAllByOrderByLevelAscOrderNoAsc());
    }
}
