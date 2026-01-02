package com.example.perm.controller.admin;

import com.example.perm.common.ApiResponse;
import com.example.perm.entity.EquipmentPackageAttachmentEntity;
import com.example.perm.entity.EquipmentPackageEntity;
import com.example.perm.repo.EquipmentPackageAttachmentRepository;
import com.example.perm.repo.EquipmentPackageRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/crm/packages")
public class AdminEquipmentPackageController {
    private final EquipmentPackageRepository repository;
    private final EquipmentPackageAttachmentRepository attachmentRepository;

    public AdminEquipmentPackageController(EquipmentPackageRepository repository, EquipmentPackageAttachmentRepository attachmentRepository) {
        this.repository = repository;
        this.attachmentRepository = attachmentRepository;
    }

    @GetMapping
    @PreAuthorize("@perm.has(authentication,'crm/package','see')")
    public ApiResponse<Object> list() {
        return ApiResponse.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has(authentication,'crm/package','read')")
    public ApiResponse<Object> detail(@PathVariable String id) {
        var item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备成套不存在"));
        var atts = attachmentRepository.findByEquipmentPackageOrderByCreateTimeDesc(item);
        return ApiResponse.ok(new Object() {
            public final EquipmentPackageEntity base = item;
            public final java.util.List<EquipmentPackageAttachmentEntity> attachments = atts;
        });
    }

    @PostMapping("/upsert")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/package','add') or @perm.has(authentication,'crm/package','update')")
    public ApiResponse<Object> upsert(@RequestBody Map<String, Object> req) {
        EquipmentPackageEntity entity = (req.get("id") == null || ((String) req.get("id")).isBlank())
                ? new EquipmentPackageEntity()
                : repository.findById((String) req.get("id")).orElseThrow(() -> new IllegalArgumentException("设备成套不存在"));
        entity.setName((String) req.get("name"));
        entity.setModel((String) req.get("model"));
        entity.setParams((String) req.get("params"));
        entity.setPrice((String) req.get("price"));
        entity.setSize((String) req.get("size"));
        entity.setWeight((String) req.get("weight"));
        entity.setLeadTime((String) req.get("leadTime"));
        entity.setRemark((String) req.get("remark"));
        repository.save(entity);
        return ApiResponse.ok(entity.getId());
    }

    @PostMapping("/import")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/package','add') or @perm.has(authentication,'crm/package','update')")
    public ApiResponse<Object> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择文件");
        int count = 0;
        try (var wb = WorkbookFactory.create(file.getInputStream())) {
            var sheet = wb.getSheetAt(0);
            var formatter = new DataFormatter();
            var idx = new HashMap<String, Integer>();
            int firstRow = sheet.getFirstRowNum();
            int startRow = firstRow;
            Row headerRow = sheet.getRow(firstRow);
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    var header = cellText(formatter, cell);
                    var key = packageHeaderKey(header);
                    if (key != null && !idx.containsKey(key)) {
                        idx.put(key, cell.getColumnIndex());
                    }
                }
                if (!idx.isEmpty()) {
                    startRow = firstRow + 1;
                }
            }
            if (idx.isEmpty()) {
                idx.put("name", 0);
                idx.put("model", 1);
                idx.put("params", 2);
                idx.put("price", 3);
                idx.put("weight", 4);
                idx.put("leadTime", 5);
                idx.put("size", 6);
                idx.put("remark", 7);
            }
            for (int r = startRow; r <= sheet.getLastRowNum(); r++) {
                var row = sheet.getRow(r);
                if (row == null) continue;
                var name = cellText(formatter, getCell(row, idx.get("name")));
                if (name.isBlank()) continue;
                var entity = new EquipmentPackageEntity();
                entity.setName(name);
                entity.setModel(cellText(formatter, getCell(row, idx.get("model"))));
                entity.setParams(cellText(formatter, getCell(row, idx.get("params"))));
                entity.setPrice(cellText(formatter, getCell(row, idx.get("price"))));
                entity.setWeight(cellText(formatter, getCell(row, idx.get("weight"))));
                entity.setLeadTime(cellText(formatter, getCell(row, idx.get("leadTime"))));
                entity.setSize(cellText(formatter, getCell(row, idx.get("size"))));
                entity.setRemark(cellText(formatter, getCell(row, idx.get("remark"))));
                repository.save(entity);
                count++;
            }
        }
        return ApiResponse.ok(count);
    }

    @PostMapping("/{id}/attachments/upload")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/package','update')")
    public ApiResponse<Object> upload(@PathVariable String id, @RequestParam("file") MultipartFile file) throws Exception {
        var item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备成套不存在"));
        var root = new File("backend/uploads");
        if (!root.exists()) root.mkdirs();
        var ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        var fname = java.util.UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
        var target = new File(root, fname);
        Files.copy(file.getInputStream(), target.toPath());
        var att = new EquipmentPackageAttachmentEntity();
        att.setEquipmentPackage(item);
        att.setFileName(file.getOriginalFilename());
        att.setFilePath(target.getPath());
        attachmentRepository.save(att);
        return ApiResponse.ok(att.getId());
    }

    @GetMapping("/attachments/{id}/download")
    @PreAuthorize("@perm.has(authentication,'crm/package','read')")
    public ResponseEntity<FileSystemResource> download(@PathVariable String id) {
        var att = attachmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("附件不存在"));
        var file = new File(att.getFilePath());
        if (!file.exists()) throw new IllegalArgumentException("文件不存在");
        var encoded = URLEncoder.encode(att.getFileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @PostMapping("/attachments/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/package','update')")
    public ApiResponse<Object> deleteAttachment(@PathVariable String id) {
        attachmentRepository.deleteById(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/delete")
    @Transactional
    @PreAuthorize("@perm.has(authentication,'crm/package','update')")
    public ApiResponse<Object> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ApiResponse.ok();
    }

    private static Cell getCell(Row row, Integer idx) {
        if (row == null || idx == null) return null;
        return row.getCell(idx);
    }

    private static String cellText(DataFormatter formatter, Cell cell) {
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    private static String packageHeaderKey(String header) {
        if (header == null) return null;
        var h = header.trim();
        if (h.isBlank()) return null;
        if (h.contains("名称")) return "name";
        if (h.contains("型号")) return "model";
        if (h.contains("参数")) return "params";
        if (h.contains("价格")) return "price";
        if (h.contains("重量")) return "weight";
        if (h.contains("交货") || h.contains("周期")) return "leadTime";
        if (h.contains("尺寸")) return "size";
        if (h.contains("备注")) return "remark";
        return null;
    }
}
