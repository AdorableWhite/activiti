package com.zxx.activitiflow.config;

import com.zxx.activitiflow.interceptor.RequestTraceIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CustomWebMvcConfig implements WebMvcConfigurer {
    final RequestTraceIdInterceptor requestTraceIdInterceptor;

    @Autowired
    public CustomWebMvcConfig(RequestTraceIdInterceptor requestTraceIdInterceptor) {
        this.requestTraceIdInterceptor = requestTraceIdInterceptor;
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加TraceId拦截器
        registry.addInterceptor(requestTraceIdInterceptor);
    }
}
