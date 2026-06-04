package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.dto.JsonRecordDto;
import com.tools.entity.JsonRecord;
import com.tools.security.SecurityUtils;
import com.tools.service.JsonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/json")
@RequiredArgsConstructor
public class JsonController {

    private final JsonService jsonService;

    @GetMapping
    public ApiResponse<List<JsonRecord>> list() {
        return jsonService.listByUser(SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public ApiResponse<JsonRecord> get(@PathVariable Long id) {
        return jsonService.getById(id, SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    public ApiResponse<JsonRecord> create(@RequestBody JsonRecordDto dto) {
        return jsonService.create(dto, SecurityUtils.getCurrentUserId());
    }

    @PutMapping("/{id}")
    public ApiResponse<JsonRecord> update(@PathVariable Long id, @RequestBody JsonRecordDto dto) {
        return jsonService.update(id, dto, SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return jsonService.delete(id, SecurityUtils.getCurrentUserId());
    }
}
