package com.cnu.zyx.backend.interceptor;

import com.cnu.zyx.backend.entity.User;
import com.cnu.zyx.backend.mapper.UserMapper;
import com.cnu.zyx.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        response.setContentType("application/json;charset=UTF-8");

        //1.无token
        if(token == null || token.isEmpty()){
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"登录失效，请重新登录\",\"data\":null}");
            writer.flush();
            writer.close();
            return false;
        }

        //2.token校验不合法 / 过期
        if(!JwtUtil.verifyToken(token)){
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"token无效，请重新登录\",\"data\":null}");
            writer.flush();
            writer.close();
            return false;
        }

        //3.查询用户
        Long userId = JwtUtil.getUserId(token);
        User user = userMapper.selectById(userId);
        if(user == null){
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"msg\":\"用户不存在\",\"data\":null}");
            writer.flush();
            writer.close();
            return false;
        }

        //存入用户id、角色
        request.setAttribute("userId",userId);
        request.setAttribute("role",user.getRole());
        return true;
    }
}