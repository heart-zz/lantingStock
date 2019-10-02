package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 角色权限表
 * @author yjg
 *
 */
@Table("roleauth")
public class RoleAuth extends IdEntity{

	/**
	 * 角色名称
	 */
	@Column
	private long roleId;
	
	//权限编码
	@Column
	@ColDefine(width=256)
	private String authCode;
	/**
	 * 获取 角色ID
	 * @return roleId角色ID
	 */
	public long getRoleId() {
		return roleId;
	}
	/**
	 * 设置 角色ID
	 * @param roleId角色ID
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
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
