package com.cnu.zyx.backend.exception;

import com.cnu.zyx.backend.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常统一捕获
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //捕获空指针异常
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e){
        e.printStackTrace();
        return Result.build(500,"数据为空，操作失败",null);
    }

    //捕获所有通用异常（兜底）
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        e.printStackTrace();
        return Result.build(500,"服务器内部异常，请联系管理员",null);
    }
}