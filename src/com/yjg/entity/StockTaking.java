package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 盘库单
 * @author yjg
 *
 */
@Table("st_stockTaking")
public class StockTaking extends IdEntity{
		//盘库单号
		@Column
		private String appNo;					
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
		
		//备注
		@Column
		private String hint;
		//状态
		@Column
		private int status;
		
		//仓库
		@Column
		private String stockplace;
		//仓库id
		@Column
		private long stockplaceId;
		/**
		 * 获取  盘库单号
		 * @return appNo
		 */
		public String getAppNo() {
			return appNo;
		}
		
		/**
		 * 设置  盘库单号
		 * @param appNo
		 */
		public void setAppNo(String appNo) {
			this.appNo = appNo;
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

		/**
		 * 获取  仓库
		 * @return stockplace
		 */
		public String getStockplace() {
			return stockplace;
		}
		

		/**
		 * 设置  仓库
		 * @param stockplace
		 */
		public void setStockplace(String stockplace) {
			this.stockplace = stockplace;
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

		public String getHint() {
			return hint;
		}

		public void setHint(String hint) {
			this.hint = hint;
		}
		
		
}
