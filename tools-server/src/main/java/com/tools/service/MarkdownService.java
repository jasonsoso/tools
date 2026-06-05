package com.tools.service;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.MarkdownDoc;
import com.tools.entity.OperationLog;
import com.tools.repository.MarkdownDocRepository;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.converter.MarkdownDocConverter;
import com.tools.vo.req.MarkdownDocReqVO;
import com.tools.vo.resp.MarkdownDocRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Markdown 在线编辑器服务，提供 Markdown 文档的 CRUD 操作。
 * <p>
 * 核心业务规则与 {@link JsonService} 一致：
 * <ul>
 *   <li>文档归属于创建者（userId 隔离）</li>
 *   <li>增删改操作写入操作日志</li>
 *   <li>创建时 content 默认为空字符串</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MarkdownService {

    private final MarkdownDocRepository docRepository;
    private final OperationLogRepository logRepository;

    /**
     * 获取当前用户的所有文档，按更新时间倒序排列。
     */
    public List<MarkdownDocRespVO> listByUser(Long userId) {
        List<MarkdownDoc> docs = docRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return MarkdownDocConverter.INSTANCE.toRespVOList(docs);
    }

    /**
     * 获取单篇文档详情（含所有权校验）。
     */
    public MarkdownDocRespVO getById(Long id, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) {
            throw new BusinessException(ErrorCode.DOC_NOT_FOUND);
        }
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return MarkdownDocConverter.INSTANCE.toRespVO(doc);
    }

    /**
     * 创建 Markdown 文档。
     * <p>
     * 如果请求中未提供 content，默认为空字符串，方便前端新建后直接进入编辑状态。
     */
    public MarkdownDocRespVO create(MarkdownDocReqVO req, Long userId) {
        MarkdownDoc doc = new MarkdownDoc();
        doc.setUserId(userId);
        doc.setTitle(req.getTitle());
        // 新建文档时 content 可空，默认为 ""
        doc.setContent(req.getContent() != null ? req.getContent() : "");
        docRepository.save(doc);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("CREATE");
        log.setDetail("创建文档：" + doc.getTitle());
        logRepository.save(log);

        return MarkdownDocConverter.INSTANCE.toRespVO(doc);
    }

    /**
     * 更新 Markdown 文档（含所有权校验）。
     * <p>
     * 支持部分更新：title 和 content 可单独更新。
     */
    public MarkdownDocRespVO update(Long id, MarkdownDocReqVO req, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) {
            throw new BusinessException(ErrorCode.DOC_NOT_FOUND);
        }
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_MODIFY);
        }
        // 部分更新：仅更新传入的非 null 字段
        if (req.getTitle() != null) {
            doc.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            doc.setContent(req.getContent());
        }
        docRepository.update(doc);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("UPDATE");
        log.setDetail("更新文档：" + doc.getTitle());
        logRepository.save(log);

        return MarkdownDocConverter.INSTANCE.toRespVO(doc);
    }

    /**
     * 删除 Markdown 文档（含所有权校验，操作不可逆）。
     */
    public void delete(Long id, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) {
            throw new BusinessException(ErrorCode.DOC_NOT_FOUND);
        }
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DELETE);
        }
        docRepository.deleteById(id);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("DELETE");
        log.setDetail("删除文档：" + doc.getTitle());
        logRepository.save(log);
    }
}
