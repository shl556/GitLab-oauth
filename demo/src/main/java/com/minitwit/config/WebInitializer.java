package com.minitwit.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * 实现WebApplicationInitializer接口等价于web.xml文件，SpringServletContainerInitializer会自动探查该接口的实现类
 * 并创建该实现类的实例完成spring mvc的初始化，而SpringServletContainerInitializer本身会随着servlet容器自动启动并初始化
 * 但只有web.xml不存在时才可以使用
 * @author Administrator
 *
 */
public class WebInitializer implements WebApplicationInitializer {
//
    @Override
    public void onStartup(javax.servlet.ServletContext sc) throws ServletException {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        sc.addListener(new ContextLoaderListener(rootContext));

        //2、springmvc上下文
        AnnotationConfigWebApplicationContext springMvcContext = new AnnotationConfigWebApplicationContext();
        springMvcContext.register(MvcConfig.class);
        //3、DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(springMvcContext);
        ServletRegistration.Dynamic dynamic = sc.addServlet("dispatcherServlet", dispatcherServlet);
        dynamic.setLoadOnStartup(1);
        dynamic.addMapping("/");

        //4、CharacterEncodingFilter
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("utf-8");
        FilterRegistration filterRegistration =
                sc.addFilter("characterEncodingFilter", characterEncodingFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/");

    }
}
