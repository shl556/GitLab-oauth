package com.h3c.DFSDao;

import java.util.List;

import com.h3c.Util.FileBroken;

public interface FileBrokenDao {
	
    /** 新增断点
     * @param fileBroken
     * @return
     */
    boolean add(FileBroken fileBroken);
    
    /** 更新断点状态
     * @param fileBroken
     * @return
     */
    boolean update(FileBroken fileBroken);
    
    /** 删除断点信息
     * @param fileBroken
     * @return
     */
    boolean delete(String fileId);
    
    /** 查询指定文件是否存在未上传完毕的断点
     * @param fileId
     * @return
     */
    FileBroken get(String filePath);
    
    /** 检查指定线程的文件是否上传完毕
     * @param fileId
     * @param threadNum
     * @return
     */
    List<FileBroken> getErrorThreads(String fileId);
}
