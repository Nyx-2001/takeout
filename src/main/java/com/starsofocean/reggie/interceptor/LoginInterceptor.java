package com.starsofocean.reggie.interceptor;//package com.starsofocean.reggie.interceptor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Slf4j
//public class LoginInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//Object user=httpServletRequest.getSession().getAttribute("employee");
//if(user==null){
//    httpServletRequest.setAttribute("msg","，没有权限请先登录");
////    httpServletRequest.getRequestDispatcher("/backend/page/login/login.html").forward(httpServletRequest,httpServletResponse);
//    httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/backend/page/login/login.html");
//    return false;
//}
//return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//    }
//}



//    public final static String SESSION_KEY = "user";
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
//                .excludePathPatterns("/backend/page/login/login.html"
//                        ,"/employee/login","/backend/api","/backend/images"
//                        ,"/backend/js","/backend/plugins","/backend/styles"
//                        ,"/backend/favicon.ico");
//    }