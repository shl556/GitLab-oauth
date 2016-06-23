package com.h3c.Util;



import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/** 读取配置文件
 * @author Administrator
 *
 */
public class ConfigManager {
	
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIGFILEPATH="FastDFSConfig.properties";
    private static PropertiesConfiguration config;
    
    /**
     * 客户端配置文件
     */
    private static  String client_filename;
    
    /**
     * 最大并发数
     */
    private static  int maxConcurrentNum;
    
    /**
     * 分片大小
     */
    private static  int fragmentSize;

    /**
     * 多线程处理时单线程处理文件的大小
     */
    private static int threadFragmentSize;
    
    static{
    	readConfigFile();
    }


    public static String getClient_filename() {
		return client_filename;
	}
    
    public static int getMaxConcurrentNum() {
		return maxConcurrentNum;
	}
    
    public static int getFragmentSize() {
		return fragmentSize;
	}
    
    public static int getThreadFragmentSize() {
		return threadFragmentSize;
	}
    
    /**
     * 重新加载配置文件
     */
    public static void reload(){
    	config.reload();
    	readConfigFile();
    }
    
    private static void readConfigFile(){
    	try {
			config=new PropertiesConfiguration(CONFIGFILEPATH);
			client_filename=config.getString("client_filename");
			logger.info("客户端配置文件位置，{}",client_filename);
			maxConcurrentNum=config.getInt("maxConcurrentNum");
			logger.info("最大并发数，{}",maxConcurrentNum);
			fragmentSize=config.getInt("fragmentSize");
			logger.info("分片大小，{}",fragmentSize);
			threadFragmentSize=config.getInt("threadFragmentSize");
			logger.info("多线程时单线程处理的文件大小：{}",threadFragmentSize);
		} catch (ConfigurationException e) {
			logger.error("读取配置文件{}失败",CONFIGFILEPATH,e);
			throw new RuntimeException("读取配置文件失败",e);
		}
    }
}
