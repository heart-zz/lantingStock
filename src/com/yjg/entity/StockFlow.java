package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 库存流动表
 * @author yjg
 *
 */
@Table("st_stockflow")
public class StockFlow extends IdEntity{
	
	//采购 领料单号
	@Column
	private String preAppNo;
	
	//退料 的 领料单号
	@Column
	private String preAppId;
	
 	//对应库存记录
	@Column
	private long stockId;
	
	//退料 的 领料 flowId
	@Column
	private long preItemId;
	
	//库存类型
	@Column
	private String stType;
	
	//库存流动类型:内置，参见 StockFlowUtils 内属性
	@Column
	private String sfType;
	
	//数量变动前,对应库存数量
	@Column
	private float stockNumber;
	
	
	//退料的实时领料数量
	@Column
	@ColDefine(precision=2,width=16)
	private float nowNum;
	
	//变动数量
	@Column
	@ColDefine(precision=2,width=16)
	private float number;
	
	//数量变动后,对应库存数量
	@Column
	private float stockedNumber;

	//单价
	@Column
	@ColDefine(precision=2,width=16)
	private float price;
	
	//小计
	@Column
	@ColDefine(precision=2,width=16)
	private float sum;
	
	//标准品名id
    @Column
	private long productId;
    
	//标准品名编号
	@Column
	@ColDefine(width=128)
	private String proNo;
	
	//品名
	@Column
	@ColDefine(width=128)
	private String name;
	
	//型号
	@Column
	@ColDefine(width=128)
	private String model;
	
	//单位
	@Column
	private String unit;
	
	//用途
	@Column
	@ColDefine(width=128)
	private String content;
	
	//备注
	@Column
	@ColDefine(width=128)
	private String  remark;
	
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
		
		//库存管理员
		@Column
		private String stockUser;
		//库存管理员Id
		@Column
		private long stockUserId;
		//出入库日期
		@Column
		private Date stockDate;
	
	//库存变动方向 1=流入库存,-1=流出库存,参见 StockFlowUtils 内属性
	@Column
	private int flowType;
	
	//状态码   0=待审核,1=已审核,6=已采购,-9=出/入库不通过,9=已入/出库
	@Column
	private int status;
	
	//目标仓库
	@Column
	private String stockplace;
	//目标仓库id
	@Column
	private long stockplaceId;
	
	//目标项目
	@Column
	private String project;

	/**
	 * 获取  原单号
	 * @return preAppNo
	 */
	public String getPreAppNo() {
		return preAppNo;
	}
	

	/**
	 * 设置  原单号
	 * @param preAppNo
	 */
	public void setPreAppNo(String preAppNo) {
		this.preAppNo = preAppNo;
	}
	

	/**
	 * 获取  原业务id
	 * @return preAppId
	 */
	public String getPreAppId() {
		return preAppId;
	}
	

	/**
	 * 设置  原业务id
	 * @param preAppId
	 */
	public void setPreAppId(String preAppId) {
		this.preAppId = preAppId;
	}
	

	/**
	 * 获取  对应库存记录
	 * @return stockId
	 */
	public long getStockId() {
		return stockId;
	}
	

	/**
	 * 设置  对应库存记录
	 * @param stockId
	 */
	public void setStockId(long stockId) {
		this.stockId = stockId;
	}
	

	/**
	 * 获取  对应明细id
	 * @return preItemId
	 */
	public long getPreItemId() {
		return preItemId;
	}
	

	/**
	 * 设置  对应明细id
	 * @param preItemId
	 */
	public void setPreItemId(long preItemId) {
		this.preItemId = preItemId;
	}
	

	/**
	 * 获取  库存类型
	 * @return stType
	 */
	public String getStType() {
		return stType;
	}
	

	/**
	 * 设置  库存类型
	 * @param stType
	 */
	public void setStType(String stType) {
		this.stType = stType;
	}
	

	/**
	 * 获取  库存流动类型:内置，参见 StockFlowUtils 内属性
	 * @return sfType
	 */
	public String getSfType() {
		return sfType;
	}
	

	/**
	 * 设置  库存流动类型:内置，参见 StockFlowUtils 内属性
	 * @param sfType
	 */
	public void setSfType(String sfType) {
		this.sfType = sfType;
	}
	

