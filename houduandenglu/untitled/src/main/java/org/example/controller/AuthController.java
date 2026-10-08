package org.example.controller;

import jakarta.validation.Valid;
import org.example.VO.LoginVO;
import org.example.common.Result;
import org.example.dto.LoginDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> log(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO=userService.login(loginDTO);
        return Result.success(loginVO);
    }
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
