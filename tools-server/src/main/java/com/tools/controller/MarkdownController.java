package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.security.SecurityUtils;
import com.tools.service.MarkdownService;
import com.tools.vo.req.MarkdownDocReqVO;
import com.tools.vo.resp.MarkdownDocRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markdown")
@RequiredArgsConstructor
public class MarkdownController {

    private final MarkdownService markdownService;

    @GetMapping
    public ApiResponse<List<MarkdownDocRespVO>> list() {
        return ApiResponse.success(markdownService.listByUser(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MarkdownDocRespVO> get(@PathVariable Long id) {
        return ApiResponse.success(markdownService.getById(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping
    public ApiResponse<MarkdownDocRespVO> create(@Valid @RequestBody MarkdownDocReqVO req) {
        return ApiResponse.success(markdownService.create(req, SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<MarkdownDocRespVO> update(@PathVariable Long id, @Valid @RequestBody MarkdownDocReqVO req) {
        return ApiResponse.success(markdownService.update(id, req, SecurityUtils.getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        markdownService.delete(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.success(null);
    }
}
