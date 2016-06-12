package com.spark.websocket;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailSender {
	public static void main(String[] args) {
		Transport transport = null;
		try {
			Properties props = new Properties();
			//设置邮件服务器主机名
			props.put("mail.smtp.host", "smtp.h3c.com");
			props.put("mail.smtp.port", "25");
			//设置认证
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication("13636451649@163.com", "1021270919abc");
					//普通连接下权限不足，无法操作
					return new PasswordAuthentication("KF.sunhongliangA@h3c.com", "shl556ABC");
				}
			};
			Session session = Session.getDefaultInstance(props, auth);
			Message msg = new MimeMessage(session);
			//设置发送地址
			msg.setFrom(new InternetAddress("KF.sunhongliangA@h3c.com"));
			//设置接收地址
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					"KF.sunhongliangA@h3c.com"));
			msg.setSubject("Test Title");
			msg.setSentDate(new Date());
			msg.setText("How are you!!");
			transport = session.getTransport("smtp");
			transport.send(msg);
			System.out.println("邮件发送成功!");
		} catch (MessagingException m) {
			System.out.println("邮件发送失败!");
			m.printStackTrace();
		} finally {
			try {
				transport.close();
			} catch (Exception e) {
			}			
		}
	}
}
