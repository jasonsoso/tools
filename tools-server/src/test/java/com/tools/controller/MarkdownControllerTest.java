package com.tools.controller;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.service.MarkdownService;
import com.tools.vo.req.MarkdownDocReqVO;
import com.tools.vo.resp.MarkdownDocRespVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MarkdownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarkdownService markdownService;

    @Test
    @WithMockUser(username = "1")
    void shouldListDocuments() throws Exception {
        MarkdownDocRespVO resp = new MarkdownDocRespVO();
        resp.setId(1L);
        resp.setTitle("Test");
        when(markdownService.listByUser(anyLong())).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/markdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Test"));
    }

    @Test
    @WithMockUser(username = "1")
    void shouldCreateDocument() throws Exception {
        MarkdownDocRespVO resp = new MarkdownDocRespVO();
        resp.setId(1L);
        resp.setTitle("New Doc");
        when(markdownService.create(any(), anyLong())).thenReturn(resp);

        mockMvc.perform(post("/api/markdown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Doc\",\"content\":\"# Hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn404ForMissingDocument() throws Exception {
        when(markdownService.getById(eq(999L), anyLong()))
                .thenThrow(new BusinessException(ErrorCode.DOC_NOT_FOUND));

        mockMvc.perform(get("/api/markdown/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}
