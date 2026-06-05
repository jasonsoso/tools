package com.tools.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRespVO {

    private String token;
    private Long userId;
    private String username;
}
