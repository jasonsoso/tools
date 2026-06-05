package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.security.SecurityUtils;
import com.tools.service.JsonService;
import com.tools.vo.req.JsonRecordReqVO;
import com.tools.vo.resp.JsonRecordRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/json")
@RequiredArgsConstructor
public class JsonController {

    private final JsonService jsonService;

    @GetMapping
    public ApiResponse<List<JsonRecordRespVO>> list() {
        return ApiResponse.success(jsonService.listByUser(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<JsonRecordRespVO> get(@PathVariable Long id) {
        return ApiResponse.success(jsonService.getById(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping
    public ApiResponse<JsonRecordRespVO> create(@Valid @RequestBody JsonRecordReqVO req) {
        return ApiResponse.success(jsonService.create(req, SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<JsonRecordRespVO> update(@PathVariable Long id, @Valid @RequestBody JsonRecordReqVO req) {
        return ApiResponse.success(jsonService.update(id, req, SecurityUtils.getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        jsonService.delete(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.success(null);
    }
}
