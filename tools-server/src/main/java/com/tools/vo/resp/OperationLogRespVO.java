package com.tools.vo.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogRespVO {
    private Long id;
    private Long userId;
    private String toolType;
    private String action;
    private String detail;
    private LocalDateTime createdAt;
}
