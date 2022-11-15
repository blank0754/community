package com.example.community.filter;


import com.alibaba.fastjson.JSON;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.utils.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * //检查用户是否已经完成了登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFiter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获得本次请求的URI和从Redis中获取token值
        String requestURI = request.getRequestURI();

        ValueOperations valueOperations = redisTemplate.opsForValue();

        String a = (String) valueOperations.get(Jwt.getMemberIdByJwtToken(request));


        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/login",
                "/login/register",
        };

        //2.判断本次请求是否需要处理
        boolean check = check(requestURI, urls);


        //3.如果不需要处理则直接放行
        if (check){
            log.info("本次请求不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断是否token过期
        if (!Jwt.checkToken(request)){
            log.info("用户未登录");
            //5.如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }

        //将传入的token和Redis中的token进行对比，判断token是否已经被刷新
        if (!request.getHeader("token").equals(a)){
            log.info("用户登录已过期");
            //5.如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }



        //4.判断登录状态，如果已登录，则直接放行
        if (request.getHeader("token")!= null){
            log.info("用户已登录,用户ID为：{}",Jwt.getMemberIdByJwtToken(request));

            String Id = Jwt.getMemberIdByJwtToken(request);
            BaseContext.setCurrentId(Long.valueOf(Id));//调用BaseContext设置id值到线程存储空间

            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断移动端登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("user") != null){
//            log.info("用户已登录,用户ID为：{}",request.getSession().getAttribute("user"));
//
//            Long userId = (Long) request.getSession().getAttribute("user");
//            BaseContext.setCurrentId(userId);//调用BaseContext设置id值到线程存储空间
//
//            filterChain.doFilter(request,response);
//            return;
//        }

        log.info("用户未登录");
        //5.如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;





    }

    /**
     * 路径匹配，检查本次请求是否需要放行s
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
