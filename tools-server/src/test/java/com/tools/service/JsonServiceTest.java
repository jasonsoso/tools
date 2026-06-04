package com.tools.service;

import com.tools.common.ApiResponse;
import com.tools.dto.JsonRecordDto;
import com.tools.entity.JsonRecord;
import com.tools.entity.OperationLog;
import com.tools.repository.JsonRecordRepository;
import com.tools.repository.OperationLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        JsonRecordDto dto = new JsonRecordDto();
        dto.setName("test.json");
        dto.setContent("{\"key\":\"value\"}");

        ApiResponse<JsonRecord> result = jsonService.create(dto, 1L);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getUserId()).isEqualTo(1L);
        verify(recordRepository).save(any(JsonRecord.class));
        verify(logRepository).save(any(OperationLog.class));
    }

    @Test
    void shouldRejectInvalidJsonOnCreate() {
        JsonRecordDto dto = new JsonRecordDto();
        dto.setName("bad");
        dto.setContent("not json");

        ApiResponse<JsonRecord> result = jsonService.create(dto, 1L);
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).contains("JSON");
        verify(recordRepository, never()).save(any());
    }

    @Test
    void shouldListUserRecords() {
        JsonRecord rec = new JsonRecord();
        rec.setId(1L);
        rec.setName("test");
        when(recordRepository.findByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(rec));

        ApiResponse<List<JsonRecord>> result = jsonService.listByUser(1L);
        assertThat(result.getData()).hasSize(1);
    }
}
