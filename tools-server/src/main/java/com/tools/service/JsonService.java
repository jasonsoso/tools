package com.tools.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.JsonRecord;
import com.tools.entity.OperationLog;
import com.tools.repository.JsonRecordRepository;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.converter.JsonRecordConverter;
import com.tools.vo.req.JsonRecordReqVO;
import com.tools.vo.resp.JsonRecordRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonService {

    private final JsonRecordRepository recordRepository;
    private final OperationLogRepository logRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<JsonRecordRespVO> listByUser(Long userId) {
        List<JsonRecord> records = recordRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return JsonRecordConverter.INSTANCE.toRespVOList(records);
    }

    public JsonRecordRespVO getById(Long id, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_JSON);
        }
        return JsonRecordConverter.INSTANCE.toRespVO(record);
    }

    public JsonRecordRespVO create(JsonRecordReqVO req, Long userId) {
        try {
            objectMapper.readTree(req.getContent());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_JSON, e.getMessage());
        }
        JsonRecord record = new JsonRecord();
        record.setUserId(userId);
        record.setName(req.getName());
        record.setContent(req.getContent());
        recordRepository.save(record);

        logOperation(userId, "CREATE", "创建记录：" + record.getName());
        return JsonRecordConverter.INSTANCE.toRespVO(record);
    }

    public JsonRecordRespVO update(Long id, JsonRecordReqVO req, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_MODIFY_JSON);
        }
        if (req.getContent() != null) {
            try {
                objectMapper.readTree(req.getContent());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.INVALID_JSON, e.getMessage());
            }
            record.setContent(req.getContent());
        }
        if (req.getName() != null) {
            record.setName(req.getName());
        }
        recordRepository.update(record);

        logOperation(userId, "UPDATE", "更新记录：" + record.getName());
        return JsonRecordConverter.INSTANCE.toRespVO(record);
    }

    public void delete(Long id, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DELETE_JSON);
        }
        recordRepository.deleteById(id);

        logOperation(userId, "DELETE", "删除记录：" + record.getName());
    }

    private void logOperation(Long userId, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("json");
        log.setAction(action);
        log.setDetail(detail);
        logRepository.save(log);
    }
}
