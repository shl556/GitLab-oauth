package com.h3c.DFSDao;

import org.csource.common.NameValuePair;

import com.h3c.Util.StorageInfo;

public interface DFSDao {
	/**
	 * 上传指定位置的文件，内部还是会将文件转换成字节数组上传
	 * 
	 * @param fileInfo
	 * @param filePath
	 * @return
	 */
	StorageInfo uploadFile(NameValuePair[] fileInfo, String filePath);
	
	/**
	 * 上传文件指定位置的文件至指定分组
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            上传文件路径
	 * @param groupName
	 *            上传至指定的分组
	 * @return
	 */
	StorageInfo uploadFile(NameValuePair[] fileInfo, String filePath, String groupName);
	
	
	/**
	 * 借助回调函数上传文件
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            上传文件路径
	 * @param groupName
	 *            上传至指定的分组
	 * @return
	 */
	StorageInfo uploadFileByCallBack(NameValuePair[] fileInfo, String filePath, String groupName);
	
	
	/**
	 * 上传从文件
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            上传文件路径
	 * @param groupName
	 *            上传至指定的分组
	 * @param masterFileId
	 *            主文件的fileID
	 * @param prefixName
	 *            生成从文件id时的后缀
	 * @return
	 * 
	 */
	public StorageInfo uploadSlaveFile(NameValuePair[] fileInfo, String filePath, StorageInfo storageInfo,String prefixName);
	
	/**
	 * 上传appender File,upload_appender_file与upload_file上传文件相比，主要区别在于前者上传的文件可以修改，
	 * 执行appender_file,modify_file或者truncate_file操作
	 * 这三者主要用于文本文件的修改（增量同步），appender_file还可用于分片上传。而后者不能修改，
	 * 如果需要修改只能通过删除然后重新上传的方式完成
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	StorageInfo uploadAppenderFile(NameValuePair[] fileInfo, String filePath);
	
	/**
	 * 上传文件至指定分组
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            文件路径
	 * @param groupName
	 *            指定分组
	 * @return
	 */
    StorageInfo uploadAppenderFile(NameValuePair[] fileInfo, String filePath, String groupName);
    
    /**
	 * 通过回调函数上传文件至指定分组中
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            文件路径
	 * @param groupName
	 *            指定分组
	 * @return
	 */
	StorageInfo uploadAppenderFileByCallBack(NameValuePair[] fileInfo, String filePath, String groupName);
	
	/**
	 * 执行文件上传，小文件采用uploadFile，大文件先判断该文件是否存在断点，若存在则从断点处开始上传，否则 从头开始上传
	 * 
	 * @param fileInfo
	 * @param filePath
	 * @return
	 */
	StorageInfo uploadFileByFragment(NameValuePair[] fileInfo, String filePath);
	
	/** 多线程分片上传大文件
	 * @param fileInfo
	 * @param localFilePath
	 * @return
	 */
	StorageInfo uploadFileByMultiThread(NameValuePair[] fileInfo, String localFilePath);
	
	/**
	 * 删除文件
	 * 
	 * @param storageInfo
	 * @return
	 */
   boolean delete(StorageInfo storageInfo);
   
   /**
	 * 下载文件
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	boolean download(StorageInfo storageInfo, String localFilePath);
	
	/**
	 * 通过回调函数下载文件
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	boolean downloadByCallBack(StorageInfo storageInfo, String localFilePath);
	
	/** 分片下载
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	boolean downloadByFragment(StorageInfo storageInfo, String localFilePath);
	
	/** 多线程分片下载大文件
	 * @param info
	 * @param localFilePath
	 * @return
	 */
	boolean downloadByMultiThread(StorageInfo info, String localFilePath);
}
