package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 用户权限关系表
 * @author yjg
 *
 */
@Table("userAuth")
public class UserAuth extends IdEntity{

	/**
	 * 用户ID
	 */
	@Column
	private long userId;
	
	//权限编码
	@Column
	@ColDefine(width=256)
	private String authCode;

	/**
	 * 获取 用户ID
	 * @return userId用户ID
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * 设置 用户ID
	 * @param userId用户ID
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * 获取  权限编码
	 * @return authCode
	 */
	public String getAuthCode() {
		return authCode;
	}
	

	/**
	 * 设置  权限编码
	 * @param authCode
	 */
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
	
}
