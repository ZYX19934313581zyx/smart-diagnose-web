package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.User;
import com.cnu.zyx.backend.service.UserService;
import com.cnu.zyx.backend.util.JwtUtil;
import com.cnu.zyx.backend.util.OssUtil;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OssUtil ossUtil;

    //登录接口
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String,String> params){
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.findUserByUsername(username);
        if(user == null){
            return Result.fail("账号不存在");
        }
        if(!user.getPassword().equals(password)){
            return Result.fail("密码错误");
        }
        if(user.getStatus() != null && user.getStatus() == 1){
            return Result.fail("账号尚未审核通过，暂时无法登录");
        }
        //登录成功，生成token
        String token = JwtUtil.createToken(user.getId());
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("token",token);
        dataMap.put("userInfo",user);
        return Result.success(dataMap);
    }

    //注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        return userService.register(user);
    }

    // 修改密码接口
    @PostMapping("/changePwd")
    public Result<String> changePwd(@RequestBody Map<String,String> params, HttpServletRequest request){
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        // 从token获取当前登录用户ID
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        return userService.changePassword(userId, oldPwd, newPwd);
    }

    // 【废弃旧上传接口，前端统一走全局/upload】
    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        // 获取当前登录用户id
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        // 上传到OSS，存放在avatar文件夹
        String avatarUrl = ossUtil.upload(file, "avatar");
        // 更新数据库头像地址
        return userService.updateAvatar(userId, avatarUrl);
    }

    // 新接口：接收OSS返回的图片链接，更新数据库头像
    @PutMapping("/update/avatar")
    public Result<String> updateAvatar(@RequestParam String avatarUrl, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        return userService.updateAvatar(userId, avatarUrl);
    }
}