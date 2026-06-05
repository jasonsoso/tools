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

/**
 * JSON 格式化工具服务，提供 JSON 记录的 CRUD 操作。
 * <p>
 * 核心业务规则：
 * <ul>
 *   <li>每条记录只属于创建它的用户（基于 userId 隔离）</li>
 *   <li>创建和更新时会校验 JSON 格式合法性（通过 Jackson 解析验证）</li>
 *   <li>所有增删改操作都会记录到操作日志</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class JsonService {

    private final JsonRecordRepository recordRepository;
    private final OperationLogRepository logRepository;

    /** Jackson 解析器，用于校验 JSON 格式合法性 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取当前用户的所有 JSON 记录，按更新时间倒序排列。
     */
    public List<JsonRecordRespVO> listByUser(Long userId) {
        List<JsonRecord> records = recordRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return JsonRecordConverter.INSTANCE.toRespVOList(records);
    }

    /**
     * 获取单条 JSON 记录的详情。
     * <p>
     * 包含权限检查：只有记录的所有者才能查看。
     */
    public JsonRecordRespVO getById(Long id, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }
        // 用户只能查看自己的记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_JSON);
        }
        return JsonRecordConverter.INSTANCE.toRespVO(record);
    }

    /**
     * 创建 JSON 记录。
     * <p>
     * 在写入数据库前，通过 Jackson 的 {@code readTree} 验证 JSON 语法是否合法。
     * 非法 JSON 会被拒绝并返回具体的解析错误信息。
     */
    public JsonRecordRespVO create(JsonRecordReqVO req, Long userId) {
        // 校验 JSON 格式合法性（格式错误时 Jackson 会抛出详细的异常信息）
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

        // 记录操作日志
        logOperation(userId, "CREATE", "创建记录：" + record.getName());
        return JsonRecordConverter.INSTANCE.toRespVO(record);
    }

    /**
     * 更新 JSON 记录。
     * <p>
     * 支持部分更新：只更新请求中提供的字段（name 或 content）。
     * 如果更新了 content，同样会校验 JSON 格式。
     */
    public JsonRecordRespVO update(Long id, JsonRecordReqVO req, Long userId) {
        JsonRecord record = recordRepository.findById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_MODIFY_JSON);
        }
        // 部分更新：仅更新传入的非 null 字段
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

    /**
     * 删除 JSON 记录（含权限检查）。
     * <p>
     * 注意：该操作不可逆，前端应作二次确认。
     */
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

    /**
     * 记录用户操作到日志表，用于审计追溯。
     *
     * @param userId 操作用户 ID
     * @param action 操作类型（CREATE/UPDATE/DELETE）
     * @param detail 操作详情描述
     */
    private void logOperation(Long userId, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setToolType("json");
        log.setAction(action);
        log.setDetail(detail);
        logRepository.save(log);
    }
}
