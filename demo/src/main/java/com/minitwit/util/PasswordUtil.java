package com.minitwit.util;

import javax.swing.plaf.synth.SynthScrollBarUI;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

	/** 将密码明文转换成散列码
	 * @param pwd
	 * @return
	 */
	public static String hashPassword(String pwd) {
		//BCrypt.gensalt()生成一个类似于秘钥的东西
		String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
		
		return hashed;
	}
	
	/**将密码明文跟散列码比较，返回是否相同
	 * @param pwd 明文密码
	 * @param hash 从数据库中取出的散列码形式的密码
	 * @return
	 */
	public static boolean verifyPassword(String pwd, String hash) {
		boolean b = BCrypt.checkpw(pwd, hash);
		
		return b;
	}
    public static void main(String[] args) {
		String password="123456";  
		//同一密码在不同时间生成的散列码不同
    	String password2=hashPassword(password);
    	String password3=hashPassword(password);
    	System.out.println(password2.equals(password3));
	}
}
