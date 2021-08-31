package com.hhf.config;

import com.hhf.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyInterceptors  implements WebMvcConfigurer {

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Value("${disconf.project-directory}")
    private String directory;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //添加对用户是否登录的拦截器，并添加过滤项、排除项
		registry.addInterceptor(userLoginInterceptor)
				.excludePathPatterns("/css/**","/js/**","/images/**","/resources/static/**")//排除样式、脚本、图片等资源文件
				.excludePathPatterns("/springBoot/vue/queryByVue")//排除登录页面
				.excludePathPatterns("/springBoot/verifyCode")//排除验证码
				.excludePathPatterns("/base/getSelectDistrictByLevel")//注册账号-选择地址
				.excludePathPatterns("/springBoot/registerUser")//注册账号-用户新增
				.excludePathPatterns("/springBoot/vue/loadingFile")//注册账号-用户头像
				.excludePathPatterns("/springBoot/checkUserName")//注册账号-用户账号
				.excludePathPatterns("*/msgWebSocket/*")//webSocket长连接
				.excludePathPatterns("/api/**")//对外接口
				.excludePathPatterns("/login/**")//登录接口
				.excludePathPatterns("/my/*");//my Controller测试控制器
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //外部访问路径映射到本地磁盘路径
        registry.addResourceHandler("/resources/static/file/**").addResourceLocations("file:"+directory+"/springBoot2.0/springBoot_1/src/main/resources/static/file/");
        registry.addResourceHandler("/resources/static/voice/**").addResourceLocations("file:"+directory+"/springBoot2.0/springBoot_1/src/main/resources/static/voice/");
        registry.addResourceHandler("/resources/static/video/**").addResourceLocations("file:"+directory+"/springBoot2.0/springBoot_1/src/main/resources/static/video/");
        registry.addResourceHandler("/resources/static/games/**").addResourceLocations("file:"+directory+"/springBoot2.0/springBoot_1/src/main/resources/static/games/");
        registry.addResourceHandler("/resources/static/img/**").addResourceLocations("file:"+directory+"/springBoot2.0/springBoot_1/src/main/resources/static/img/");
    }

}