	/**
	 * 获取  数量
	 * @return number
	 */
	public float getNumber() {
		return number;
	}
	

	/**
	 * 设置  数量
	 * @param number
	 */
	public void setNumber(float number) {
		this.number = number;
	}
	

	/**
	 * 获取  单价
	 * @return price
	 */
	public float getPrice() {
		return price;
	}
	

	/**
	 * 设置  单价
	 * @param price
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	

	/**
	 * 获取  小计
	 * @return sum
	 */
	public float getSum() {
		return sum;
	}
	

	/**
	 * 设置  小计
	 * @param sum
	 */
	public void setSum(float sum) {
		this.sum = sum;
	}
	

	/**
	 * 获取  品名
	 * @return name
	 */
	public String getName() {
		return name;
	}
	

	/**
	 * 设置  品名
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	

	/**
	 * 获取  型号
	 * @return model
	 */
	public String getModel() {
		return model;
	}
	

	/**
	 * 设置  型号
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}
	

	/**
	 * 获取  单位
	 * @return unit
	 */
	public String getUnit() {
		return unit;
	}
	

	/**
	 * 设置  单位
	 * @param unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	

	/**
	 * 获取  用途
	 * @return content
	 */
	public String getContent() {
		return content;
	}
	

	/**
	 * 设置  用途
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}
	

	/**
	 * 获取  库存变动方向 1=流入库存,-1=流出库存,参见 StockFlowUtils 内属性
	 * @return flowType
	 */
	public int getFlowType() {
		return flowType;
	}
	

	/**
	 * 设置  库存变动方向 1=流入库存,-1=流出库存,参见 StockFlowUtils 内属性
	 * @param flowType
	 */
	public void setFlowType(int flowType) {
		this.flowType = flowType;
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
	 * 获取  0=待审核,1=已审核,6=已采购,-9=入库不通过,9=已入库
	 * @return status
	 */
	public int getStatus() {
		return status;
	}
	

	/**
	 * 设置 状态码   0=待审核,1=已审核,6=已采购,-9=出/入库不通过,9=已入/出库
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/**
	 * 获取  目标仓库
	 * @return stockplace
	 */
	public String getStockplace() {
		return stockplace;
	}
	


	/**
	 * 设置  目标仓库
	 * @param stockplace
	 */
	public void setStockplace(String stockplace) {
		this.stockplace = stockplace;
	}


	/**
	 * 获取  目标仓库id
	 * @return stockplaceId
	 */
	public long getStockplaceId() {
		return stockplaceId;
	}
	


	/**
	 * 设置  目标仓库id
	 * @param stockplaceId
	 */
	public void setStockplaceId(long stockplaceId) {
		this.stockplaceId = stockplaceId;
	}


	/**
	 * 获取  目标项目
	 * @return project
	 */
	public String getProject() {
		return project;
	}
	


	/**
	 * 设置  目标项目
	 * @param project
	 */
	public void setProject(String project) {
		this.project = project;
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


	public float getStockNumber() {
		return stockNumber;
	}


	public void setStockNumber(float stockNumber) {
		this.stockNumber = stockNumber;
	}

	public String getStockUser() {
		return stockUser;
	}


	public void setStockUser(String stockUser) {
		this.stockUser = stockUser;
	}


	public long getStockUserId() {
		return stockUserId;
	}


	public void setStockUserId(long stockUserId) {
		this.stockUserId = stockUserId;
	}


	public Date getStockDate() {
		return stockDate;
	}


	public void setStockDate(Date stockDate) {
		this.stockDate = stockDate;
	}
	
	public float getStockedNumber() {
		return stockedNumber;
	}


	public void setStockedNumber(float stockedNumber) {
		this.stockedNumber = stockedNumber;
	}


	public long getProductId() {
		return productId;
	}


	public void setProductId(long productId) {
		this.productId = productId;
	}


	public String getProNo() {
		return proNo;
	}


	public void setProNo(String proNo) {
		this.proNo = proNo;
	}


	public float getNowNum() {
		return nowNum;
	}


	public void setNowNum(float nowNum) {
		this.nowNum = nowNum;
	}
}
