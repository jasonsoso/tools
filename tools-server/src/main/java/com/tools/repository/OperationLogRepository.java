package com.tools.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tools.entity.OperationLog;
import com.tools.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OperationLogRepository {

    private final OperationLogMapper operationLogMapper;

    public void save(OperationLog log) {
        operationLogMapper.insert(log);
    }

    public IPage<OperationLog> findByPage(int page, int size) {
        Page<OperationLog> p = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        return operationLogMapper.selectPage(p, wrapper);
    }
}
