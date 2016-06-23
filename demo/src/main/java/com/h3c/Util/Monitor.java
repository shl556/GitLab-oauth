
package com.h3c.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.StructGroupStat;
import org.csource.fastdfs.StructStorageStat;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 获取FastDFS系统各组件状态信息管理类
 * @author Administrator
 *
 */
public class Monitor
{
	
	
	private Monitor()
	{
	}
	
	/** 获取所有的分组信息
	 * @param configFilePath
	 * @return
	 */
	public StructGroupStat[] getAllStructGroupStat(String configFilePath){
		Logger log=LoggerFactory.getLogger(getClass());
		//读取配置文件
		try {
			ClientGlobal.init(configFilePath);
		} catch (IOException | MyException e) {
			log.error("配置文件{}没有找到",configFilePath,e);
			throw new RuntimeException("配置文件没有找到",e);
		}
  		log.debug("读取配置文件，network_timeout=" + ClientGlobal.g_network_timeout + "ms");
  		log.debug("读取配置文件，charset=" + ClientGlobal.g_charset);
  		
  	    //TrackerServer的客户端，用来获取或者删除指定的StorageServer
  		TrackerClient tracker = new TrackerClient();
  		
  		//可通过TrackServer获取server的InetSocketAddres 和 Socket,StorageServer继承自TrackServer,增加了一个getStorePathIndex方法
  		TrackerServer trackerServer;
		try {
			trackerServer = tracker.getConnection();
  		if (trackerServer == null)
  		{
  			log.error("没有可用的TrackServer");
  			throw new RuntimeException("没有可用的TrackServer，请检查服务器配置");
  		}
		} catch (IOException e) {
			 log.error("获取TrackerServer连接失败",e);
			 throw new RuntimeException("获取TrackerServer连接失败",e);
		}
  		//StructGroupStat表示一个分组（卷），一个分组内包含多个StorageServer,各个StorageServer的数据互为冗余备份，该StructGroupStat的最大容量为各个StorageServer中
  		//的最小存储容量。
  		StructGroupStat[] groupStats;
		try {
			groupStats = tracker.listGroups(trackerServer);
		
  		if (groupStats == null)
  		{
  			log.error("ERROR! list groups error, error no: " + tracker.getErrorCode());
  			throw new RuntimeException("没有可用的StructGroupStat");
  		}
  		log.info("获取所有分组成功，总共有{}个分组",groupStats.length);
	    return groupStats;
		} catch (IOException e) {
			log.error("获取所有的分组失败",e);
			throw new RuntimeException("获取所有的分组失败",e);
		}
  		
	}
	
	/** 获取指定分组的状态信息和基本配置
	 * @param groupStat
	 */
	public static void printStructGroupStatInfo(StructGroupStat groupStat){
		System.out.println(" group name = " + groupStat.getGroupName());
		System.out.println("disk total space = " + groupStat.getTotalMB() + "MB");
		System.out.println("disk free space = " + groupStat.getFreeMB() + " MB");
		System.out.println("trunk free space = " + groupStat.getTrunkFreeMB() + " MB");
		System.out.println("storage server count = " + groupStat.getStorageCount());
		System.out.println("active server count = " + groupStat.getActiveCount());
		System.out.println("storage server port = " + groupStat.getStoragePort());
		System.out.println("storage HTTP port = " + groupStat.getStorageHttpPort());
		System.out.println("store path count = " + groupStat.getStorePathCount());
		System.out.println("subdir count per path = " + groupStat.getSubdirCountPerPath());
		System.out.println("current write server index = " + groupStat.getCurrentWriteServer());
		System.out.println("current trunk file id = " + groupStat.getCurrentTrunkFileId());
	}
	
