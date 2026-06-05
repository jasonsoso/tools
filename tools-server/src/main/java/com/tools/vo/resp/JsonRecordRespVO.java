package com.tools.vo.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JsonRecordRespVO {
    private Long id;
    private Long userId;
    private String name;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
