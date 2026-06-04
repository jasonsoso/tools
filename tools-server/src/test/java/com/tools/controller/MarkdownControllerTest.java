package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.dto.MarkdownDocDto;
import com.tools.entity.MarkdownDoc;
import com.tools.service.MarkdownService;
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
        MarkdownDoc doc = new MarkdownDoc();
        doc.setId(1L);
        doc.setTitle("Test");
        when(markdownService.listByUser(anyLong())).thenReturn(ApiResponse.success(List.of(doc)));

        mockMvc.perform(get("/api/markdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Test"));
    }

    @Test
    @WithMockUser(username = "1")
    void shouldCreateDocument() throws Exception {
        MarkdownDoc saved = new MarkdownDoc();
        saved.setId(1L);
        saved.setTitle("New Doc");
        when(markdownService.create(any(), anyLong())).thenReturn(ApiResponse.success(saved));

        mockMvc.perform(post("/api/markdown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Doc\",\"content\":\"# Hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn404ForMissingDocument() throws Exception {
        when(markdownService.getById(eq(999L), anyLong())).thenReturn(ApiResponse.error(404, "文档不存在"));

        mockMvc.perform(get("/api/markdown/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}
