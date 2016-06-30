package com.minitwit.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.minitwit.model.Condition;
import com.minitwit.model.Error;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.service.MessageService;

@RestController
@RequestMapping("/messages")
@SessionAttributes("user")
public class MessageController {
    @Autowired
    private MessageService messageService;
    
    //produces对应请求中的Accept，consumes对应请求中的Content-Type
    @RequestMapping(value="/{id}/pub",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserTimelineMessages(@PathVariable("id") int id,@ModelAttribute("user") User user){
    	User result=messageService.getUserTimelineMessages(id,user);
    	return result;
	}
	
    @RequestMapping(value="/{id}/reply",method=RequestMethod.GET,
    produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserFullTimelineMessages(@PathVariable("id") int id,@ModelAttribute("user") User user){
		return messageService.getUserFullTimelineMessages(id,user);
	}
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Message> getPublicTimelineMessages(){
		System.out.println("查询所有消息");
		return messageService.getPublicTimelineMessages();
	}
	
	@RequestMapping(method=RequestMethod.PUT,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	//@RequestAttribute获取请求中的属性但无法解析表单参数，ModelAttribute可以解析表单参数并自动包装成目标对象
	public Error insertMessage( Message m){
		//当模型中没有message数据时，springmvc会自动构建一个实例，不会返回null
		System.out.println("获取message"+m);
		return messageService.insertMessage(m);
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error deleteMessage(@PathVariable int id){
		return messageService.deleteMessage(id);
	}
	
	@RequestMapping(method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error updateMessage(Message m){
		System.out.println("获取message"+m);
		return messageService.updateMessage(m);
	}
	
	
	@RequestMapping(value="/condition",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getMessageByCondition(@ModelAttribute("user") User user,Condition condition){
		System.out.println("username"+user.getUsername());
		System.out.println("condition"+condition.getPubDate());
		return messageService.getMessageByCondition(user, condition);
	}
    
	@RequestMapping(value="/{id}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Message getMessageById(@PathVariable int id){
		return messageService.getMessageById(id);
	}
}
