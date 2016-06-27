package com.shl;

import java.util.List;

public interface UserService {
    /** 根据用户id查找用户
     * @param id
     * @return
     */
    public User getUser(int id);
    
    /** 检查用户名是否存在
     * @param username
     * @return
     */
    public boolean checkUsername(String username);
    
    /** 更新用户信息
     * @param user
     * @return
     */
    public boolean updateUser(User user);
    
    /** 删除用于信息
     * @param id
     * @return
     */
    public boolean deleteUser(int id );
    
    /** 查询符合条件的用户列表
     * @param username
     * @param age
     * @return
     */
    public List<User> queryUser(String username,int age);
}
