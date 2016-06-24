package com.h3c.DFSDao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.RuntimeErrorException;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.DownloadCallback;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.h3c.Util.ConfigManager;
import com.h3c.Util.DownloadFileWriter;
import com.h3c.Util.FastDFSUtill;
import com.h3c.Util.FileBroken;
import com.h3c.Util.MD5Util;
import com.h3c.Util.StorageInfo;
import com.h3c.Util.UploadLocalFileSender;

@Repository
public class DFSDaoImpl implements DFSDao {

	private static Logger log = LoggerFactory.getLogger(DFSDaoImpl.class);
	
	@Autowired
	private FileBrokenDao dao;

	private final int fragmentSize = ConfigManager.getFragmentSize();

	private final int threadFragmentSize = ConfigManager.getThreadFragmentSize();

	/**
	 * 外面控制多线程线程池的关闭
	 */
	private boolean run=true;

	public void shutdownThreadPool(){
		run=false;
	}
	/**
	 * 上传指定位置的文件，内部还是会将文件转换成字节数组上传
	 * 
	 * @param fileInfo
	 * @param filePath
	 * @return
	 */
	public StorageInfo uploadFile(NameValuePair[] fileInfo, String filePath) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
            StorageClient1 client=FastDFSUtill.getStorageClient1();
			// 参数文件的扩展名可以为空，如果为空FastDFS会在内部根据文件路径生成扩展名
			String[] result = client.upload_file(filePath, getExtName(filePath), fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

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
	public StorageInfo uploadFile(NameValuePair[] fileInfo, String filePath, String groupName) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] result = client.upload_file(groupName, getFileBytes(filePath), getExtName(filePath), fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

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
	public StorageInfo uploadFileByCallBack(NameValuePair[] fileInfo, String filePath, String groupName,UploadLocalFileSender callback) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
//			UploadLocalFileSender callBack = new UploadLocalFileSender(filePath);
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] result = client.upload_file(groupName, callback.getFileSize(), callback, getExtName(filePath),
					fileInfo);

			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

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
	 * 		主从文件是指文件ID有关联的文件，一个主文件可以对应多个从文件。 主文件ID = 主文件名 + 主文件扩展名 从文件ID =
	 *         主文件名 + 从文件后缀名 + 从文件扩展名 以本场景为例：主文件为原始图片，从文件为该图片的一张或多张缩略图。 流程说明：
	 *         先上传主文件（即：原文件），得到主文件FID
	 *         然后上传从文件（即：缩略图），指定主文件FID和从文件后缀名，上传后得到从文件FID。 注意：
	 *         FastDFS中的主从文件只是在文件ID上有联系。FastDFS
	 *         server端没有记录主从文件对应关系，因此删除主文件，FastDFS不会自动删除从文件。
	 *         删除主文件后，从文件的级联删除，需要由应用端来实现。
	 */

