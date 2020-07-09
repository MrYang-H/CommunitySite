package com.nowcoder.community.controller.Interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor  implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       if (handler instanceof HandlerMethod){
           //强制转型为方法
           HandlerMethod handlerMethod =(HandlerMethod) handler;
           //查到要访问的这个方法。
           Method method=handlerMethod.getMethod();
           //查询该方法是否有注解标记
           LoginRequired loginRequired=  method.getAnnotation(LoginRequired.class);
           //带有注解的方法表示需要登录信息加以验证，如果没有登录信息User ，就重定向到登录界面。
          if (loginRequired!=null && hostHolder.getUser() ==null){
              response.sendRedirect(request.getContextPath()+"/login");
              return false;
          }

       }
        return true;
    }
}
