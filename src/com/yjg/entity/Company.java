package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

@Table("company")
public class Company extends IdEntity{
	
	/**
	 * 公司名称
	 */
	@ColDefine(type=ColType.VARCHAR,width=255)
	@Column
	private String name;
	
	/**
	 * 公司编号
	 */
	@Column
	private String no;
	
	/**
	 * 公司地址
	 */
	@ColDefine(type=ColType.VARCHAR,width=255)
	@Column
	private String address;
	
	/**
	 * 联系电话
	 */
	@Column
	private String tel;
	/**
	 * 简介
	 */
	@Column
	private String content;
	/**
	 * 管理人
	 */
	@Column
	private String admin;
	/**
	 * 管理人id
	 */
	@Column
	private long adminId;
	
	/**
	 * 管理人身份证号
	 */
	@Column
	private String userID;
	/**
	 * 管理人电话		
	 */
	@Column
	private String adminTel;
	
	/**
	 * 公司状态
	 * 0  服务维护中
	 * 1  服务中  
	 * -1 服务停止
	 */
	@Column
	private int comstatus;
	/**
	 * 获取公司名
	 * @return name 公司名
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置公司名
	 * @param name 公司名
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取公司编号
	 * @return no 公司编号
	 */
	public String getNo() {
		return no;
	}
	
	/**
	 * 设置公司编号
	 * @param no 公司编号
	 */
	public void setNo(String no) {
		this.no = no;
	}
	
	/**
	 * 获取公司地址
	 * @return address 公司地址
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * 设置公司地址
	 * @param address 公司地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public long getAdminId() {
		return adminId;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}

	public String getAdminTel() {
		return adminTel;
	}

	public void setAdminTel(String adminTel) {
		this.adminTel = adminTel;
	}

	public int getComstatus() {
		return comstatus;
	}
	/**
	 * 公司状态
	 * 0  服务维护中
	 * 1  服务中  
	 * -1 服务停止
	 */
	public void setComstatus(int comstatus) {
		this.comstatus = comstatus;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
}

