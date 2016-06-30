package com.minitwit.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//如果继承自WebMvcConfigurationSupport可以移除EnableWebMvc注解
@EnableWebMvc //开启Mvc支持
@ComponentScan(basePackages="com.minitwit.controller",
useDefaultFilters = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class})})
public class MvcConfig  extends WebMvcConfigurerAdapter{

}
