

package com.h3c.Util;

import java.io.*;
import java.util.*;
import java.net.*;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 上传文件的回调函数，执行文件上传操作
*/
public class UploadLocalFileSender implements UploadCallback
{
	private String local_filename;
	private Logger log=LoggerFactory.getLogger(getClass());
	private long fileSize;
	
	
	public UploadLocalFileSender(String szLocalFilename)
	{
		this.local_filename = szLocalFilename;
	}
	
	/**
	* 读取文件并写入至指定输出流中，成功返回0，否则返回-1
	* @param out output stream for writing file content
	* @return 0 success, return none zero(errno) if fail
	*/
	public int send(OutputStream out)
	{
			int readBytes;
			byte[] buff = new byte[256 * 1024];
			
			try(FileInputStream fis= new FileInputStream(this.local_filename))
			{
				
				while ((readBytes=fis.read(buff)) >= 0)
				{
					if (readBytes == 0)
					{
						continue;
					}
					
					out.write(buff, 0, readBytes);
					out.flush();
				}
			}catch(IOException e){
				log.error("读取文件{}失败",local_filename,e);
				return -1;
			}
			
			return 0;
	}
	
	public long getFileSize(){
		File file=new File(local_filename);
		fileSize=file.length();
		return fileSize;
	}
}
