package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 根据用户名查询用户（登录用）
    User selectByUsername(String username);

    // 新增用户（注册用）
    int insert(User user);

    void updateStatus(@Param("id") Long id,@Param("status") Integer status);

    // 根据主键id查询用户
    User selectById(@Param("id") Long id);

    // 修改用户密码
    int updatePassword(User user);

    // 更新用户头像OSS地址
    int updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);
}