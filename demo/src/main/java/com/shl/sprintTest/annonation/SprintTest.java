package com.shl.sprintTest.annonation;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

//指定运行Runner,SpringRunner继承自SpringJUnit4ClassRunner
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)


//自定义一个启动策略，通过继承AbstractTestContextBootstrapper及其子类实现，使用默认配置即可
@BootstrapWith

//指定配置文件位置，配置Bean，配置Groovy脚本或者初始化类(initializers属性)，现阶段可以混合使用xml文件和Groovy脚本，但是不能将其与classes混合
//可以通过在配置bean中使用ImportResource注解的方式引入xml文件或者Groovy脚本定义的bean实现混合的目的
//通过inheritLocations属性控制子类是否继承父类的ContextConfiguration配置，默认为true，即子类会加载父类声明的配置文件等，可能会覆盖父类定义的bean 
//@ContextConfiguration("/applicationContext.xml")//表示相对于类路径，不加斜杠表示相对于项目根路径（包路径）
//@ContextConfiguration("classpath:applicationContext.xml")
//@ContextConfiguration(classes=SpringConfig.class)

//表示加载WebApplicationContext,注意必须和ContextConfiguration同时使用
//WebAppConfiguration的基准路径默认为file，而ContextConfiguration默认为classpath
//@ContextConfiguration
//@WebAppConfiguration("classpath:test-web-resources")

//表示一种层次性分级的ApplicationContext
@WebAppConfiguration
@ContextHierarchy({
	 @ContextConfiguration("/parent-config.xml"),
	 @ContextConfiguration("/child-config.xml")
})


//bean definition profile 给我们提供了一种基于部署上下文/环境来决定哪些 beans被注册的方便的机制，类似于分组的概念
//可以在配置bean中通过Profile注解实现相同的功能。inheritProfiles属性控制子类是否继承父类的ActiveProfile设置，默认为true
//可以实现ActiveProfilesResolver接口并通过resolvers属性注册来实现程序化的控制激活的Profile
//@ActiveProfiles表示激活指定的profiles
//@ContextConfiguration
//@ActiveProfiles({"dev", "integration"})

//加载配置文件
//在配置bean中通过PropertySource注解也可引入配置文件，支持xml或者properties
//属性同名的情况下，通过properties属性设置的优先级最高，其次是通过TestPropertySource加载的配置文件的属性，的同名属性的优先级要高于系统环境或者应用中的同名属性，
//系统环境属性或者应用属性优先级最低，会被覆盖
//inheritLocations和inheritProperties属性控制子类是否继承 父类的properties设置，默认为true
//@ContextConfiguration
//@TestPropertySource("/test.properties")
//@TestPropertySource(properties = { "timezone = GMT", "port: 4242" })

//表示当前测试执行完成后会把ApplicationContext标记为dirty，然后会从缓存中移除重新构建，可用在类或者方法上并指定标记模式
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)//默认为AFTER_CLASS,可选值包括BEFORE_EACH_TEST_METHOD，
//AFTER_EACH_TEST_METHOD,methodMode可选值包括BEFORE_METHOD，AFTER_METHOD（默认）
//跟@ContextHierarchy配合使用时还有hierarchyMode选项

//注册TestExecutionListener,注意必须加上mergeMode参数以避免因为注册自定义listeners导致默认注册的listener没有注册
@ContextConfiguration
@TestExecutionListeners(
		listeners=CustomTestExecutionListener.class,
		mergeMode=MergeMode.MERGE_WITH_DEFAULTS)

//表示将当前执行的事务操作提交，等效于@Rollback(false),可用于类或者方法
@Commit
//@Rollback


public class SprintTest {
	
	   //代替使用SpringRunner，注意两者必须同时使用
	   @ClassRule
	   public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	   @Rule
	   public final SpringMethodRule springMethodRule = new SpringMethodRule();

	
	//支持将ApplicationContext注入进测试类中
	 @Autowired
//	 private ApplicationContext applicationContext;
	 private WebApplicationContext wac;
    
	//表明当前注释方法需要时事务开启前执行，与之相反的是AfterTransaction
	@BeforeTransaction
	void beforeTransaction() {
	    // logic to be executed before a transaction is started
	}
	
	//除了通过public的配置Bean加载ApplicationContext外还可以通过静态内部类的形式加载配置Bean
	@Configuration
    static class Config {

    }
	
}
