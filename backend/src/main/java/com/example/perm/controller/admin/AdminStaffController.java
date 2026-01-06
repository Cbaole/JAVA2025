package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.common.Gender;
import com.example.perm.entity.OptionEntity;
import com.example.perm.repo.OptionRepository;
import com.example.perm.repo.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/crm/staffs")
public class AdminStaffController {
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    public AdminStaffController(UserRepository userRepository, OptionRepository optionRepository) {
        this.userRepository = userRepository;
        this.optionRepository = optionRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'crm/staff','see')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(userRepository.findAll());
    }

    @PostMapping("/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/staff','update')")
    public ApiResponse<Object> upsert(@RequestBody Map<String, Object> body) {
        var id = (String) body.get("id");
        if (id == null || id.isBlank()) throw new IllegalArgumentException("人员ID不能为空");

        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("人员不存在"));

        var name = (String) body.get("name");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("姓名不能为空");
        user.setName(name);

        var genderStr = (String) body.get("gender");
        if (genderStr != null && !genderStr.isBlank()) {
            try { user.setGender(Gender.valueOf(genderStr)); } catch (Exception ignored) {}
        }

        var birthdayStr = (String) body.get("birthday");
        if (birthdayStr != null && !birthdayStr.isBlank()) {
            user.setBirthday(LocalDate.parse(birthdayStr));
        } else {
            user.setBirthday(null);
        }

        var phone = (String) body.get("phone");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("联系方式不能为空");
        var existPhone = userRepository.findByPhone(phone).orElse(null);
        if (existPhone != null && !existPhone.getId().equals(user.getId())) throw new IllegalArgumentException("手机号已存在");
        user.setPhone(phone);

        var postOptionId = (String) body.get("postOptionId");
        user.setPostOption(postOptionId != null && !postOptionId.isBlank() ? optionRepository.findById(postOptionId).orElse(null) : null);

        var areaOptionId = (String) body.get("areaOptionId");
        user.setAreaOption(areaOptionId != null && !areaOptionId.isBlank() ? optionRepository.findById(areaOptionId).orElse(null) : null);

        user.setStaffNo((String) body.get("staffNo"));
        user.setRemark((String) body.get("remark"));

        userRepository.save(user);
        return ApiResponse.ok();
    }

    @PostMapping("/move")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/staff','update')")
    public ApiResponse<Object> move(@RequestBody Map<String, Object> body) {
        var idsRaw = body.get("ids");
        if (!(idsRaw instanceof List<?> ids) || ids.isEmpty()) throw new IllegalArgumentException("ids不能为空");
        var areaId = (String) body.get("areaOptionId");
        OptionEntity area = null;
        if (areaId != null && !areaId.isBlank()) {
            area = optionRepository.findById(areaId).orElse(null);
        }
        var updated = new java.util.ArrayList<com.example.perm.entity.UserEntity>();
        for (var idObj : ids) {
            var id = idObj != null ? String.valueOf(idObj) : "";
            if (id.isBlank()) continue;
            var u = userRepository.findById(id).orElse(null);
            if (u == null) continue;
            u.setAreaOption(area);
            updated.add(u);
        }
        if (!updated.isEmpty()) userRepository.saveAll(updated);
        return ApiResponse.ok();
    }
}
