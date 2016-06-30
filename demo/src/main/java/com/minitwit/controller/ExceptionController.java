package com.minitwit.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
	@ExceptionHandler
    public void exceptionHandle(Exception e,HttpServletResponse response){
    	try {
    		e.printStackTrace();
			response.sendError(404, "服务器内部错误，请待会重试");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
