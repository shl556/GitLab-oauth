package com.minitwit.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/** 表示查询条件的类
 * @author Administrator
 *
 */
public class Condition {
     private String keyWord;
     private String pubDate;
     private int userId=-1;
     private Map<String, Object> params=new HashMap<>();
     
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
    /** 根据查询条件返回查询语句
     * @return
     */
    public  String   getSql(){
    	StringBuffer sql=new StringBuffer("select a.*,b.* from message a,user b where a.author_id=b.user_id ");
    	int i=0;
    	if(userId!=-1){
//    		 appendWhere(i, sql);
    		 i=1;
    		 params.put("id", userId);
             sql.append(" and  a.author_id=:id ");   		 
    	}
    	if(StringUtils.isNotEmpty(keyWord)){
//    		      appendWhere(i, sql);
    		      i=1;
//    		      params.put("keyWord", keyWord);
    		      sql.append("  and a.text like '%"+keyWord+"%' ");
    	  }
    	if(StringUtils.isNotEmpty(pubDate)){
//    		    appendWhere(i, sql);
    		    params.put("pubDate", getSelectDate(pubDate));
    	    	sql.append( " and a. pub_date> :pubDate");
    	}
    	sql.append(" order by a.pub_date desc");
//    	System.out.println("合成spl语句："+sql);
//    	params.forEach((s,r)->{
//    		 System.out.print(s+": "+ r);
//    	});
    	return sql.toString();
    }
    public  Map<String, Object> getParams(){
    	   return params;
    }
    
    /** 根据i判断是插入where还是and
     * @param i
     * @param stringBuffer
     * @return
     */
    private StringBuffer appendWhere(int i,StringBuffer stringBuffer){
    	 if(i==0){
    		 stringBuffer.append(" where ");
    	 }else{
    		 stringBuffer.append(" and ");
    	 }
    	 return stringBuffer;
    }
    
    /** 将日期选项转换对应的查询日期
     * 1表示最近一周，2表示最近2周，3表示最近一月，4表示最近一年
     * @param i
     * @return
     */
    public  Date  getSelectDate(String pubDate ){
    	   Calendar now=Calendar.getInstance();
    	   switch (pubDate) {
		case "1":
			now.add(Calendar.DAY_OF_MONTH,-7);
			break;
		case "2":
			now.add(Calendar.DAY_OF_MONTH,-14);
			break;
		case "3":
			now.add(Calendar.MONTH,-1);
			break;
		case "4":
			now.add(Calendar.YEAR,-1);
			break;
		default:
			break;
		}
    	return now.getTime();
    }
    
    public static void main(String[] args) {
		   Condition condition=new Condition();
		   System.out.println(DateFormatUtils.format(condition.getSelectDate("1"),"yyyy-MM-dd"));
		   System.out.println(DateFormatUtils.format(condition.getSelectDate("2"),"yyyy-MM-dd"));
		   System.out.println(DateFormatUtils.format(condition.getSelectDate("3"),"yyyy-MM-dd"));
		   System.out.println(DateFormatUtils.format(condition.getSelectDate("4"),"yyyy-MM-dd"));
	}
    
    @Override
    public boolean equals(Object obj) {
    	Condition condition=(Condition)obj;
    	return condition.getKeyWord().equals(this.keyWord)&& condition.getPubDate().equals(this.pubDate)&&condition.getUserId()==this.userId;
    }
}
