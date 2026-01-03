package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.repo.DeviceRepository;
import com.example.perm.repo.EquipmentPackageRepository;
import com.example.perm.repo.OptionRepository;
import com.example.perm.repo.PriceBookRepository;
import com.example.perm.repo.SparePartRepository;
import com.example.perm.entity.PriceBookEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/crm/price-book")
public class AdminPriceBookController {
    private final PriceBookRepository repository;
    private final OptionRepository optionRepository;
    private final DeviceRepository deviceRepository;
    private final SparePartRepository sparePartRepository;
    private final EquipmentPackageRepository packageRepository;

    public AdminPriceBookController(
            PriceBookRepository repository,
            OptionRepository optionRepository,
            DeviceRepository deviceRepository,
            SparePartRepository sparePartRepository,
            EquipmentPackageRepository packageRepository
    ) {
        this.repository = repository;
        this.optionRepository = optionRepository;
        this.deviceRepository = deviceRepository;
        this.sparePartRepository = sparePartRepository;
        this.packageRepository = packageRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'crm/price','see')")
    public ApiResponse<Object> list(@RequestParam String type) {
        var rows = repository.findByProductTypeOrderByCreateTimeDesc(type);
        var idSet = rows.stream().map(PriceBookEntity::getProductId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        Map<String, Object> productMap = new HashMap<>();
        if ("DEVICE".equalsIgnoreCase(type)) {
            deviceRepository.findAllById(idSet).forEach(d -> productMap.put(d.getId(), d));
        } else if ("SPARE".equalsIgnoreCase(type)) {
            sparePartRepository.findAllById(idSet).forEach(s -> productMap.put(s.getId(), s));
        } else if ("PACKAGE".equalsIgnoreCase(type)) {
            packageRepository.findAllById(idSet).forEach(p -> productMap.put(p.getId(), p));
        }
        List<Object> res = rows.stream().map(r -> {
            Object p = productMap.get(r.getProductId());
            String name = "";
            String model = "";
            if (p != null) {
                if (p instanceof com.example.perm.entity.DeviceEntity d) {
                    name = d.getName();
                    model = d.getModel();
                } else if (p instanceof com.example.perm.entity.SparePartEntity s) {
                    name = s.getName();
                    model = s.getModel();
                } else if (p instanceof com.example.perm.entity.EquipmentPackageEntity e) {
                    name = e.getName();
                    model = e.getModel();
                }
            }
            final String productNameText = name;
            final String productModelText = model;
            String deptTitle = r.getDeptOption() != null ? r.getDeptOption().getTitle() : "";
            String deptId = r.getDeptOption() != null ? r.getDeptOption().getId() : "";
            return (Object) new Object() {
                public final String id = r.getId();
                public final String productType = r.getProductType();
                public final String productId = r.getProductId();
                public final String productName = productNameText;
                public final String productModel = productModelText;
                public final Object deptOption = new Object() {
                    public final String id = deptId;
                    public final String title = deptTitle;
                };
                public final Integer quantity = r.getQuantity();
                public final String price = r.getPrice();
                public final String remark = r.getRemark();
                public final java.time.LocalDateTime createTime = r.getCreateTime();
            };
        }).toList();
        return ApiResponse.ok(res);
    }

    @PostMapping("/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/price','add') or @perm.has(authentication,'crm/price','update')")
    public ApiResponse<Object> upsert(@RequestBody Map<String, Object> req) {
        var id = req.get("id") != null ? String.valueOf(req.get("id")) : "";
        var type = req.get("productType") != null ? String.valueOf(req.get("productType")) : "";
        var deptOptionId = req.get("deptOptionId") != null ? String.valueOf(req.get("deptOptionId")) : "";
        var productId = req.get("productId") != null ? String.valueOf(req.get("productId")) : "";
        if (type.isBlank()) throw new IllegalArgumentException("productType不能为空");
        if (deptOptionId.isBlank()) throw new IllegalArgumentException("deptOptionId不能为空");
        if (productId.isBlank()) throw new IllegalArgumentException("productId不能为空");

        var deptOpt = optionRepository.findById(deptOptionId).orElseThrow(() -> new IllegalArgumentException("部门选项不存在"));
        var existing = repository.findByDeptOptionAndProductTypeAndProductId(deptOpt, type, productId);
        if (existing.isPresent() && (id.isBlank() || !existing.get().getId().equals(id))) {
            throw new IllegalArgumentException("该部门已维护过该产品价格");
        }

        PriceBookEntity entity = (id.isBlank())
                ? new PriceBookEntity()
                : repository.findById(id).orElseThrow(() -> new IllegalArgumentException("价格本记录不存在"));
        entity.setDeptOption(deptOpt);
        entity.setProductType(type);
        entity.setProductId(productId);

        Integer qty = 1;
        if (req.get("quantity") != null) {
            try {
                qty = Integer.parseInt(String.valueOf(req.get("quantity")));
            } catch (Exception ignored) {
                qty = 1;
            }
        }
        if (qty == null || qty <= 0) qty = 1;
        entity.setQuantity(qty);

        entity.setPrice(req.get("price") != null ? String.valueOf(req.get("price")) : "");
        entity.setRemark(req.get("remark") != null ? String.valueOf(req.get("remark")) : "");
        repository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/price','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ApiResponse.ok();
    }
}
