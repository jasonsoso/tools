package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.dto.MarkdownDocDto;
import com.tools.entity.MarkdownDoc;
import com.tools.security.SecurityUtils;
import com.tools.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markdown")
@RequiredArgsConstructor
public class MarkdownController {

    private final MarkdownService markdownService;

    @GetMapping
    public ApiResponse<List<MarkdownDoc>> list() {
        return markdownService.listByUser(SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public ApiResponse<MarkdownDoc> get(@PathVariable Long id) {
        return markdownService.getById(id, SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    public ApiResponse<MarkdownDoc> create(@RequestBody MarkdownDocDto dto) {
        return markdownService.create(dto, SecurityUtils.getCurrentUserId());
    }

    @PutMapping("/{id}")
    public ApiResponse<MarkdownDoc> update(@PathVariable Long id, @RequestBody MarkdownDocDto dto) {
        return markdownService.update(id, dto, SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return markdownService.delete(id, SecurityUtils.getCurrentUserId());
    }
}
