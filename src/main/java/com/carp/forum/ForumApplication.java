package com.carp.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.carp.forum.interceptor.TokenInterceptor;

@SpringBootApplication
public class ForumApplication {
	@Autowired
	private TokenInterceptor tokenInterceptor;
	
	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

    @Bean
    WebMvcConfigurer myMvcConfigurer() {
        return new WebMvcConfigurer() {
            //AJOUT D'UN FILTRE
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                   registry.addInterceptor(tokenInterceptor);
            }

            // CROS ORIGIN
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
            }
            // static resources
//			@Override
//			public void addResourceHandlers(ResourceHandlerRegistry registry) {
//							
//				registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
//				registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
//			}
        };
    }
    
    
    
}
