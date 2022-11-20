package com.example.community.config;

import com.example.community.common.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * spring security配置
 * @author java1234_小锋 （公众号：java1234）
 * @site www.java1234.vip
 * @company 南通小锋网络科技有限公司
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private MyUserDetailServiceImpl myUserDetailService;

    @Autowired
    private JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    private static final String URL_WHITELIST[] ={
            "/login",
            "/logout",
            "/login/register",
            "/captcha",
            "/password",
            "/image/**",
            "/test/**"
    } ;

    //BCrypt解析
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }



    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启跨域 以及csrf攻击 关闭
        http
            .cors()
            .and()
            .csrf()
            .disable()

        // 登录登出配置
        .formLogin()
              .successHandler(loginSuccessHandler)
              .failureHandler(loginFailureHandler)
        .and()
            .logout()
            .logoutSuccessHandler(jwtLogoutSuccessHandler)

        // session禁用配置
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态

        // 拦截规则配置
        .and()
        .authorizeRequests()
        .antMatchers(URL_WHITELIST).permitAll()  // 白名单 放行
        .anyRequest().authenticated()


        // 异常处理配置
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)

        // 自定义过滤器配置
        .and()
        .addFilter(jwtAuthenticationFilter());

    }
}