	/**获取指定存储节点的状态信息
	 * @param storageStat
	 */
	public static  void printStructStorageStatInfo(StructStorageStat storageStat){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("\t\tstorage id = " + storageStat.getId());
		System.out.println("\t\tip_addr = " + storageStat.getIpAddr() + "  " + ProtoCommon.getStorageStatusCaption(storageStat.getStatus()));
		System.out.println("\t\thttp domain = " + storageStat.getDomainName());
		System.out.println("\t\tversion = " + storageStat.getVersion());
		System.out.println("\t\tjoin time = " + df.format(storageStat.getJoinTime()));
		System.out.println("\t\tup time = " + (storageStat.getUpTime().getTime() == 0 ? "" : df.format(storageStat.getUpTime())));
		System.out.println("\t\ttotal storage = " + storageStat.getTotalMB() + "MB");
		System.out.println("\t\tfree storage = " + storageStat.getFreeMB() + "MB");
		System.out.println("\t\tupload priority = " + storageStat.getUploadPriority());
		System.out.println("\t\tstore_path_count = " + storageStat.getStorePathCount());
		System.out.println("\t\tsubdir_count_per_path = " + storageStat.getSubdirCountPerPath());
		System.out.println("\t\tstorage_port = " + storageStat.getStoragePort());
		System.out.println("\t\tstorage_http_port = " + storageStat.getStorageHttpPort());
		System.out.println("\t\tcurrent_write_path = " + storageStat.getCurrentWritePath());
		System.out.println("\t\tsource ip_addr = " + storageStat.getSrcIpAddr());
		System.out.println("\t\tif_trunk_server = " + storageStat.isTrunkServer());
		System.out.println("\t\ttotal_upload_count = " + storageStat.getTotalUploadCount());
		System.out.println("\t\tsuccess_upload_count = " + storageStat.getSuccessUploadCount());
		System.out.println("\t\ttotal_append_count = " + storageStat.getTotalAppendCount());
		System.out.println("\t\tsuccess_append_count = " + storageStat.getSuccessAppendCount());
		System.out.println("\t\ttotal_modify_count = " + storageStat.getTotalModifyCount());
		System.out.println("\t\tsuccess_modify_count = " + storageStat.getSuccessModifyCount());
		System.out.println("\t\ttotal_truncate_count = " + storageStat.getTotalTruncateCount());
		System.out.println("\t\tsuccess_truncate_count = " + storageStat.getSuccessTruncateCount());
		System.out.println("\t\ttotal_set_meta_count = " + storageStat.getTotalSetMetaCount());
		System.out.println("\t\tsuccess_set_meta_count = " + storageStat.getSuccessSetMetaCount());
		System.out.println("\t\ttotal_delete_count = " + storageStat.getTotalDeleteCount());
		System.out.println("\t\tsuccess_delete_count = " + storageStat.getSuccessDeleteCount());
		System.out.println("\t\ttotal_download_count = " + storageStat.getTotalDownloadCount());
		System.out.println("\t\tsuccess_download_count = " + storageStat.getSuccessDownloadCount());
		System.out.println("\t\ttotal_get_meta_count = " + storageStat.getTotalGetMetaCount());
		System.out.println("\t\tsuccess_get_meta_count = " + storageStat.getSuccessGetMetaCount());
		System.out.println("\t\ttotal_create_link_count = " + storageStat.getTotalCreateLinkCount());
		System.out.println("\t\tsuccess_create_link_count = " + storageStat.getSuccessCreateLinkCount());
		System.out.println("\t\ttotal_delete_link_count = " + storageStat.getTotalDeleteLinkCount());
		System.out.println("\t\tsuccess_delete_link_count = " + storageStat.getSuccessDeleteLinkCount());
		System.out.println("\t\ttotal_upload_bytes = " + storageStat.getTotalUploadBytes());
		System.out.println("\t\tsuccess_upload_bytes = " + storageStat.getSuccessUploadBytes());
		System.out.println("\t\ttotal_append_bytes = " + storageStat.getTotalAppendBytes());
		System.out.println("\t\tsuccess_append_bytes = " + storageStat.getSuccessAppendBytes());
		System.out.println("\t\ttotal_modify_bytes = " + storageStat.getTotalModifyBytes());
		System.out.println("\t\tsuccess_modify_bytes = " + storageStat.getSuccessModifyBytes());
		System.out.println("\t\ttotal_download_bytes = " + storageStat.getTotalDownloadloadBytes());
		System.out.println("\t\tsuccess_download_bytes = " + storageStat.getSuccessDownloadloadBytes());
		System.out.println("\t\ttotal_sync_in_bytes = " + storageStat.getTotalSyncInBytes());
		System.out.println("\t\tsuccess_sync_in_bytes = " + storageStat.getSuccessSyncInBytes());
		System.out.println("\t\ttotal_sync_out_bytes = " + storageStat.getTotalSyncOutBytes());
		System.out.println("\t\tsuccess_sync_out_bytes = " + storageStat.getSuccessSyncOutBytes());
		System.out.println("\t\ttotal_file_open_count = " + storageStat.getTotalFileOpenCount());
		System.out.println("\t\tsuccess_file_open_count = " + storageStat.getSuccessFileOpenCount());
		System.out.println("\t\ttotal_file_read_count = " + storageStat.getTotalFileReadCount());
		System.out.println("\t\tsuccess_file_read_count = " + storageStat.getSuccessFileReadCount());
		System.out.println("\t\ttotal_file_write_count = " + storageStat.getTotalFileWriteCount());
		System.out.println("\t\tsuccess_file_write_count = " + storageStat.getSuccessFileWriteCount());
		System.out.println("\t\tlast_heart_beat_time = " + df.format(storageStat.getLastHeartBeatTime()));
		System.out.println("\t\tlast_source_update = " + df.format(storageStat.getLastSourceUpdate()));
		System.out.println("\t\tlast_sync_update = " + df.format(storageStat.getLastSyncUpdate()));
	}
	
	
  
