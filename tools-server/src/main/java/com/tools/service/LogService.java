package com.tools.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tools.entity.OperationLog;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.converter.OperationLogConverter;
import com.tools.vo.resp.OperationLogRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final OperationLogRepository logRepository;

    public IPage<OperationLogRespVO> list(int page, int size) {
        IPage<OperationLog> entityPage = logRepository.findByPage(page, size);
        return entityPage.convert(OperationLogConverter.INSTANCE::toRespVO);
    }
}
