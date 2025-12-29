package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.dto.AdminDtos;
import com.example.perm.entity.OptionEntity;
import com.example.perm.repo.OptionRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/options")
public class AdminOptionController {
    private final OptionRepository optionRepository;

    public AdminOptionController(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'au/option','read')")
    public ApiResponse<Object> list(@RequestParam(required = false) String groupKey) {
        if (groupKey == null || groupKey.isBlank()) {
            return ApiResponse.ok(optionRepository.findAll());
        }
        return ApiResponse.ok(optionRepository.findByGroupKeyOrderByOrderNoAsc(groupKey));
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/option','add')")
    public ApiResponse<Object> create(@Valid @RequestBody AdminDtos.OptionUpsertRequest req) {
        var opt = new OptionEntity();
        opt.setGroupKey(req.groupKey());
        opt.setTitle(req.title());
        opt.setValue(req.value());
        opt.setOrderNo(req.orderNo());
        return ApiResponse.ok(optionRepository.save(opt));
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/option','update')")
    public ApiResponse<Object> update(@PathVariable String id, @Valid @RequestBody AdminDtos.OptionUpsertRequest req) {
        var opt = optionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("选项不存在"));
        opt.setGroupKey(req.groupKey());
        opt.setTitle(req.title());
        opt.setValue(req.value());
        opt.setOrderNo(req.orderNo());
        return ApiResponse.ok(opt);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'au/option','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        optionRepository.deleteById(id);
        return ApiResponse.ok();
    }
}

