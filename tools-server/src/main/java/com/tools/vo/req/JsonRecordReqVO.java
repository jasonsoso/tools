package com.tools.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JsonRecordReqVO {

    @NotBlank(message = "记录名称不能为空")
    private String name;

    private String content;
}
