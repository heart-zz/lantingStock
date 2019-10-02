package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 业务逻辑日志表（记录业务逻辑中重要的操作内容，内置时间）
 * @author yjg
 *
 */
@Table("businesslog")
public class BusinessLog extends IdEntity{
	
	public BusinessLog() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 带参构造函数
	 * 业务逻辑日志表（记录业务逻辑中重要的操作内容，内置时间）
	 * @param _content 业务内容
	 * @param _userId 操作人ID
	 * @param _username 操作人姓名
	 * @param _functionCode 功能代码
	 */
	public BusinessLog(String _content,User user,String _functionCode,int _level) {
		// TODO Auto-generated constructor stub
		content=_content;
		if(user!=null){
			userId=user.getId();
			username=user.getUsername();
		}
		else{
			userId=0;
			username="系统";
			
		}		
		functionCode=_functionCode;
		level=_level;		
		setAuthor("系统");
		setAuthorId(0);
		setDateCreater(new Date());
	}

	//内容
	@ColDefine(type=ColType.TEXT)
	@Column
	private String content;	
	
	//操作人ID
	@Column
	private long userId;
	//操作人姓名
	@Column
	private String username;
	
	//事件级别
	@Column
	private int level;
	//对应的模块代码
	@Column
	private String functionCode;
	
	//date
	@Column
	private Date date;
	
	/**
	 * 获取  内容
	 * @return content
	 */
	public String getContent() {
		return content;
	}
	

	/**
	 * 设置  内容
	 * @param contentcontent
	 */
	public void setContent(String content) {
		this.content = content;
	}
	

	/**
	 * 获取  操作人ID
	 * @return userId
	 */
	public long getUserId() {
		return userId;
	}
	

	/**
	 * 设置  操作人ID
	 * @param userIduserId
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	

	/**
	 * 获取  操作人姓名
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	

	/**
	 * 设置  操作人姓名
	 * @param usernameusername
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	

	/**
	 * 获取  事件级别
	 * @return level
	 */
	public int getLevel() {
		return level;
	}
	

	/**
	 * 设置  事件级别
	 * @param levellevel
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	

	/**
	 * 获取  对应的模块代码
	 * @return functionCode
	 */
	public String getFunctionCode() {
		return functionCode;
	}
	

	/**
	 * 设置  对应的模块代码
	 * @param functionCodefunctionCode
	 */
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
