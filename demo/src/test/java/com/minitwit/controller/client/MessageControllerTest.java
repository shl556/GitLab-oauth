package com.minitwit.controller.client;

import static org.junit.Assert.fail;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;



/**
 * 直接通过Jetty服务器或者Spring Boot启动，然后再通过RestTemplate访问指定URI，这种方式有两个
 * 较大的不足：
 * 一是整个测试会直接改变测试数据库中的数据
 * 二是RestTemplate对于某些请求属性的模拟没有Spring MVC Test方便好用
 * @author Administrator
 *
 */
public class MessageControllerTest {

	private static Server server;

	private RestTemplate template;
	
	private String baseUri="http://localhost:8080";
	
    @BeforeClass
    public static void beforeClass() throws Exception {
        //创建一个server
        server = new Server(8080);
        WebAppContext context = new WebAppContext();
        //使用默认Maven默认目录结构时不用设置
//        String webapp = "src/main/webapp";
//        context.setDescriptor(webapp + "/WEB-INF/web.xml");  //指定web.xml配置文件
//        context.setResourceBase(webapp);  //指定webapp目录
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop(); //当测试结束时停止服务器
    }
    
    @Before
    public void setUp(){
    	template=new RestTemplate();
    }
    
	@Test
	public void testGetUserTimelineMessages() {
		String targetUri=baseUri+"/users/";
	}

	@Test
	public void testGetUserFullTimelineMessages() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPublicTimelineMessages() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessageByCondition() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessageById() {
		fail("Not yet implemented");
	}

}
