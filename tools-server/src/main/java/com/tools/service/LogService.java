package com.tools.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tools.common.ApiResponse;
import com.tools.entity.OperationLog;
import com.tools.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final OperationLogRepository logRepository;

    public ApiResponse<IPage<OperationLog>> list(int page, int size) {
        return ApiResponse.success(logRepository.findByPage(page, size));
    }
}
