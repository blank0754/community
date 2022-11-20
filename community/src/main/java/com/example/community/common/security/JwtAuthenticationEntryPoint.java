package com.example.community.common.security;

import cn.hutool.json.JSONUtil;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.utils.JwtUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT自定义认证失败处理
 */


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        outputStream.write(JSONUtil.toJsonStr(R.error("认证失败请登录")).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
