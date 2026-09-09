package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.User;
import com.cnu.zyx.backend.util.Result;

public interface UserService {
    //登录：根据账号查用户
    User findUserByUsername(String username);
    //注册：新增用户
    Result<String> register(User user);
    //修改密码
    Result<String> changePassword(Long userId, String oldPwd, String newPwd);

    // 更新用户头像
    Result<String> updateAvatar(Long userId, String avatarUrl);
}