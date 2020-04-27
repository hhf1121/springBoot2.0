package com.hhf.config;

import com.hhf.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyInterceptors  implements WebMvcConfigurer {

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //添加对用户是否登录的拦截器，并添加过滤项、排除项
		registry.addInterceptor(userLoginInterceptor)
				.excludePathPatterns("/css/**","/js/**","/images/**")//排除样式、脚本、图片等资源文件
				.excludePathPatterns("/springBoot/vue/queryByVue")//排除登录页面
				.excludePathPatterns("/springBoot/verifyCode");//排除验证码
//				.excludePathPatterns("/wechatplatformuser/loginnote/updateNoteRBAC");//排除用户点击登录按钮
    }

}
