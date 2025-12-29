package com.example.perm.controller;

import com.example.perm.common.ApiResponse;
import com.example.perm.repo.OptionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicOptionController {
    private final OptionRepository optionRepository;

    public PublicOptionController(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @GetMapping("/options")
    public ApiResponse<Object> options(@RequestParam String groupKey) {
        return ApiResponse.ok(optionRepository.findByGroupKeyOrderByOrderNoAsc(groupKey));
    }
}

