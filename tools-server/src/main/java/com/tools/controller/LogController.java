package com.tools.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tools.common.ApiResponse;
import com.tools.entity.OperationLog;
import com.tools.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    public ApiResponse<IPage<OperationLog>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return logService.list(page, size);
    }
}
