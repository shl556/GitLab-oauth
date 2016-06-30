package com.minitwit.controller;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.minitwit.config.AppConfig;
import com.minitwit.config.MvcConfig;

public class WebContextInitial implements ApplicationContextInitializer<AnnotationConfigWebApplicationContext>{

	@Override
	public void initialize(AnnotationConfigWebApplicationContext applicationContext) {
		ServletContext context=applicationContext.getServletContext();
		applicationContext.register(AppConfig.class);
		context.addListener(new ContextLoaderListener(applicationContext));
		
		AnnotationConfigWebApplicationContext mvc=new AnnotationConfigWebApplicationContext();
		mvc.register(MvcConfig.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(mvc);
        ServletRegistration.Dynamic dynamic = context.addServlet("dispatcherServlet", dispatcherServlet);
        dynamic.setLoadOnStartup(1);
        dynamic.addMapping("/");
        
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("utf-8");
        FilterRegistration filterRegistration =
                context.addFilter("characterEncodingFilter", characterEncodingFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/");
		
	}

}
