package com.nowcoder.community.controller.Interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    /**
     *   思路：在请求的一开始去获取ticket，用ticket去查找有没有对应的user
     *     因为在社区网站中，很多地方都需查看当前用户
     *     因此preHandle可以在Controller执行前就判断有无用户信息
     *    若没有用户信息，重定向到登录界面
     */
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        /**
         *   从服务器的角度来看，这里若想得到用户传过来的cookie，不能随意在preHandle参数中加@CookieValue
         *   但是可以通过request来得到用户传回来的cookie
         */
        /**
         *  调用CookieUtil来查询凭证,第二个参数ticket已经在用户登录功能之后通过response以cookie形式给了客户端：
         *   Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
         *   所以在cookie数组中就有了ticket这个键，所以我们就通过这个CookieUtil.getValue(request,"ticket")来
         *   得到cookie 的value（这个value就是之前用随机字符串生成），通过这个ticket可以查到对应UserId，再通过userId
         *   查到User
         */


        String ticket= CookieUtil.getValue(request,"ticket");
          if(ticket!=null){
            //查询凭证
              LoginTicket loginTicket =userService.findLoginTicket(ticket);
              //检查凭证是否有效
              if (loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                 //根据凭证查询用户
                User user= userService.findUseById(loginTicket.getUserId());
                //在本次请求中持有用户
                     hostHolder.setUsers(user);
              }
          }

        return true;
    }
     //Controller之后，模板之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         User user=hostHolder.getUser();
         if (user!=null &&  modelAndView != null){
             modelAndView.addObject("loginUser",user);
         }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
