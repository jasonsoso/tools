package com.tools.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tools.common.ApiResponse;
import com.tools.service.LogService;
import com.tools.vo.resp.OperationLogRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    public ApiResponse<IPage<OperationLogRespVO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(logService.list(page, size));
    }
}
