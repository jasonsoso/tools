package com.tools.service;

import com.tools.common.ApiResponse;
import com.tools.dto.MarkdownDocDto;
import com.tools.entity.MarkdownDoc;
import com.tools.entity.OperationLog;
import com.tools.repository.MarkdownDocRepository;
import com.tools.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkdownService {

    private final MarkdownDocRepository docRepository;
    private final OperationLogRepository logRepository;

    public ApiResponse<List<MarkdownDoc>> listByUser(Long userId) {
        return ApiResponse.success(docRepository.findByUserIdOrderByUpdatedAtDesc(userId));
    }

    public ApiResponse<MarkdownDoc> getById(Long id, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) return ApiResponse.error(404, "文档不存在");
        if (!doc.getUserId().equals(userId)) return ApiResponse.error(403, "无权访问此文档");
        return ApiResponse.success(doc);
    }

    public ApiResponse<MarkdownDoc> create(MarkdownDocDto dto, Long userId) {
        MarkdownDoc doc = new MarkdownDoc();
        doc.setUserId(userId);
        doc.setTitle(dto.getTitle() != null ? dto.getTitle() : "未命名文档");
        doc.setContent(dto.getContent() != null ? dto.getContent() : "");
        docRepository.save(doc);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("CREATE");
        log.setDetail("创建文档：" + doc.getTitle());
        logRepository.save(log);

        return ApiResponse.success(doc);
    }

    public ApiResponse<MarkdownDoc> update(Long id, MarkdownDocDto dto, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) return ApiResponse.error(404, "文档不存在");
        if (!doc.getUserId().equals(userId)) return ApiResponse.error(403, "无权修改此文档");
        if (dto.getTitle() != null) doc.setTitle(dto.getTitle());
        if (dto.getContent() != null) doc.setContent(dto.getContent());
        docRepository.update(doc);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("UPDATE");
        log.setDetail("更新文档：" + doc.getTitle());
        logRepository.save(log);

        return ApiResponse.success(doc);
    }

    public ApiResponse<Void> delete(Long id, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) return ApiResponse.error(404, "文档不存在");
        if (!doc.getUserId().equals(userId)) return ApiResponse.error(403, "无权删除此文档");
        docRepository.deleteById(id);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("markdown");
        log.setAction("DELETE");
        log.setDetail("删除文档：" + doc.getTitle());
        logRepository.save(log);

        return ApiResponse.success(null);
    }
}