  /** 获取当前节点在该节点所处分组的文件同步延时时间字符串，将秒数转换成对应的天，时，分， 秒的形式
 * @param storageStats
 * @param currentStorageStat
 * @return
 */
public static String getSyncedDelayString(StructStorageStat[] storageStats, StructStorageStat currentStorageStat)
  {
		long maxLastSourceUpdate = 0;
		//获取同一分组下的所有StructStorageStat中最后一次文件更新时间
		for (StructStorageStat storageStat : storageStats)
		{
      if (storageStat != currentStorageStat && storageStat.getLastSourceUpdate().getTime() > maxLastSourceUpdate)
      {
      	maxLastSourceUpdate = storageStat.getLastSourceUpdate().getTime();
      }
		}
		
		//当前整个分组没有更新
		if (maxLastSourceUpdate == 0)
		{
		 	return "";
		}
        
		//当前存储节点一直没有跟其他存储节点同步
		if (currentStorageStat.getLastSyncedTimestamp().getTime() == 0)
		{
			return " (never synced)";
		}
		
		//获取当前节点与组内最后一个文件更新节点的延时秒数
		int delaySeconds = (int)((maxLastSourceUpdate - currentStorageStat.getLastSyncedTimestamp().getTime()) / 1000);
		int day = delaySeconds / (24 * 3600);
		int remainSeconds = delaySeconds % (24 * 3600);
		int hour = remainSeconds / 3600;
		remainSeconds %= 3600;
		int minute = remainSeconds / 60;
		int second = remainSeconds % 60;
		
		String delayTimeStr;
		if (day != 0)
		{
			delayTimeStr = String.format("%1$d days %2$02dh:%3$02dm:%4$02ds", day, hour, minute, second);
		}
		else if (hour != 0)
		{
			delayTimeStr = String.format("%1$02dh:%2$02dm:%3$02ds", hour, minute, second);
		}
		else if (minute != 0)
		{
			delayTimeStr = String.format("%1$02dm:%2$02ds", minute, second);
		}
		else
		{
			delayTimeStr = String.format("%1$ds", second);
		}
		
		return " (" + delayTimeStr + " delay)";
	}

/** 打印指定分组的StorageServer信息
 * @param trackerServer
 * @param groupName
 */
public static  void printStorageServerInfo(TrackerServer trackerServer,String groupName){
	TrackerClient trackerClient=new TrackerClient();
	try {
		StorageServer[] storageServers=trackerClient.getStoreStorages(trackerServer, groupName);
		if (storageServers == null)
		{
			System.err.println("获取storage servers失败, error code: " + trackerClient.getErrorCode());
		}
		else
		{
			System.err.println("store storage servers数量: " + storageServers.length);
			System.err.println("store storage servers各IP地址: ");
			for (int k=0; k<storageServers.length; k++)
			{
				System.err.println((k+1) + ". " + storageServers[k].getInetSocketAddress().getAddress().getHostAddress() + ":" + storageServers[k].getInetSocketAddress().getPort());
			}
			System.err.println("");
		}
	} catch (IOException e) {
		System.err.println("获取StorageServer失败");
		e.printStackTrace();
	}
}

/** 打印指定文件所处分组的StorageServer信息
 * @param client
 * @param server
 * @param groupName
 * @param fileName
 */
public static  void printStorageServerInfo(TrackerClient client,TrackerServer server,String groupName,String fileName){
	try {
		ServerInfo[] servers=client.getFetchStorages(server, groupName, fileName);
		if (servers == null)
		{
			System.err.println("获取storage servers失败, error code: " + client.getErrorCode());
		}
		else
		{
			System.err.println("查询到的关联storage server总数: " + servers.length);
			System.err.println("查询到的关联storage server信息: ");
			for (int k=0; k<servers.length; k++)
			{
				System.err.println((k+1) + ". " + servers[k].getIpAddr() + ":" + servers[k].getPort());
			}
			System.err.println("");
		}
	} catch (IOException e) {
		e.printStackTrace();
		throw new RuntimeException("获取Storage Server失败",e);
	}
	
}

public static void printFileInfo(String groupName,String fileName){
	try {
		StorageClient client=FastDFSUtill.getStorageClient();
		NameValuePair[] info=client.get_metadata(groupName, fileName);
	    FileInfo info2=client.get_file_info(groupName, fileName);
		if(info!=null&&info2!=null){
	        for(NameValuePair vp:info){
	        	System.out.println(vp.getName()+" : "+vp.getValue());
	        }
	        System.out.println("Crc32: "+info2.getCrc32());
	        System.out.println("CreateTime: "+info2.getCreateTimestamp());
	        System.out.println("FileSize: "+info2.getFileSize());
	        System.out.println("SourceIpAddress(最开始上传的服务器ip)： "+info2.getSourceIpAddr());
	    }else {
			System.out.println("没有找到"+groupName+": "+fileName+"文件的属性信息");
		}
	} catch (IOException | MyException e) {
		e.printStackTrace();
		throw new RuntimeException("获取文件属性信息失败");
	}
}

public static void setFileInfo(String groupName,String fileName,NameValuePair[] fileInfo){
	try {
		int errorno=FastDFSUtill.getStorageClient().set_metadata(groupName, fileName, fileInfo, ProtoCommon.STORAGE_SET_METADATA_FLAG_MERGE);
	    if (errorno==0) {
			System.out.println("给文件"+groupName+":"+fileName+"设置属性信息成功");
		}
	} catch (IOException | MyException e) {
		e.printStackTrace();
	    throw new RuntimeException("设置文件属性信息失败");
	}
	
}


}
