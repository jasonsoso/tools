package com.tools.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tools.common.ApiResponse;
import com.tools.dto.JsonRecordDto;
import com.tools.entity.JsonRecord;
import com.tools.entity.OperationLog;
import com.tools.repository.JsonRecordRepository;
import com.tools.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonService {

    private final JsonRecordRepository recordRepository;
    private final OperationLogRepository logRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponse<List<JsonRecord>> listByUser(Long userId) {
        return ApiResponse.success(recordRepository.findByUserIdOrderByUpdatedAtDesc(userId));
    }

    public ApiResponse<JsonRecord> getById(Long id, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) return ApiResponse.error(404, "记录不存在");
        if (!record.getUserId().equals(userId)) return ApiResponse.error(403, "无权访问");
        return ApiResponse.success(record);
    }

    public ApiResponse<JsonRecord> create(JsonRecordDto dto, Long userId) {
        try {
            objectMapper.readTree(dto.getContent());
        } catch (Exception e) {
            return ApiResponse.error(400, "JSON 格式无效: " + e.getMessage());
        }
        JsonRecord record = new JsonRecord();
        record.setUserId(userId);
        record.setName(dto.getName() != null ? dto.getName() : "未命名记录");
        record.setContent(dto.getContent());
        recordRepository.save(record);

        logOperation(userId, "CREATE", "创建记录：" + record.getName());
        return ApiResponse.success(record);
    }

    public ApiResponse<JsonRecord> update(Long id, JsonRecordDto dto, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) return ApiResponse.error(404, "记录不存在");
        if (!record.getUserId().equals(userId)) return ApiResponse.error(403, "无权修改");
        if (dto.getContent() != null) {
            try {
                objectMapper.readTree(dto.getContent());
            } catch (Exception e) {
                return ApiResponse.error(400, "JSON 格式无效: " + e.getMessage());
            }
            record.setContent(dto.getContent());
        }
        if (dto.getName() != null) record.setName(dto.getName());
        recordRepository.update(record);

        logOperation(userId, "UPDATE", "更新记录：" + record.getName());
        return ApiResponse.success(record);
    }

    public ApiResponse<Void> delete(Long id, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) return ApiResponse.error(404, "记录不存在");
        if (!record.getUserId().equals(userId)) return ApiResponse.error(403, "无权删除");
        recordRepository.deleteById(id);

        logOperation(userId, "DELETE", "删除记录：" + record.getName());
        return ApiResponse.success(null);
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
