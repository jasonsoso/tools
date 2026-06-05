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

@Service
@RequiredArgsConstructor
public class MarkdownService {

    private final MarkdownDocRepository docRepository;
    private final OperationLogRepository logRepository;

    public List<MarkdownDocRespVO> listByUser(Long userId) {
        List<MarkdownDoc> docs = docRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return MarkdownDocConverter.INSTANCE.toRespVOList(docs);
    }

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

    public MarkdownDocRespVO create(MarkdownDocReqVO req, Long userId) {
        MarkdownDoc doc = new MarkdownDoc();
        doc.setUserId(userId);
        doc.setTitle(req.getTitle());
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

    public MarkdownDocRespVO update(Long id, MarkdownDocReqVO req, Long userId) {
        MarkdownDoc doc = docRepository.findById(id);
        if (doc == null) {
            throw new BusinessException(ErrorCode.DOC_NOT_FOUND);
        }
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_MODIFY);
        }
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
