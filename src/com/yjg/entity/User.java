package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 账户信息
 * @author yjg
 *
 */
@Table("user")
public class User extends IdEntity{

	/**
	 * 用户名
	 */
	@ColDefine(type=ColType.VARCHAR,width=255)
	@Column
	private String username;
	
	/**
	 * 密码
	 */
	@ColDefine(type=ColType.VARCHAR,width=255)
	@Column
	private String password;
	
	/**
	 * 身份证号/工号
	 */
	@ColDefine(type=ColType.VARCHAR,width=255)
	@Column
	private String userID;
	
	/**
	 * 上次登录IP
	 */
	@Column
	private String lastIP;
	/**
	 * 上传登录时间
	 */
	@Column
	private Date lastDate;	
	
	/**
	 * 一共登录次数
	 */
	@Column
	private int loginCount;
	/**
	 * 手机号码
	 */
	@Column
	private String phone;
	/**
	 * 账号状态 -1 停用  1 可以使用
	 */
	@Column
	private int status;
	
	@Column
	private long roleId;
	
	/**
	 * 角色名称
	 */
	@Column
	private String roleName;
	
	/**
	 * 邮箱
	 */
	@ColDefine(type=ColType.VARCHAR,width=100)
	private String email;
	
	/**
	 * 获取 用户名
	 * @return username用户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置 用户名
	 * @param username用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取 密码
	 * @return password密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置 密码
	 * @param password密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取 上次登录IP
	 * @return lastIP上次登录IP
	 */
	public String getLastIP() {
		return lastIP;
	}
	/**
	 * 设置 上次登录IP
	 * @param lastIP上次登录IP
	 */
	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}
	/**
	 * 获取 上传登录时间
	 * @return lastDate上传登录时间
	 */
	public Date getLastDate() {
		return lastDate;
	}
	/**
	 * 设置 上传登录时间
	 * @param lastDate上传登录时间
	 */
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	/**
	 * 获取 一共登录次数
	 * @return loginCount一共登录次数
	 */
	public int getLoginCount() {
		return loginCount;
	}
	/**
	 * 设置 一共登录次数
	 * @param loginCount一共登录次数
	 */
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	/**
	 * 获取 手机号码
	 * @return phone手机号码
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置 手机号码
	 * @param phone手机号码
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取 邮箱
	 * @return email邮箱
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 设置 邮箱
	 * @param email邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * 获取  不同角色间用英文逗号分隔
	 * @return roleIds
	 */
	public long getRoleId() {
		return roleId;
	}
	
	/**
	 * 设置  不同角色间用英文逗号分隔
	 * @param roleIds
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	/**
	 * 获取  bare_field_comment
	 * @return roleName
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * 设置  bare_field_comment
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getStatus() {
		return status;
	}
	/**
	 * 账号状态 -1 停用  1 可以使用
	 */
	public void setStatus(int status) {
		this.status = status;
	}	
	
	
}
