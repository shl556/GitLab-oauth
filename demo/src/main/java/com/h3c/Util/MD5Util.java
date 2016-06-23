package com.h3c.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	/**
	 * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
			e.printStackTrace();
		}
	}

	/**
	 * 判断字符串的md5校验码是否与一个已知的md5码相匹配
	 *
	 * @param password
	 *            要校验的字符串
	 * @param md5PwdStr
	 *            已知的md5校验码
	 * @return
	 */
	public static boolean checkPassword(String md5, String md5PwdStr) {
		return md5.equals(md5PwdStr);
	}

	/**
	 * 生成文件的md5校验值
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5String(String filePath) {
		InputStream fis = null;
		fis = MD5Util.class.getClassLoader().getResourceAsStream(filePath);
		try {
			if (fis == null) {
				fis = new FileInputStream(filePath);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件" + filePath + "没有找到", e);
		}

		try {
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				// 输入待计算的数据
				messagedigest.update(buffer, 0, numRead);
			}
		} catch (IOException e) {
			throw new RuntimeException("读取文件" + filePath + "失败", e);
		}
		String md5=bufferToHex(messagedigest.digest());
		System.out.println("获取文件md5:"+md5);
		return md5;
	}

	private static String bufferToHex(byte[] bytes) {
		StringBuffer stringbuffer = new StringBuffer(2 * bytes.length);
		for (int l = 0; l < bytes.length; l++) {
			byte bt = bytes[l];
			char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>>
													// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
			char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
			stringbuffer.append(c0);
			stringbuffer.append(c1);
		}
		return stringbuffer.toString();
	}

}
