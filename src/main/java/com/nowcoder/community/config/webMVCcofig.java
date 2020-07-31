package com.nowcoder.community.config;

import com.nowcoder.community.controller.Interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.Interceptor.LoginTicketInterceptor;
import com.nowcoder.community.controller.Interceptor.MessageIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webMVCcofig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

     @Autowired
     private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageIntercepter messageIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");
        
        registry.addInterceptor(messageIntercepter)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
