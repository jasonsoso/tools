package com.tools.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tools.entity.OperationLog;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.converter.OperationLogConverter;
import com.tools.vo.resp.OperationLogRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志查询服务。
 * <p>
 * 操作日志由 {@link JsonService} 和 {@link MarkdownService} 在增删改时自动写入，
 * 本服务仅提供分页查询功能，用于管理员审计追溯。
 */
@Service
@RequiredArgsConstructor
public class LogService {

    private final OperationLogRepository logRepository;

    /**
     * 分页查询操作日志，按创建时间倒序排列。
     *
     * @param page 页码（从 0 开始）
     * @param size 每页条数
     * @return MyBatis-Plus 分页对象，内部记录已转换为 VO
     */
    public IPage<OperationLogRespVO> list(int page, int size) {
        IPage<OperationLog> entityPage = logRepository.findByPage(page, size);
        return entityPage.convert(OperationLogConverter.INSTANCE::toRespVO);
    }
}
