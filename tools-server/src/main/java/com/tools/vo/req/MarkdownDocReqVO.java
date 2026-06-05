package com.tools.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkdownDocReqVO {

    @NotBlank(message = "文档标题不能为空")
    private String title;

    private String content;
}
