package com.tools.service;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.JsonRecord;
import com.tools.entity.OperationLog;
import com.tools.repository.JsonRecordRepository;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.req.JsonRecordReqVO;
import com.tools.vo.resp.JsonRecordRespVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonServiceTest {

    @Mock
    private JsonRecordRepository recordRepository;

    @Mock
    private OperationLogRepository logRepository;

    @InjectMocks
    private JsonService jsonService;

    @Test
    void shouldCreateRecordAndLog() {
        JsonRecordReqVO req = new JsonRecordReqVO();
        req.setName("test.json");
        req.setContent("{\"key\":\"value\"}");

        doAnswer(inv -> {
            JsonRecord rec = inv.getArgument(0);
            rec.setId(1L);
            rec.setUserId(1L);
            rec.setName("test.json");
            return null;
        }).when(recordRepository).save(any(JsonRecord.class));

        JsonRecordRespVO resp = jsonService.create(req, 1L);

        assertThat(resp.getUserId()).isEqualTo(1L);
        verify(recordRepository).save(any(JsonRecord.class));
        verify(logRepository).save(any(OperationLog.class));
    }

    @Test
    void shouldRejectInvalidJsonOnCreate() {
        JsonRecordReqVO req = new JsonRecordReqVO();
        req.setName("bad");
        req.setContent("not json");

        assertThatThrownBy(() -> jsonService.create(req, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCode.INVALID_JSON.getCode())
                .hasMessageContaining("JSON");
        verify(recordRepository, never()).save(any());
    }

    @Test
    void shouldListUserRecords() {
        JsonRecord rec = new JsonRecord();
        rec.setId(1L);
        rec.setName("test");
        when(recordRepository.findByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(rec));

        List<JsonRecordRespVO> result = jsonService.listByUser(1L);
        assertThat(result).hasSize(1);
    }
}
