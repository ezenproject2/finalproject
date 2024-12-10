package com.ezen.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    String uploadPath = "file:///D:\\project_workspace\\fileUpload\\";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }

    /* 결제 api에서 토큰 받아올 때 필요함 */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}