package com.example.perm.service;

import com.example.perm.common.UserStatus;
import com.example.perm.common.Gender;
import com.example.perm.dto.AuthDtos;
import com.example.perm.repo.OptionRepository;
import com.example.perm.repo.UserRepository;
import com.example.perm.util.IdCardUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    public RegisterService(UserRepository userRepository, OptionRepository optionRepository) {
        this.userRepository = userRepository;
        this.optionRepository = optionRepository;
    }

    public AuthDtos.RegisterCheckResponse checkIdCard(String idCard) {
        var existing = userRepository.findByIdCard(idCard);
        if (existing.isEmpty()) {
            return new AuthDtos.RegisterCheckResponse(false, null, null);
        }
        var user = existing.get();
        if (user.getStatus() == UserStatus.PENDING) {
            return new AuthDtos.RegisterCheckResponse(false, null, null);
        }
        return new AuthDtos.RegisterCheckResponse(true, user.getName(), user.getUsername());
    }

    @Transactional
    public String register(AuthDtos.RegisterRequest req) {
        var existingByIdCard = userRepository.findByIdCard(req.idCard());
        var user = existingByIdCard.orElseGet(com.example.perm.entity.UserEntity::new);
        if (existingByIdCard.isPresent() && user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException("身份证号已注册");
        }

        var existingByPhone = userRepository.findByPhone(req.phone());
        if (existingByPhone.isPresent() && (user.getId() == null || !existingByPhone.get().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("手机号已注册");
        }

        var parsed = IdCardUtil.parse18(req.idCard());

        user.setName(req.name());
        user.setIdCard(req.idCard());
        user.setPhone(req.phone());
        user.setBirthday(req.birthday() != null ? req.birthday() : parsed.birthday());

        var gender = parsed.gender();
        if (req.gender() != null && !req.gender().isBlank()) {
            try {
                gender = Gender.valueOf(req.gender().trim().toUpperCase());
            } catch (Exception ignored) {
            }
        }
        user.setGender(gender);

        user.setStatus(UserStatus.PENDING);

        if (req.postOptionId() != null && !req.postOptionId().isBlank()) {
            user.setPostOption(optionRepository.findById(req.postOptionId()).orElseThrow(() -> new IllegalArgumentException("岗位选项不存在")));
        } else {
            user.setPostOption(null);
        }
        if (req.areaOptionId() != null && !req.areaOptionId().isBlank()) {
            user.setAreaOption(optionRepository.findById(req.areaOptionId()).orElseThrow(() -> new IllegalArgumentException("所属片区选项不存在")));
        } else {
            user.setAreaOption(null);
        }

        return userRepository.save(user).getId();
    }
}
