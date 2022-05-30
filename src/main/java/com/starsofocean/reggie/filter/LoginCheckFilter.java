package com.starsofocean.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.starsofocean.reggie.common.BaseContext;
import com.starsofocean.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //获取请求路径
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //调用检查方法
        boolean check = check(requestURI, urls);

        //如果为不需要处理的方法，则check为true，直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //如果管理端用户已经登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            //通过session获取用户id
            Long employeeId = (Long)request.getSession().getAttribute("employee");
            //将用户id存入线程中
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(request,response);
            return;
        }

        //如果移动端用户已经登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            //通过session获取用户id
            Long userId = (Long)request.getSession().getAttribute("user");
            //将用户id存入线程中
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 定义方法检查请求路径是否为不需要处理的路径
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
