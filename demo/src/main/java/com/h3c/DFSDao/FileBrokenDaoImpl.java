package com.h3c.DFSDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.h3c.Util.FileBroken;

@Repository
@Lazy(false)
public class FileBrokenDaoImpl implements FileBrokenDao{
    private Logger log=LoggerFactory.getLogger(getClass());
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	@Override
	public boolean add(FileBroken fileBroken) {
		Assert.notNull(fileBroken);
		Map<String, Object> params=new HashMap<>();
		params.put("fileId", fileBroken.getFileId());
		params.put("threadNum",0);
		params.put("localFilePath", fileBroken.getLocalFilePath());
		params.put("fileSize", fileBroken.getFileSize());
		params.put("targetFileSize", fileBroken.getTargetFileSize());
		params.put("stopTime", fileBroken.getStopTime());
		params.put("errorMessage", fileBroken.getErrorMessage());
		String sql="insert into filebroken values(:fileId,:threadNum,:localFilePath,:fileSize,:targetFileSize,:stopTime,:errorMessage)";
		template.update(sql, params);
		log.info("插入断点信息成功,{}"+fileBroken);
		return true;
	}

	@Override
	public boolean update(FileBroken fileBroken) {
		Assert.notNull(fileBroken);
		Map<String, Object> params=new HashMap<>();
		params.put("fileId", fileBroken.getFileId());
		params.put("localFilePath", fileBroken.getLocalFilePath());
		params.put("fileSize", fileBroken.getFileSize());
		params.put("targetFileSize", fileBroken.getTargetFileSize());
		params.put("stopTime", fileBroken.getStopTime());
		params.put("errorMessage", fileBroken.getErrorMessage());
		String sql="update filebroken set filesize=:fileSize,stopTime=:stopTime,errorMessage=:errorMessage where fileid=:fileId";
		template.update(sql, params);
		log.info("更新断点信息成功,{}",fileBroken);
		return true;
	}

	@Override
	public boolean delete(String fileId) {
		Assert.notNull(fileId);;
		Map<String, Object> params=new HashMap<>();
		params.put("fileId", fileId);
		String sql ="delete from filebroken where fileid =:fileId";
		template.update(sql,params);
		log.info("删除断点{}信息成功",fileId);
		return true;
	}

	@Override
	public FileBroken get(String filePath) {
		Assert.notNull(filePath);
		Map<String, Object> params=new HashMap<>();
		params.put("localfilePath", filePath);
		String sql="select * from filebroken where localfilepath=:localfilePath";
		FileBroken broken=template.query(sql,params,resultSetExtractor());
		if (broken!=null) {
			log.info("查找断点信息成功，{}",broken);
			if(broken.getFileSize()<broken.getTargetFileSize()){
				return broken;
			}else {
				System.out.println("该文件"+filePath+"中的断点已经处理完毕");
				return null;
			}
		}else {
			log.info("文件{}的断点不存在",filePath);
		}
		return broken;
	}
	
	private ResultSetExtractor<FileBroken> resultSetExtractor(){
		ResultSetExtractor<FileBroken> extractor=new ResultSetExtractor<FileBroken>() {
	
			@Override
			public FileBroken extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs==null) {
					return null;
				}
				if(rs.next()){
				String fileId=rs.getString("fileid");
				String localFilePath=rs.getString("localfilepath");
				int fileSize=rs.getInt("filesize");
				int targetFileSize=rs.getInt("targetfilesize");
				Date stopTime=rs.getDate("stoptime");
				String errorMessage=rs.getString("errormessage");
				
				FileBroken broken =new FileBroken(fileId, localFilePath, fileSize, targetFileSize, stopTime, errorMessage);
				return broken;
				}
	            return null;				
			}
		};
		return extractor;
	}

	@Override
	public List<FileBroken> getErrorThreads(String fileId) {
		Assert.notNull(fileId);
		Map<String ,Object> params=new HashMap<>();
		params.put("fileId", fileId);
		String sql="select * from filebroken where fileid =:fileId";
		List<FileBroken> brokens=new ArrayList<>();
		brokens=template.query(sql, params, getRowMapper());
		log.info("共查找发生异常的线程{}个",brokens.size());
		return brokens;
	}

	private RowMapper<FileBroken> getRowMapper(){
		RowMapper<FileBroken> mapper=new RowMapper<FileBroken>() {

			@Override
			public FileBroken mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs==null) {
					return null;
				}
				if(rs.next()){
				String fileId=rs.getString("fileid");
				int threadNum=rs.getInt("threadnum");
				int fileSize=rs.getInt("filesize");
				Date stopTime=rs.getDate("stoptime");
				String errorMessage=rs.getString("errormessage");
				String localFilePath=rs.getString("localfilepath");
				FileBroken broken =new FileBroken(fileId, threadNum, fileSize, stopTime, errorMessage,localFilePath);
				return broken;
				}
	            return null;	
			}
		};
		return mapper;
	}
}
