package com.tools.service;

import com.tools.common.ApiResponse;
import com.tools.dto.MarkdownDocDto;
import com.tools.entity.MarkdownDoc;
import com.tools.entity.OperationLog;
import com.tools.repository.MarkdownDocRepository;
import com.tools.repository.OperationLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownServiceTest {

    @Mock
    private MarkdownDocRepository docRepository;

    @Mock
    private OperationLogRepository logRepository;

    @InjectMocks
    private MarkdownService markdownService;

    @Test
    void shouldCreateDocumentAndLog() {
        MarkdownDocDto dto = new MarkdownDocDto();
        dto.setTitle("Test Doc");
        dto.setContent("# Hello");

        doAnswer(inv -> {
            MarkdownDoc doc = inv.getArgument(0);
            doc.setId(1L);
            return null;
        }).when(docRepository).save(any(MarkdownDoc.class));

        ApiResponse<MarkdownDoc> result = markdownService.create(dto, 1L);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getUserId()).isEqualTo(1L);
        assertThat(result.getData().getTitle()).isEqualTo("Test Doc");

        ArgumentCaptor<OperationLog> logCaptor = ArgumentCaptor.forClass(OperationLog.class);
        verify(logRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getAction()).isEqualTo("CREATE");
        assertThat(logCaptor.getValue().getToolType()).isEqualTo("markdown");
    }

    @Test
    void shouldReturnUserDocumentsOnly() {
        MarkdownDoc doc1 = new MarkdownDoc();
        doc1.setId(1L);
        doc1.setUserId(1L);
        doc1.setTitle("User1 Doc");

        when(docRepository.findByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(doc1));

        ApiResponse<List<MarkdownDoc>> result = markdownService.listByUser(1L);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getTitle()).isEqualTo("User1 Doc");
    }

    @Test
    void shouldRejectAccessToOtherUserDocument() {
        MarkdownDoc doc = new MarkdownDoc();
        doc.setId(1L);
        doc.setUserId(2L);
        doc.setTitle("Other's Doc");

        when(docRepository.findById(1L)).thenReturn(doc);

        ApiResponse<MarkdownDoc> result = markdownService.getById(1L, 1L);
        assertThat(result.getCode()).isEqualTo(403);
    }

    @Test
    void shouldReturn404ForMissingDoc() {
        when(docRepository.findById(999L)).thenReturn(null);
        ApiResponse<MarkdownDoc> result = markdownService.getById(999L, 1L);
        assertThat(result.getCode()).isEqualTo(404);
    }
}
