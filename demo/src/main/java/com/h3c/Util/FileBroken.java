package com.h3c.Util;

import java.util.Date;

import org.csource.fastdfs.FileInfo;

/**表示上传或者下载文件时断点的实体类
 * @author Administrator
 *
 */
public class FileBroken {
	
	/**
	 * 服务器端文件id
	 */
	private String fileId;
	
	/**
	 * 客户端本地文件路径
	 */
	private String localFilePath;
	
	public int getThreadNum() {
		return threadNum;
	}


	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	/**
	 * 执行上传或者下载操作的线程编号
	 */
	private int threadNum;
	
	/**
	 * 断点时的文件大小
	 */
	private int fileSize;
	
	/**
	 * 目标文件实际大小
	 */
	private int targetFileSize;
	
	/**
	 * 产生断点时间
	 */
	private Date stopTime;
	
	/**
	 * 产生断点的原因
	 */
	private String errorMessage;

	public FileBroken() {
	}
	
	
	public FileBroken(String fileId, String localFilePath, int fileSize, int targetFileSize, Date stopTime,
			String errorMessage) {
		super();
		this.fileId = fileId;
		this.localFilePath = localFilePath;
		this.fileSize = fileSize;
		this.targetFileSize = targetFileSize;
		this.stopTime = stopTime;
		this.errorMessage = errorMessage;
	}

	

	public FileBroken(String fileId, int threadNum, int fileSize, Date stopTime, String errorMessage,String localFilePath) {
		super();
		this.fileId = fileId;
		this.threadNum = threadNum;
		this.fileSize = fileSize;
		this.stopTime = stopTime;
		this.errorMessage = errorMessage;
		this.localFilePath=localFilePath;
	}


	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getTargetFileSize() {
		return targetFileSize;
	}

	public void setTargetFileSize(int targetFileSize) {
		this.targetFileSize = targetFileSize;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return fileId+",fileSize: "+fileSize+", targetFileSize:"+targetFileSize+", errorMessage:"+errorMessage;
	}

}
