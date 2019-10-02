package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 销售（退料）表
 * @author yjg
 *
 */
@Table("st_sale")
public class Sale extends IdEntity{

		//销售(退料)单号
		@Column
		private String appNo;
		
		//类型 1=退料单，0=销售单
		@Column
		private int type;
		//原销售单id,退料单有
		@Column
		private long preAppId;
		//原销售单号,退料单有
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
		//备注
		@Column
		private String remark;
		
		//客户
		@Column
		private String custName;
		//客户地址
		@Column
		private String custAddr;
		//客户联系方式
		@Column
		private String custPhone;
		
		//状态
		@Column
		private int status;

		/**
		 * 获取  销售(退料)单号
		 * @return appNo
		 */
		public String getAppNo() {
			return appNo;
		}
		

		/**
		 * 设置  销售(退料)单号
		 * @param appNo
		 */
		public void setAppNo(String appNo) {
			this.appNo = appNo;
		}
		

		/**
		 * 获取  类型 1=退料单，0=销售单
		 * @return type
		 */
		public int getType() {
			return type;
		}
		

		/**
		 * 设置  类型 1=退料单，0=销售单
		 * @param type
		 */
		public void setType(int type) {
			this.type = type;
		}
		

		/**
		 * 获取  原销售单id,退料单有
		 * @return preAppId
		 */
		public long getPreAppId() {
			return preAppId;
		}
		

		/**
		 * 设置  原销售单id,退料单有
		 * @param preAppId
		 */
		public void setPreAppId(long preAppId) {
			this.preAppId = preAppId;
		}
		

		/**
		 * 获取  原销售单号,退料单有
		 * @return preAppNo
		 */
		public String getPreAppNo() {
			return preAppNo;
		}
		

		/**
		 * 设置  原销售单号,退料单有
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
		 * 获取  客户
		 * @return custName
		 */
		public String getCustName() {
			return custName;
		}
		

		/**
		 * 设置  客户
		 * @param custName
		 */
		public void setCustName(String custName) {
			this.custName = custName;
		}
		

		/**
		 * 获取  客户地址
		 * @return custAddr
		 */
		public String getCustAddr() {
			return custAddr;
		}
		

		/**
		 * 设置  客户地址
		 * @param custAddr
		 */
		public void setCustAddr(String custAddr) {
			this.custAddr = custAddr;
		}
		

		/**
		 * 获取  客户联系方式
		 * @return custPhone
		 */
		public String getCustPhone() {
			return custPhone;
		}
		

		/**
		 * 设置  客户联系方式
		 * @param custPhone
		 */
		public void setCustPhone(String custPhone) {
			this.custPhone = custPhone;
		}
		

		/**
		 * 获取  状态
		 * @return status
		 */
		public int getStatus() {
			return status;
		}
		

		/**
		 * 设置  状态
		 * @param status
		 */
		public void setStatus(int status) {
			this.status = status;
		}
		
}
