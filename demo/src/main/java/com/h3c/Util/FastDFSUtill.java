package com.h3c.Util;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastDFSUtill {
	private static Logger log=LoggerFactory.getLogger(FastDFSUtill.class);
	
	static{
		globalClientInit();
	}
	private static  void globalClientInit(){
		String client_filePath=ConfigManager.getClient_filename();
		try {
		    //底层通过FileReader读取配置文件,无法直接读取resources底下的配置文件
			ClientGlobal.init(getFilePath(client_filePath));
		    log.info("读取配置文件，network_timeout=" + ClientGlobal.g_network_timeout + "ms");
	  		log.info("读取配置文件，charset=" + ClientGlobal.g_charset);
	 
		} catch (IOException | MyException e) {
			log.error("读取配置文件{}失败",client_filePath,e);
			throw new RuntimeException("读取配置文件失败",e);
		}
	}
	
	/*
	 * StorageClient1继承自StorageClient,其区别在于Client1封装了把返回的字符串数组合并成fielId和把fileID拆分成字符串数组
	 * 的操作，所以一般使用StorageClient1即可
	 */
	public static  StorageClient1 getStorageClient1(){
//		globalClientInit();
		TrackerClient trackerClient = new TrackerClient();
        
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
        StorageServer storageServer=getStorageServer(trackerClient, trackerServer);
        
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
	    return client;
	}
	
	public static  StorageClient getStorageClient(){
//		globalClientInit();
		TrackerClient trackerClient = new TrackerClient();
		
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
		StorageServer storageServer=getStorageServer(trackerClient, trackerServer);
		
		StorageClient client = new StorageClient(trackerServer, storageServer);
		return client;
	}
	
	/** 获取指定ip和端口号的trackServer的storageServer client
	 * @param ipAddress
	 * @param port
	 * @return
	 */
	public static StorageClient1 getStorageClient1(String ipAddress,int port){
//		globalClientInit();
		
		TrackerGroup trackerGroup = new TrackerGroup(new InetSocketAddress[]{new InetSocketAddress(ipAddress,port)});
		
		TrackerClient trackerClient = new TrackerClient(trackerGroup);
        
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
        StorageServer storageServer=getStorageServer(trackerClient, trackerServer);
		
        StorageClient1 client1 = new StorageClient1(trackerServer, storageServer);
	    return client1;
	}
	
	
	/** 获取指定ip和端口号的trackServer的storageServer client
	 * @param ipAddress
	 * @param port
	 * @return
	 */
	public static StorageClient getStorageClient(String ipAddress,int port){
//		globalClientInit();
		
		TrackerGroup trackerGroup = new TrackerGroup(new InetSocketAddress[]{new InetSocketAddress(ipAddress,port)});
		
		TrackerClient trackerClient = new TrackerClient(trackerGroup);
		
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
		StorageServer storageServer=getStorageServer(trackerClient, trackerServer);
		
		StorageClient client = new StorageClient(trackerServer, storageServer);
		return client;
	}
	
	/** 获取指定分组的StorageClient
	 * @param groupName
	 * @return
	 */
	public static StorageClient1 getStorageClient1(String groupName){
//		globalClientInit();
		
		TrackerClient trackerClient=new TrackerClient();
		
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
		StorageServer storageServer=getStorageServer(trackerClient, trackerServer, groupName);
		
		StorageClient1 client1=new StorageClient1(trackerServer, storageServer);
		
		return client1;
	}
	
	
	/** 获取指定分组的StorageClient
	 * @param groupName
	 * @return
	 */
	public static StorageClient getStorageClient(String groupName){
//		globalClientInit();
		
		TrackerClient trackerClient=new TrackerClient();
		
		TrackerServer trackerServer=geTrackerServer(trackerClient);
		
		StorageServer storageServer=getStorageServer(trackerClient, trackerServer, groupName);
		
		StorageClient client=new StorageClient(trackerServer, storageServer);
		
		return client;
	}
	
	
	private static TrackerServer geTrackerServer(TrackerClient client){
		 TrackerServer trackerServer;
			try {
				trackerServer = client.getConnection();
				if (trackerServer == null)
				{
					log.error("没有可用的TrackServer");
					throw new RuntimeException("没有可用的TrackServer");
				}
				return trackerServer;
			} catch (IOException e) {
				log.error("获取TrackServer服务器连接失败",e);
				throw new RuntimeException("获取TrackServer服务器连接失败",e);
			}
	}
	
	private static StorageServer getStorageServer(TrackerClient client,TrackerServer server){
		StorageServer storageServer;
		try {
			storageServer = client.getStoreStorage(server);
			if (storageServer == null)
			{
				log.error("没有可用的StorageServer");
				throw new RuntimeException("没有可用的StorageServer");
			}
			return storageServer;
		} catch (IOException e) {
			log.error("获取StorageServer连接失败",e);
			throw new RuntimeException(e);
		}
	}
	
	
	private static StorageServer getStorageServer(TrackerClient client,TrackerServer server,String groupName){
		StorageServer storageServer;
		try {
			storageServer = client.getStoreStorage(server,groupName);
			if (storageServer == null)
			{
				log.error("没有可用的StorageServer");
				throw new RuntimeException("没有可用的StorageServer");
			}
			return storageServer;
		} catch (IOException e) {
			log.error("获取StorageServer连接失败",e);
			throw new RuntimeException(e);
		}
	}

	/** 将相对于类路径的文件路径转换成为File等可以找到的绝对路径
	 * @param filePath
	 * @return
	 */
	public static String getFilePath(String filePath){
		String path=FastDFSUtill.class.getClassLoader().getResource(filePath).getPath();
	    System.out.println(filePath+"的绝对路径为："+path);
	    return path;
	}
	
}
