package com.tools.service;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.MarkdownDoc;
import com.tools.entity.OperationLog;
import com.tools.repository.MarkdownDocRepository;
import com.tools.repository.OperationLogRepository;
import com.tools.vo.req.MarkdownDocReqVO;
import com.tools.vo.resp.MarkdownDocRespVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        MarkdownDocReqVO req = new MarkdownDocReqVO();
        req.setTitle("Test Doc");
        req.setContent("# Hello");

        doAnswer(inv -> {
            MarkdownDoc doc = inv.getArgument(0);
            doc.setId(1L);
            return null;
        }).when(docRepository).save(any(MarkdownDoc.class));

        MarkdownDocRespVO resp = markdownService.create(req, 1L);

        assertThat(resp.getUserId()).isEqualTo(1L);
        assertThat(resp.getTitle()).isEqualTo("Test Doc");

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

        List<MarkdownDocRespVO> result = markdownService.listByUser(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("User1 Doc");
    }

    @Test
    void shouldRejectAccessToOtherUserDocument() {
        MarkdownDoc doc = new MarkdownDoc();
        doc.setId(1L);
        doc.setUserId(2L);
        doc.setTitle("Other's Doc");

        when(docRepository.findById(1L)).thenReturn(doc);

        assertThatThrownBy(() -> markdownService.getById(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCode.FORBIDDEN.getCode());
    }

    @Test
    void shouldReturn404ForMissingDoc() {
        when(docRepository.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> markdownService.getById(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCode.DOC_NOT_FOUND.getCode());
    }
}
