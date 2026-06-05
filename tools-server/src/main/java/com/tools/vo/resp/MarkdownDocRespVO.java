package com.tools.vo.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MarkdownDocRespVO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
