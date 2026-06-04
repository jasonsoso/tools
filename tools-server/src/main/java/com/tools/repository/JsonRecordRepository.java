package com.tools.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tools.entity.JsonRecord;
import com.tools.mapper.JsonRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JsonRecordRepository {

    private final JsonRecordMapper jsonRecordMapper;

    public List<JsonRecord> findByUserIdOrderByUpdatedAtDesc(Long userId) {
        LambdaQueryWrapper<JsonRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JsonRecord::getUserId, userId)
               .orderByDesc(JsonRecord::getUpdatedAt);
        return jsonRecordMapper.selectList(wrapper);
    }

    public JsonRecord findById(Long id) {
        return jsonRecordMapper.selectById(id);
    }

    public void save(JsonRecord record) {
        if (record.getId() == null) {
            jsonRecordMapper.insert(record);
        } else {
            jsonRecordMapper.updateById(record);
        }
    }

    public void update(JsonRecord record) {
        jsonRecordMapper.updateById(record);
    }

    public void deleteById(Long id) {
        jsonRecordMapper.deleteById(id);
    }
}
