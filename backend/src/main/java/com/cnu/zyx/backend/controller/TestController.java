package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/hello")
    public Result<?> hello(){
        return Result.success("访问成功，token校验通过");
    }
}