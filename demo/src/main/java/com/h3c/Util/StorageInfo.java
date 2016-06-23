package com.h3c.Util;

import org.csource.fastdfs.StorageClient1;

/** 表示上传操作返回的文件存储信息
 * @author Administrator
 *
 */
public class StorageInfo {
    /**
     * 文件所处卷名
     */
    private String groupName;
    /**
     * 文件名
     */
    private String fileName;
    
    public StorageInfo() {
	}
    
    public StorageInfo(String[] results){
    	this.groupName=results[0];
    	this.fileName=results[1];
    }
    
    
	public StorageInfo(String groupName, String fileName) {
		super();
		this.groupName = groupName;
		this.fileName = fileName;
	}


	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
	@Override
	public String toString() {
		return groupName+" : "+fileName;
	}
    
	public String getFileId(){
		return groupName+ StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR + fileName;
	}
	
	public static  StorageInfo splitFileId(String fileId){
		int pos = fileId.indexOf(StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
		if ((pos <= 0) || (pos == fileId.length() - 1))
		{
			throw new RuntimeException("fileId:"+fileId+"解析失败");
		}
		String groupName = fileId.substring(0, pos);
		String fileName = fileId.substring(pos + 1);
		return new 	StorageInfo(groupName, fileName);
	}
}
