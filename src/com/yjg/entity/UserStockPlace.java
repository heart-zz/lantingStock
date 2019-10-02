package com.yjg.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 用户仓库权限表
 * @author yjg
 *
 */
@Table("st_userStockPlace")
public class UserStockPlace extends IdEntity{

	//用户id
	@Column
	private long userId;
	//用户
	@Column
	private String username;
	//仓库id
	@Column
	private long stockplaceId;
	//仓库名称
	@Column
	private String stockplace;
	/**
	 * 获取  用户id
	 * @return userId
	 */
	public long getUserId() {
		return userId;
	}
	
	/**
	 * 设置  用户id
	 * @param userId
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	/**
	 * 获取  用户
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 设置  用户
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * 获取  仓库id
	 * @return stockplaceId
	 */
	public long getStockplaceId() {
		return stockplaceId;
	}
	
	/**
	 * 设置  仓库id
	 * @param stockplaceId
	 */
	public void setStockplaceId(long stockplaceId) {
		this.stockplaceId = stockplaceId;
	}
	
	/**
	 * 获取  仓库名称
	 * @return stockplace
	 */
	public String getStockplace() {
		return stockplace;
	}
	
	/**
	 * 设置  仓库名称
	 * @param stockplace
	 */
	public void setStockplace(String stockplace) {
		this.stockplace = stockplace;
	}
	
}