	public StorageInfo uploadSlaveFile(NameValuePair[] fileInfo, String filePath, StorageInfo storageInfo,
			String prefixName) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] result = client.upload_file(storageInfo.getGroupName(), storageInfo.getFileName(), prefixName,
					filePath, getExtName(filePath), fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("从文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

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
	public StorageInfo uploadAppenderFile(NameValuePair[] fileInfo, String filePath) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			// 参数文件的扩展名可以为空，如果为空FastDFS会在内部根据文件路径生成扩展名
			String[] result = client.upload_appender_file(filePath, getExtName(filePath), fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

	/**
	 * 上传文件至指定分组，当分组不存在时会报IOException,recv package size -1 != 10
	 * 
	 * @param fileInfo
	 *            文件属性信息
	 * @param filePath
	 *            文件路径
	 * @param groupName
	 *            指定分组
	 * @return
	 */
	public StorageInfo uploadAppenderFile(NameValuePair[] fileInfo, String filePath, String groupName) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] result = client.upload_appender_file(groupName, getFileBytes(filePath), getExtName(filePath),
					fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

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
	public StorageInfo uploadAppenderFileByCallBack(NameValuePair[] fileInfo, String filePath, String groupName,UploadLocalFileSender callback) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		try {
//			UploadLocalFileSender callBack = new UploadLocalFileSender(filePath);
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] result = client.upload_appender_file(groupName, callback.getFileSize(), callback,
					getExtName(filePath), fileInfo);
			if (result != null) {
				StorageInfo info = new StorageInfo(result);
				log.info("文件上传成功，返回fileID:{}", info);
				return info;
			}
		} catch (IOException | MyException e) {
			log.error("上传文件{}失败", filePath, e);
			throw new RuntimeException("文件上传失败", e);
		}
		return null;
	}

	/**
	 * 执行文件上传，小文件采用uploadFile，大文件先判断该文件是否存在断点，若存在则从断点处开始上传，否则 从头开始上传
	 * 
	 * @param fileInfo
	 * @param filePath
	 * @return
	 */
	public StorageInfo uploadFileByFragment(NameValuePair[] fileInfo, String filePath) {
		fileInfo = addMd5(fileInfo, filePath);
		filePath = FastDFSUtill.getFilePath(filePath);
		byte[] file = getFileBytes(filePath);
		// 待上传的目标文件实际大小
		int targetFilesize = file.length;

		if (targetFilesize < fragmentSize) {
			return uploadFile(fileInfo, filePath);
		}

		FileBroken broken = dao.get(filePath);
		if (broken == null) {
			return uploadFileByFragmentFromStart(filePath, fileInfo);
		} else {
			return uploadFileByFragmentFromBroken(file, broken);
		}

	}

	/**
	 * 从头开始上传文件，如果产生异常则保存断点
	 * 
	 * @param filePath
	 * @param fileInfo
	 * @return
	 */
	private StorageInfo uploadFileByFragmentFromStart(String filePath, NameValuePair[] fileInfo) {
		byte[] file = getFileBytes(filePath);
		String exeName = getExtName(filePath);

		StorageInfo info = null;
		// 待上传目标文件实际大小
		int targetFilesize = file.length;
		// 已经上传的部分文件的大小
		int fileSize = 0;
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] results = client.upload_appender_file(file, 0, fragmentSize, exeName, fileInfo);
			if (results != null) {
				info = new StorageInfo(results);
				log.info("上传文件{}分片{}成功", info, fileSize);
				fileSize += fragmentSize;
			}
			while (targetFilesize - fileSize >= fragmentSize) {
				int errorno = client.append_file(info.getGroupName(), info.getFileName(), file, fileSize, fragmentSize);
				if (errorno != 0) {
					throw new MyException("上传appender file失败");
				}
				log.info("上传文件{}分片{}成功", info, fileSize);
				fileSize += fragmentSize;
			}
			int errorno = client.append_file(info.getGroupName(), info.getFileName(), file, fileSize,
					targetFilesize - fileSize);
			if (errorno != 0) {
				throw new MyException("上传appender file失败");
			}
			log.info("上传文件{}最后一个分片成功", info);
			return info;
		} catch (IOException | MyException e) {
			int uploadedFileSize = getUploadedFileSize(info);
			FileBroken broken = new FileBroken(info.getFileId(), filePath, uploadedFileSize, targetFilesize, new Date(),
					e.getMessage());
			dao.add(broken);
			log.error("上传文件{}失败，保存断点{}", filePath, broken);
			throw new RuntimeException("上传文件失败，请待会重试");
		}
	}

	/**
	 * 从断点处开始上传文件，若发生异常更新断点，若上传完毕则删除该断点
	 * 
	 * @param file
	 * @param fileBroken
	 * @return
	 */
	private StorageInfo uploadFileByFragmentFromBroken(byte[] file, FileBroken fileBroken) {
		StorageInfo info = StorageInfo.splitFileId(fileBroken.getFileId());
		String filePath = fileBroken.getLocalFilePath();
		// 待上传目标文件实际大小
		int targetFilesize = file.length;
		// 已经上传的部分文件的大小
		int fileSize = fileBroken.getFileSize();
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			while (targetFilesize - fileSize >= fragmentSize) {
				int errorno = client.append_file(info.getGroupName(), info.getFileName(), file, fileSize, fragmentSize);
				if (errorno != 0) {
					throw new MyException("上传appender file失败");
				}
				log.info("上传文件{}分片{}成功", info, fileSize);
				fileSize += fragmentSize;
			}
			int errorno = client.append_file(info.getGroupName(), info.getFileName(), file, fileSize,
					targetFilesize - fileSize);
			if (errorno != 0) {
				throw new MyException("上传appender file失败");
			}
			log.info("上传文件{}最后一个分片成功", info);
			// 文件上传完毕后删除断点信息
			dao.delete(fileBroken.getFileId());
			return info;
		} catch (IOException | MyException e) {
			int uploadedFileSize = getUploadedFileSize(info);
			FileBroken broken = new FileBroken(info.getFileId(), filePath, uploadedFileSize, targetFilesize, new Date(),
					e.getMessage());
			dao.update(broken);
			log.error("上传文件{}失败，更新断点{}", filePath, broken);
			throw new RuntimeException("上传文件失败，请待会重试", e);
		}
	}

	
	/**
	 * 多线程分片上传文件
	 * 
	 * @param fileInfo
	 * @param localFilePath
	 * @return
	 */
	public StorageInfo uploadFileByMultiThread(NameValuePair[] fileInfo, String localFilePath) {
		localFilePath = FastDFSUtill.getFilePath(localFilePath);
		byte[] file = getFileBytes(localFilePath);
		int targetFileSize = file.length;
		if (targetFileSize < threadFragmentSize) {
			return uploadFileByFragment(fileInfo, localFilePath);
		}

		// 上传空文件取得文件存储位置
		String exeName = getExtName(localFilePath);
		fileInfo = addMd5(fileInfo, localFilePath);
		StorageInfo info = null;
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			String[] results = client.upload_appender_file(new byte[] {}, exeName, fileInfo);
			if (results != null) {
				info = new StorageInfo(results);
			} else {
				throw new MyException("上传空文件失败");
			}
		} catch (IOException | MyException e) {
			log.error("上传空文件失败", e);
			throw new RuntimeException("上传空文件失败", e);
		}

		// 线程数量，每个线程上传会报异常Software caused connection abort: socket write error
		int threadSum = targetFileSize % fragmentSize == 0 ? targetFileSize / fragmentSize
				: targetFileSize / fragmentSize + 1;

		// 初始化线程池,多个线程同时上传文件会报
