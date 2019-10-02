package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 权限信息表
 * @author yjg
 *
 */
@Table("auth")
public class Auth extends IdEntity{

	/**
	 * 父权限编码
	 */
	@Column
	@ColDefine(width=256)
	private String fatherCode;
	/**
	 * 父权限名称
	 */
	@Column
	@ColDefine(width=256)
	private String father;
	
	/**
	 * 权限名称
	 */
	@Column
	@ColDefine(width=256)
	private String auth;
	
	/**
	 * 权限编码<br/>
	 */
	@Column
	@ColDefine(width=256)
	private String authCode;


	/**
	 * 获取 父权限名称
	 * @return father父权限名称
	 */
	public String getFather() {
		return father;
	}

	/**
	 * 设置 父权限名称
	 * @param father父权限名称
	 */
	public void setFather(String father) {
		this.father = father;
	}

	/**
	 * 获取 权限名称
	 * @return auth权限名称
	 */
	public String getAuth() {
		return auth;
	}

	/**
	 * 设置 权限名称
	 * @param auth权限名称
	 */
	public void setAuth(String auth) {
		this.auth = auth;
	}

	/**
	 * 获取 权限编码
	 * @return authCode权限编码
	 */
	public String getAuthCode() {
		return authCode;
	}

	/**
	 * 设置 权限编码<br>
	 * @param authCode权限编码
	 */
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	/**
	 * 获取 父权限编码
	 * @return fatherCode父权限编码
	 */
	public String getFatherCode() {
		return fatherCode;
	}

	/**
	 * 设置 父权限编码
	 * @param fatherCode父权限编码
	 */
	public void setFatherCode(String fatherCode) {
		this.fatherCode = fatherCode;
	}
}
