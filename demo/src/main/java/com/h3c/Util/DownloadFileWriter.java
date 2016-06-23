

package com.h3c.Util;

import java.io.*;
import java.util.*;
import java.net.*;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** DownloadCallback为文件下载的回调接口，实现该接口用来执行文件下载时写文件至本地
 * 指定文件filename的操作
 * @author Administrator
 *
 */
public class DownloadFileWriter implements DownloadCallback
{
	private String filename;
	private FileOutputStream out = null;
	private long current_bytes = 0;
	private Logger log=LoggerFactory.getLogger(getClass());
	
	public DownloadFileWriter(String filename)
	{
		this.filename = filename;
	}
	
	/* 将保存数据的二进制数组data的0至bytes位数据写入指定文件中，写入成功或者写入完成返回0，否则返回-1
	 * 
	 * @see org.csource.fastdfs.DownloadCallback#recv(long, byte[], int)
	 */
	public int recv(long file_size, byte[] data, int bytes)
	{
		try
		{
			if (this.out == null)
			{
				this.out = new FileOutputStream(this.filename);
			}
		
			this.out.write(data, 0, bytes);
			this.out.flush();
			this.current_bytes += bytes;
			
			if (this.current_bytes == file_size)
			{
				this.out.close();
				this.out = null;
				this.current_bytes = 0;
			}
		}
		catch(IOException ex)
		{
			log.error("文件写入{}失败",filename,ex);
			return -1;
		}
		
		return 0;
	}
	
	/* 关闭输出流
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable
	{
		if (this.out != null)
		{
			this.out.close();
			this.out = null;
		}
	}
}

