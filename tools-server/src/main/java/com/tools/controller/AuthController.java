package com.tools.controller;

import com.tools.common.ApiResponse;
import com.tools.service.AuthService;
import com.tools.vo.req.LoginReqVO;
import com.tools.vo.req.RegisterReqVO;
import com.tools.vo.resp.LoginRespVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<LoginRespVO> register(@Valid @RequestBody RegisterReqVO req) {
        return ApiResponse.success(authService.register(req));
    }

    @PostMapping("/login")
    public ApiResponse<LoginRespVO> login(@Valid @RequestBody LoginReqVO req) {
        return ApiResponse.success(authService.login(req));
    }
}
