package com.example.community.common.security;

import com.example.community.common.BaseContext;
import com.example.community.common.constant.JwtConstant;
import com.example.community.entity.CheckResult;
import com.example.community.entity.User;
import com.example.community.service.UserService;
import com.example.community.utils.JwtUtils;
import com.github.pagehelper.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;



/**
 * Jwt认证管理器
 */

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private MyUserDetailServiceImpl myUserDetailService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    private static final String URL_WHITELIST[] ={
            "/login",
            "/logout",
            "/login/register",
            "/captcha",
            "/password",
            "/image/**",
            "/test/**"
    };

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader("token");

        System.out.println("请求的URL"+request.getRequestURI());
        System.out.println("Token"+token);
        //如果token是空 或者 url在白名单里，则放行
        if (StringUtil.isEmpty(token) || new ArrayList<String>(Arrays.asList(URL_WHITELIST)).contains(request.getRequestURI())){
            chain.doFilter(request,response);
            return;
        }

        Claims claims1 = JwtUtils.parseJWT(token);
        //获取id
        String jti = (String) claims1.get("jti");
        System.out.println(claims1.get("jti"));

        // 从Redis中获取token值
        ValueOperations<String, String> valueOperations = redisTemplate
                .opsForValue();
        String a = valueOperations.get(jti);

        //将传入的token和Redis中的token进行对比，判断token是否已经被刷新
        if (!token.equals(a)){
            //5.如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
            throw new JwtException("token过期");
        }


        CheckResult checkResult = JwtUtils.validateJWT(token);

        if (!checkResult.isSuccess()){
            switch (checkResult.getErrCode()){
                case JwtConstant.JWT_ERRCODE_NULL:throw new JwtException("token不存在");
                case JwtConstant.JWT_ERRCODE_FAIL:throw new JwtException("token验证不通过");
                case JwtConstant.JWT_ERRCODE_EXPIRE:throw new JwtException("token过期");
            }
        }

        //获取token的名称
        Claims claims = JwtUtils.parseJWT(token);
        String username = claims.getSubject();
        System.out.println(username);
        User byUsername = userService.getByUsername(username);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,null,myUserDetailService.getUserAuthority());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request,response);

    }
}