//		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		ExecutorService threadPool = Executors.newSingleThreadExecutor();

		// 开启个下载线程
		for (int i = 1; i <= threadSum; i++) {
			threadPool.execute(getUploadThread(info, i, threadSum, file, false));
		}

		double uploadedFileSize=0;
		// 主线程间隔200毫秒查看文件上传进度并监控各子线程运行状况
		while (uploadedFileSize < targetFileSize) {
			if (!run) {
				threadPool.shutdown();
				log.info("关闭线程池，终止（取消）文件上传");
				break;
			}

			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				log.error("主线程休眠失败", e);
				throw new RuntimeException("主线程休眠失败");
			}
            uploadedFileSize=getUploadedFileSize(info);
			double uploaded =(uploadedFileSize/targetFileSize)*100.0;
			log.info("已经上传完成%{}", uploaded);

			List<FileBroken> brokens = dao.getErrorThreads(info.getFileId());

			// 重启上传失败的线程
			if (!brokens.isEmpty()) {
				for (FileBroken broken : brokens) {
					threadPool.execute(getUploadThread(info, broken.getThreadNum(), threadSum, file, true));
				}
			}
		}
		threadPool.shutdown();
		return info;
	}

	/**
	 * 获取文件上传的Runnable 各线程分别上传各自所负责的文件字节范围，若出现异常则记入数据库，由主线程负责定时间隔查询数据库，根据数据库
	 * 中是否存在失败的数据库记录重新开启该线程，直到该线程结束上传成功后再删除该数据。主线程在监控各子线程的运行状况
	 * 的同时也会从服务器端查询总的文件上传记录，直到所有的文件上传结束
	 * 
	 * @param info
	 *            文件存储位置
	 * @param threadNum
	 *            线程编号
	 * @param threadSum
	 *            总的线程数
	 * @param file
	 *            待上传文件的字节数组
	 * @return
	 */

	private Runnable getUploadThread(StorageInfo info, int threadNum, int threadSum, byte[] file, boolean retry) {
		Runnable uploadThread = new Runnable() {
			
			@Override
			public void run() {
				// 本线程上传文件到服务器上，写入服务器端文件的起始位置
				int offset = (threadNum - 1) * fragmentSize;
				byte[] content = getFileBytes(file, threadNum, threadSum);
				try {
					StorageClient1 client=FastDFSUtill.getStorageClient1();
					int errorno = client.modify_file(info.getGroupName(), info.getFileName(), offset, content);
					if (errorno != 0) {
						throw new MyException("上传文件失败，起始位置:" + offset);
					}
					log.info("{}线程上传文件成功，起始位置：{}", threadNum, offset);
					// 如果重新启动该线程且上传成功的情况下则删掉数据库中的记录
					if (retry) {
						dao.delete(info.getFileId());
					}
				} catch (IOException | MyException e) {
					log.error("上传文件{}失败，起始位置：{}", info, offset);
					FileBroken broken = new FileBroken(info.getFileId(), threadNum, offset, new Date(), e.getMessage()," ");
					dao.add(broken);
					throw new RuntimeException("上传文件失败，起始位置：" + offset);
				}
			}
		};
		return uploadThread;
	}

	/**
	 * 删除文件
	 * 
	 * @param storageInfo
	 * @return
	 */
	public boolean delete(StorageInfo storageInfo) {
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			int errorno = client.delete_file(storageInfo.getGroupName(), storageInfo.getFileName());
			if (errorno == 0) {
				log.info("删除文件{}成功", storageInfo);
				return true;
			} else {
				log.info("删除文件{}失败", storageInfo);
			}
		} catch (IOException | MyException e) {
			log.info("删除文件{}失败", storageInfo, e);
			throw new RuntimeException("删除文件失败", e);
		}
		return false;
	}

	/**
	 * 下载文件
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	public boolean download(StorageInfo storageInfo, String localFilePath) {
		// localFilePath=FastDFSUtill.getFilePath(localFilePath);
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			// 如果查找的文件不存在不会抛异常而是返回一个非0值
			int success = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), localFilePath);
			if (success == 0) {
				log.info("下载文件{}至{}位置成功", storageInfo, localFilePath);
				return checkMd5(storageInfo, localFilePath);
			} else {
				log.info("待下载的文件{}不存在", storageInfo);
			}
		} catch (IOException | MyException e) {
			log.error("下载文件{}失败", storageInfo, e);
			throw new RuntimeException("下载文件失败", e);
		}
		return false;
	}

	/**
	 * 通过回调函数下载文件
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	public boolean downloadByCallBack(StorageInfo storageInfo, String localFilePath,DownloadCallback callback) {
		// localFilePath=FastDFSUtill.getFilePath(localFilePath);
		try {
//			DownloadCallback callback = new DownloadFileWriter(localFilePath);
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			int success = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), callback);
			if (success == 0) {
				log.info("下载文件{}至{}位置成功", storageInfo, localFilePath);
				return checkMd5(storageInfo, localFilePath);
			} else {
				log.info("待下载的文件{}不存在", storageInfo);
			}
		} catch (IOException | MyException e) {
			log.error("下载文件{}失败", storageInfo, e);
			throw new RuntimeException("下载文件失败", e);
		}
		return false;
	}

	/**
	 * 分片下载
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	public boolean downloadByFragment(StorageInfo storageInfo, String localFilePath) {
		int targetFileSize = getUploadedFileSize(storageInfo);
		if (targetFileSize < fragmentSize) {
			return download(storageInfo, localFilePath);
		}
		// localFilePath=FastDFSUtill.getFilePath(localFilePath);
		FileBroken broken = dao.get(localFilePath);
		if (broken != null) {
			return downloadByFragmentFromBroken(storageInfo, broken);
		} else {
			return downloadByFragmentFromStart(storageInfo, localFilePath);
		}
	}

	/**
	 * 从头开始分片下载
	 * 
	 * @param storageInfo
	 * @param localFilePath
	 * @return
	 */
	@SuppressWarnings("resource")
	private boolean downloadByFragmentFromStart(StorageInfo storageInfo, String localFilePath) {
		// 待下载的目标文件大小
		int targetFileSize = getUploadedFileSize(storageInfo);
		// 已经下载的文件大小
		int fileSize = 0;
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(localFilePath, "rw");
			file.seek(0);
		} catch (IOException e) {
			log.error("文件{}没有找到", localFilePath, e);
			throw new RuntimeException("文件没有找到", e);
		}

		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			while (targetFileSize - fileSize >= fragmentSize) {
				byte[] results = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), fileSize,
						fragmentSize);
				if (results.length != fragmentSize) {
					throw new MyException("下载文件受到损坏");
				} else {
					file.write(results);
					fileSize += fragmentSize;
					file.seek(fileSize);
					log.info("下载文件{}分片{}成功", storageInfo, fileSize);
				}
			}

			byte[] results = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), fileSize, 0);
			if (results.length != (targetFileSize - fileSize)) {
				throw new MyException("下载文件受到损坏");
			} else {
				file.write(results);
				log.info("下载文件{}最后一个分片{}成功", storageInfo, fileSize);
				return checkMd5(storageInfo, localFilePath);
			}
		} catch (IOException | MyException e) {
			log.error("下载文件{}失败", storageInfo, e);
			FileBroken broken = new FileBroken(storageInfo.getFileId(), localFilePath, fileSize, targetFileSize,
					new Date(), e.getMessage());
			dao.add(broken);
			throw new RuntimeException("下载文件失败", e);
		}
	}

	/**
	 * 从断点处分片下载
	 * 
	 * @param storageInfo
	 * @param broken
	 * @return
	 */
	private boolean downloadByFragmentFromBroken(StorageInfo storageInfo, FileBroken broken) {
		// 本地存放目录
		String localFilePath = broken.getLocalFilePath();
		// 待下载的目标文件大小
		int targetFileSize = broken.getTargetFileSize();
		// 已经下载的文件大小
		int fileSize = broken.getFileSize();
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(localFilePath, "rw");
			file.seek(fileSize);
		} catch (IOException e) {
			log.error("文件{}没有找到", localFilePath, e);
			throw new RuntimeException("文件没有找到", e);
		}

		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			while (targetFileSize - fileSize >= fragmentSize) {
				byte[] results = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), fileSize,
						fragmentSize);
				if (results.length != fragmentSize) {
					throw new MyException("下载文件受到损坏");
				} else {
					file.write(results);
					file.seek(fileSize);
					fileSize += fragmentSize;
					log.info("下载文件{}分片{}成功", storageInfo, fileSize);
				}
			}

			byte[] results = client.download_file(storageInfo.getGroupName(), storageInfo.getFileName(), fileSize, 0);
			if (results.length != (targetFileSize - fileSize)) {
				throw new MyException("下载文件受到损坏");
			} else {
				file.write(results);
				dao.delete(storageInfo.getFileId());
				log.info("下载文件{}最后一个分片{}成功", storageInfo, fileSize);
				return checkMd5(storageInfo, localFilePath);
			}
		} catch (IOException | MyException e) {
			log.error("下载文件{}失败", storageInfo, e);
			FileBroken broken2 = new FileBroken(storageInfo.getFileId(), localFilePath, fileSize, targetFileSize,
					new Date(), e.getMessage());
			dao.update(broken);
			throw new RuntimeException("下载文件失败", e);
		}
	}

	public boolean downloadByMultiThread(StorageInfo info, String localFilePath) {
		int targetFileSize = getUploadedFileSize(info);
		if (targetFileSize < threadFragmentSize) {
			downloadByFragment(info, localFilePath);
		}

		// 线程数量，每个线程上传
		int threadSum = targetFileSize % fragmentSize == 0 ? targetFileSize / fragmentSize
				: targetFileSize / fragmentSize + 1;
        int lastFragmentSize=targetFileSize-fragmentSize*(threadSum-1);
		
		// 初始化线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(threadSum);
	
		RandomAccessFile file=null;
		try {
			file=new RandomAccessFile(localFilePath, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
		//开启各下载线程
		for(int i=1;i<=threadSum;i++){
			threadPool.execute(getDownloadThread(info, threadSum, i, file, lastFragmentSize,false));
		}
		
		double downloadedFileSize=0;
		while(downloadedFileSize<targetFileSize){
			if(!run){
				threadPool.shutdown();
				log.info("关闭线程池，终止（取消）文件{}下载",info);
				break;
			}
			
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				log.error("主线程休眠失败",e);
				throw new RuntimeException("主线程休眠失败",e);
			}
			
			 try {
				downloadedFileSize=(int) file.length();
			} catch (IOException e) {
				log.error("获取文件下载进度失败",e);
				throw new RuntimeException("获取文件下载进度失败",e);
			}
			 
				double downloaded = (downloadedFileSize * 100 )/ targetFileSize;
				log.info("已经下载完成%{}", downloaded);

				List<FileBroken> brokens = dao.getErrorThreads(info.getFileId());

				// 重启下载失败的线程
				if (!brokens.isEmpty()) {
					for (FileBroken broken : brokens) {
						threadPool.execute(getDownloadThread(info, broken.getThreadNum(), threadSum, file,lastFragmentSize, true));
					}
				}
		}
        log.info("多线程下载文件{}至位置{}成功",info,localFilePath);		
        threadPool.shutdown();
        return checkMd5(info, localFilePath);
		
	}

	private Runnable getDownloadThread(StorageInfo info,int threadSum,int threadNum,RandomAccessFile file,int lastFragmentSize,boolean retry){
		Runnable downloadThread=new Runnable() {
			
			@Override
			public void run() {
				//下载文件的起始位置
				int offset=(threadNum-1)*fragmentSize;
				
	            StorageClient1 client=FastDFSUtill.getStorageClient1();			
                byte[] results=null;
                try {
                	
                if(threadNum!=threadSum){
					results=client.download_file(info.getGroupName(), info.getFileName(), offset, fragmentSize);
				}else{
					results=client.download_file(info.getGroupName(), info.getFileName(), offset, 0);
				}
                
                //判断下载文件是否成功
                if(results.length!=fragmentSize && results.length!=lastFragmentSize){
                	log.error("下载的文件受损，受损文件大小{}，正常文件大小{}",results.length,fragmentSize);
                	throw new MyException("下载的文件受到损坏");
                }else{
                	log.info("{}号线程下载文件分片{}成功",threadNum,offset);
                	if(retry){
                		dao.delete(info.getFileId());
                	}
                }
                } catch (IOException | MyException e) {
					log.error("{}线程下载文件分片{}失败",threadNum,offset);
					FileBroken broken=new FileBroken(info.getFileId(), threadNum, offset, new Date(), e.getMessage()," ");
					dao.add(broken);
					throw new RuntimeException("子线程下载文件失败",e);
				}
                
                try {
                	//避免多个线程同时写入文件导致并发问题
                	synchronized(file){
                	file.seek(offset);
					file.write(results);
                	}
				} catch (IOException e) {
					log.error("文件写入失败",e);
					FileBroken broken=new FileBroken(info.getFileId(), threadNum, offset, new Date(), e.getMessage()," ");
					dao.add(broken);
					throw new RuntimeException("子线程写入文件失败",e);
				}
			}
			
		};
		return downloadThread;
	}
	
	/**
	 * 根据文件路径获取文件名后缀
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	private String getExtName(String filePath) {
		String exeName = filePath.substring(filePath.lastIndexOf(".") + 1);
		return exeName;
	}

	/**
	 * 将文件内容转换成byte数组
	 * 
	 * @param filePath
	 * @return
	 */
	private byte[] getFileBytes(String filePath) {
		try (FileInputStream in = new FileInputStream(filePath)) {
			byte[] content = new byte[in.available()];
			in.read(content);
			return content;
		} catch (Exception e) {
			log.error("读取上传文件{}失败", filePath, e);
			throw new RuntimeException("读取上传文件失败", e);
		}

	}

	/**
	 * 获取指定文件从起始位offset开始fragmentSize大小的字节数组
	 * 
	 * @param filePath
	 * @param offset
	 * @return
	 */
	private byte[] getFileBytes(byte[] file, int threadNum, int threadSum) {
		// 计算每个线程上传的文件大小
		int fileSize = 0;
		if (threadNum < threadSum) {
			fileSize = fragmentSize;
		} else {
			fileSize = file.length - fragmentSize * (threadSum - 1);
		}

		// 计算每个线程上传文件的起始位置
		int offset = (threadNum - 1) * fragmentSize;

		// 计算每个线程上传的字节数组
		byte[] content = new byte[fileSize];
		content = Arrays.copyOfRange(file, offset, offset + fileSize);

		return content;

	}

	/**
	 * 获取已经上传的文件大小
	 * 
	 * @param info
	 * @return
	 */
	private int getUploadedFileSize(StorageInfo info) {
		FileInfo fileInfo;
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			fileInfo = client.get_file_info(info.getGroupName(), info.getFileName());
			if (fileInfo != null) {
				return new Long(fileInfo.getFileSize()).intValue();
			}
		} catch (IOException | MyException e) {
			log.error("获取文件信息{}失败", info, e);
			throw new RuntimeException("获取文件信息失败", e);
		}
		return 0;
	}

	/**
	 * 获取线程编号列表
	 * 
	 * @param threadSum
	 * @return
	 */
	private ArrayList<Integer> getThreadNumList(int threadSum) {
		ArrayList<Integer> threadNums = new ArrayList<Integer>();
		int i = 1;
		while (i <= threadSum) {
			threadNums.add(i);
			i++;
		}
		return threadNums;
	}

	/**
	 * 对待上传的文件的文件属性中添加MD5效验字符串
	 * 
	 * @param fileInfo
	 * @param filePath
	 * @return
	 */
	private NameValuePair[] addMd5(NameValuePair[] fileInfo, String filePath) {
		String md5 = MD5Util.getFileMD5String(filePath);
		NameValuePair[] pairs = Arrays.copyOf(fileInfo, fileInfo.length + 1);
		pairs[fileInfo.length] = new NameValuePair("md5", md5);
		return pairs;
	}

	/**
	 * 对已下载的文件检查文件是否收到损坏
	 * 
	 * @param info
	 * @param filePath
	 * @return
	 */
	private boolean checkMd5(StorageInfo info, String filePath) {
		String md5 = MD5Util.getFileMD5String(filePath);
		String srcMd5 = getMd5(info);
		return md5.equals(srcMd5);
	}

	/**
	 * 获取目标文件的MD5
	 * 
	 * @param info
	 * @return
	 */
	private String getMd5(StorageInfo info) {
		String md5 = "";
		try {
			StorageClient1 client=FastDFSUtill.getStorageClient1();
			NameValuePair[] fileInfo = client.get_metadata(info.getGroupName(), info.getFileName());
			for (NameValuePair pair : fileInfo) {
				if (pair.getName().equals("md5")) {
					md5 = pair.getValue();
					log.info("获取待下载文件 {}的MD5: {}成功", info, md5);
					break;
				}
			}
			return md5;
		} catch (IOException | MyException e) {
			log.error("获取文件{}的属性信息失败", info, e);
			throw new RuntimeException("获取文件属性信息失败", e);
		}
	}
	
	public static void main(String[] args) {
	    double a=3;
	    int c=4;
		double b=(a/c)*100.0;
		System.out.println(b);
	}
	
}
