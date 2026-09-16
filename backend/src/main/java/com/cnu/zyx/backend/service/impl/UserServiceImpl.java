package com.cnu.zyx.backend.service.impl;


import com.cnu.zyx.backend.entity.User;
import com.cnu.zyx.backend.mapper.DoctorInfoMapper;
import com.cnu.zyx.backend.mapper.PatientInfoMapper;
import com.cnu.zyx.backend.mapper.UserMapper;
import com.cnu.zyx.backend.service.UserService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Override
    public User findUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public Result<String> register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        // 1.判空校验
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail("密码不能为空");
        }

        // ==========新增：用户名格式校验【中文、英文、数字、下划线，长度1‑16】==========
        String usernameReg = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,16}$";
        if (!username.matches(usernameReg)) {
            return Result.fail("用户名支持中文、英文、数字、下划线，长度1‑16位");
        }

        // 2.密码长度校验：6~16位
        String pwdTrim = password.trim();
        if (pwdTrim.length() < 6 || pwdTrim.length() > 16) {
            return Result.fail("密码长度必须为6~16位");
        }

        // 3.判断用户名是否重复
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            return Result.fail("该用户名已被注册");
        }

        // 强制角色转为小写再入库，杜绝大写脏数据
        if(user.getRole() != null){
            user.setRole(user.getRole().toLowerCase());
        }

        // 4.插入主用户数据
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            return Result.fail("注册写入数据库失败");
        }

        // 获取自增id，初始化扩展表（此时role已经是小写，可以直接equals）
        Long newUserId = user.getId();
        String role = user.getRole();
        if ("patient".equals(role)) {
            patientInfoMapper.insertEmpty(newUserId);
        } else if ("doctor".equals(role)) {
            // 医生注册后保持启用状态(status=0)，允许登录；是否审核通过由 doctor_info.audit_status 控制，前端据此限制未审核医生只能提交资质
            doctorInfoMapper.insertEmpty(newUserId);
        }

        return Result.success("注册成功");
    }

    @Override
    public Result<String> changePassword(Long userId, String oldPwd, String newPwd) {
        // 根据id查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 校验原密码
        if (!user.getPassword().equals(oldPwd)) {
            return Result.fail("原密码输入错误");
        }
        // 新密码长度校验
        if (newPwd == null || newPwd.length() < 6 || newPwd.length() > 16) {
            return Result.fail("新密码长度必须为6~16位");
        }
        // 设置新密码并更新
        user.setPassword(newPwd);
        int row = userMapper.updatePassword(user);
        if (row > 0) {
            return Result.success("密码修改成功");
        }
        return Result.fail("密码修改失败");
    }

    @Override
    public Result<String> updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if(user == null){
            return Result.fail("用户不存在");
        }
        int count = userMapper.updateAvatar(userId, avatarUrl);
        if(count > 0){
            return Result.success("头像更新成功");
        }
        return Result.fail("头像更新失败");
    }
}