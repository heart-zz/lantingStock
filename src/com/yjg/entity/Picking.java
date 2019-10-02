package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 领（退）料单
 * @author yjg
 *
 */
@Table("st_picking")
public class Picking extends IdEntity{

	//领(退)料单号
	@Column
	private String appNo;
	
	//类型 1=退料单，0=领料单
	@Column
	private int type;
	//原领料单id,退料单有
	@Column
	private long preAppId;
	
	@Column
	private String sfType;
	//原领料单号,退料单有
	@Column
	private String preAppNo;
	//申请人
	@Column
	private String appUser;
	//申请人id
	@Column
	private long appUserId;
	//申请日期
	@Column
	private Date appDate;
	//审核人
	@Column
	private String verifyUser;
	//审核人id
	@Column
	private long verifyUserId;
	//审核日期
	@Column
	private Date verifyDate;
	
	//经办人
	@Column
	private String optUser;
	//经办人Id
	@Column
	private long optUserId;
	//经办日期
	@Column
	private Date optDate;
	
	//领料仓库
	@Column
	private String stockplace;
	//领料仓库id
	@Column
	private long stockplaceId;
	
	//审核不通过的回执信息
	@Column
	private String hint;
	
	//备注
	@Column
	private String remark;
	//状态  0=待审核,-1=审核不通过,1=已审核,3=部分出库,9=已出库
	@Column 
	private int status;
	/**
	 * 获取  领(退)料单号
	 * @return appNo
	 */
	public String getAppNo() {
		return appNo;
	}
	
	/**
	 * 设置  领(退)料单号
	 * @param appNo
	 */
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	
	/**
	 * 获取  类型 1=退料单，0=领料单
	 * @return type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 设置  类型 1=退料单，0=领料单
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * 获取  原领料单id,退料单有
	 * @return preAppId
	 */
	public long getPreAppId() {
		return preAppId;
	}
	
	/**
	 * 设置  原领料单id,退料单有
	 * @param preAppId
	 */
	public void setPreAppId(long preAppId) {
		this.preAppId = preAppId;
	}
	
	/**
	 * 获取  原领料单号,退料单有
	 * @return preAppNo
	 */
	public String getPreAppNo() {
		return preAppNo;
	}
	
	/**
	 * 设置  原领料单号,退料单有
	 * @param preAppNo
	 */
	public void setPreAppNo(String preAppNo) {
		this.preAppNo = preAppNo;
	}
	
	/**
	 * 获取  申请人
	 * @return appUser
	 */
	public String getAppUser() {
		return appUser;
	}
	
	/**
	 * 设置  申请人
	 * @param appUser
	 */
	public void setAppUser(String appUser) {
		this.appUser = appUser;
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
	 * 获取  审核人
	 * @return verifyUser
	 */
	public String getVerifyUser() {
		return verifyUser;
	}
	
	/**
	 * 设置  审核人
	 * @param verifyUser
	 */
	public void setVerifyUser(String verifyUser) {
		this.verifyUser = verifyUser;
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
	 * 获取  备注
	 * @return remark
	 */
	public String getRemark() {
		return remark;
	}
	
	/**
	 * 设置  备注
	 * @param remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/**
	 * 获取  状态   0=待审核,-1=审核不通过,1=已审核,3=部分出库,9=已出库
	 * @return status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * 设置  状态   0=待审核,-1=审核不通过,1=已审核,6=部分出库,9=已出库，-9出库失败
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取  经办人
	 * @return optUser
	 */
	public String getOptUser() {
		return optUser;
	}
	

	/**
	 * 设置  经办人
	 * @param optUser
	 */
	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}
	

	/**
	 * 获取  经办人Id
	 * @return optUserId
	 */
	public long getOptUserId() {
		return optUserId;
	}
	

	/**
	 * 设置  经办人Id
	 * @param optUserId
	 */
	public void setOptUserId(long optUserId) {
		this.optUserId = optUserId;
	}
	

	/**
	 * 获取  经办日期
	 * @return optDate
	 */
	public Date getOptDate() {
		return optDate;
	}
	

	/**
	 * 设置  经办日期
	 * @param optDate
	 */
	public void setOptDate(Date optDate) {
		this.optDate = optDate;
	}

	public String getStockplace() {
		return stockplace;
	}

	public void setStockplace(String stockplace) {
		this.stockplace = stockplace;
	}

	public long getStockplaceId() {
		return stockplaceId;
	}

	public void setStockplaceId(long stockplaceId) {
		this.stockplaceId = stockplaceId;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getSfType() {
		return sfType;
	}

	public void setSfType(String sfType) {
		this.sfType = sfType;
	}
	
	
}
