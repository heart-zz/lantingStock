package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 采购单
 * @author yjg
 *
 */
@Table("st_purchase")
public class Purchase extends IdEntity{

	//采购单号
	@Column
	@ColDefine(width=64)
	private String appNo;
	
	//申请人id
	@Column
	private long appUserId;
	
	//申请人
	@Column
	private String appUser;
	//状态  0=待审核,-1=审核不通过,1=已审核,-9=入库失败,6=部分入库,9=已入库
	
	
	@Column
	private int status;
	
	//状态标签 非数据库字段
	private String statusInfo;
	
	@Column
	private String sfType;
	
	//申请日期
	@Column
	private Date appDate;
	
	//审核人id
	@Column
	private long verifyUserId;
	
	//审核人
	@Column
	private String verifyUsername;
	
	//审核日期
	@Column
	private Date verifyDate;
	
	//采购计划仓库Id
	@Column
	private long stockplaceId;
	
	//采购计划仓库
	@Column
	private String stockplace;
	
	//审核不通过的回执信息
	@Column
	private String hint;

	/**
	 * 获取  采购单号
	 * @return appNo
	 */
	public String getAppNo() {
		return appNo;
	}
	

	/**
	 * 设置  采购单号
	 * @param appNo
	 */
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	

	/**
	 * 获取  申请人id
	 * @return appUserId
	 */
	public long getAppUserId() {
		return appUserId;
	}
	

	/**
	 * 设置  申请人id
	 * @param appUserId
	 */
	public void setAppUserId(long appUserId) {
		this.appUserId = appUserId;
	}
	

	/**
	 * 获取  申请人
	 * @return appUsername
	 */
	public String getAppUser() {
		return appUser;
	}
	

	/**
	 * 设置  申请人
	 * @param appUsername
	 */
	public void setAppUser(String appUser) {
		this.appUser = appUser;
	}
	

	/**
	 * 获取  状态  0=待审核,-1=审核不通过,1=已审核,-9=入库失败,6=部分入库,9=已入库
	 * @return status
	 */
	public int getStatus() {
		return status;
	}
	

	/**
	 * 设置  状态  0=待审核,-1=审核不通过,1=已审核,-9=入库失败,6=部分入库,9=已入库
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	

	/**
	 * 获取  状态标签 非数据库字段
	 * @return statusInfo
	 */
	public String getStatusInfo() {
		return statusInfo;
	}
	

	/**
	 * 设置  状态标签 非数据库字段
	 * @param statusInfo
	 */
	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
	}
	

	/**
	 * 获取  申请日期
	 * @return appDate
	 */
	public Date getAppDate() {
		return appDate;
	}
	

	/**
	 * 设置  申请日期
	 * @param appDate
	 */
	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}
	

	/**
	 * 获取  审核人id
	 * @return verifyUserId
	 */
	public long getVerifyUserId() {
		return verifyUserId;
	}
	

	/**
	 * 设置  审核人id
	 * @param verifyUserId
	 */
	public void setVerifyUserId(long verifyUserId) {
		this.verifyUserId = verifyUserId;
	}
	

	/**
	 * 获取  审核人
	 * @return verifyUsername
	 */
	public String getVerifyUsername() {
		return verifyUsername;
	}
	

	/**
	 * 设置  审核人
	 * @param verifyUsername
	 */
	public void setVerifyUsername(String verifyUsername) {
		this.verifyUsername = verifyUsername;
	}
	

	/**
	 * 获取  审核日期
	 * @return verifyDate
	 */
	public Date getVerifyDate() {
		return verifyDate;
	}
	

	/**
	 * 设置  审核日期
	 * @param verifyDate
	 */
	public void setVerifyDate(Date verifyDate) {
		this.verifyDate = verifyDate;
	}


	/**
	 * 获取  采购计划仓库
	 * @return stockplace
	 */
	public String getStockplace() {
		return stockplace;
	}
	


	/**
	 * 设置  采购计划仓库
	 * @param stockplace
	 */
	public void setStockplace(String stockplace) {
		this.stockplace = stockplace;
	}


	public String getHint() {
		return hint;
	}


	public void setHint(String hint) {
		this.hint = hint;
	}


	public long getStockplaceId() {
		return stockplaceId;
	}


	public void setStockplaceId(long stockplaceId) {
		this.stockplaceId = stockplaceId;
	}


	public String getSfType() {
		return sfType;
	}


	public void setSfType(String sfType) {
		this.sfType = sfType;
	}
	
	
}
