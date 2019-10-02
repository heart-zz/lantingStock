package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 库存表
 * <br/>注:同一个仓库里，不允许出现重名物品。
 * @author yjg
 *
 */
@Table("st_stock")
public class Stock extends IdEntity{
	
		//标准品名id
	    @Column
		private long productId;
	    
		//标准品名编号
		@Column
		@ColDefine(width=128)
		private String proNo;

		//类型
		@Column
		private String type;
		
		
		//库存数量
		@Column
		@ColDefine(precision=2,width=16)
		private float number;
		
		//入库总数量
		@Column
		@ColDefine(precision=2,width=16)
		private float inNum;

		//出库总数量
		@Column
		@ColDefine(precision=2,width=16)
		private float outNum;
		
		//加权平均入库单价
		@Column
		@ColDefine(precision=2,width=16)
		private float priceIn;
		
		//加权平均出库单价
		@Column
		@ColDefine(precision=2,width=16)
		private float priceOut;
		
		//入库总价
		@Column
		@ColDefine(precision=2,width=16)
		private float inSum;
		
		//出库总价
		@Column
		@ColDefine(precision=2,width=16)
		private float outSum;
		
		//库存总价
		@Column
		@ColDefine(precision=2,width=16)
		private float sum;

		//最新入库单价
		@Column
		private float priceLast;
		//最新入库日期
		@Column
		private Date stockInLast;
		
		//最新出库日期
		@Column
		private Date stockOutLast;
		//最低库存数量
		@Column
		private float numberMin;
		//最高库存数量
		@Column
		private float numberMax;
		
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
		
		//操作人
		@Column
		private String optUser;
		//操作人id
		@Column
		private long optUserId;
				
		//备注
		@Column
		@ColDefine(width=128)
		private String  remark;
				
		//所属仓库
		@Column
		private String stockplace;
		//所属仓库id
		@Column
		private long stockplaceId;
		/**
		 * 获取  类型
		 * @return type
		 */
		public String getType() {
			return type;
		}
		
		/**
		 * 设置  类型
		 * @param type
		 */
		public void setType(String type) {
			this.type = type;
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
		 * 获取  加权平均入库单价
		 * @return priceIn
		 */
		public float getPriceIn() {
			return priceIn;
		}
		
		/**
		 * 设置  加权平均入库单价
		 * @param priceIn
		 */
		public void setPriceIn(float priceIn) {
			this.priceIn = priceIn;
		}
		
		/**
		 * 获取  最新入库单价
		 * @return priceLast
		 */
		public float getPriceLast() {
			return priceLast;
		}
		
		/**
		 * 设置  最新入库单价
		 * @param priceLast
		 */
		public void setPriceLast(float priceLast) {
			this.priceLast = priceLast;
		}
		
		/**
		 * 获取  最新入库日期
		 * @return stockInLast
		 */
		public Date getStockInLast() {
			return stockInLast;
		}
		
		/**
		 * 设置  最新入库日期
		 * @param stockInLast
		 */
		public void setStockInLast(Date stockInLast) {
			this.stockInLast = stockInLast;
		}
		
		/**
		 * 获取  最新出库日期
		 * @return stockOutLast
		 */
		public Date getStockOutLast() {
			return stockOutLast;
		}
		
		/**
		 * 设置  最新出库日期
		 * @param stockOutLast
		 */
		public void setStockOutLast(Date stockOutLast) {
			this.stockOutLast = stockOutLast;
		}
		
		/**
		 * 获取  最低库存数量
		 * @return numberMin
		 */
		public float getNumberMin() {
			return numberMin;
		}
		
		/**
		 * 设置  最低库存数量
		 * @param numberMin
		 */
		public void setNumberMin(float numberMin) {
			this.numberMin = numberMin;
		}
		
		/**
		 * 获取  最高库存数量
		 * @return numberMax
		 */
		public float getNumberMax() {
			return numberMax;
		}
		
		/**
		 * 设置  最高库存数量
		 * @param numberMax
		 */
		public void setNumberMax(float numberMax) {
			this.numberMax = numberMax;
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
		 * 获取  操作人
		 * @return optUser
		 */
		public String getOptUser() {
			return optUser;
		}
		
		/**
		 * 设置  操作人
		 * @param optUser
		 */
		public void setOptUser(String optUser) {
			this.optUser = optUser;
		}
		
		/**
		 * 获取  操作人id
		 * @return optUserId
		 */
		public long getOptUserId() {
			return optUserId;
		}
		
		/**
		 * 设置  操作人id
		 * @param optUserId
		 */
		public void setOptUserId(long optUserId) {
			this.optUserId = optUserId;
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
		 * 获取  所属仓库
		 * @return stockplace
		 */
		public String getStockplace() {
			return stockplace;
		}
		
		/**
		 * 设置  所属仓库
		 * @param stockplace
		 */
		public void setStockplace(String stockplace) {
			this.stockplace = stockplace;
		}
		
		/**
		 * 获取  所属仓库id
		 * @return stockplaceId
		 */
		public long getStockplaceId() {
			return stockplaceId;
		}
		
		/**
		 * 设置  所属仓库id
		 * @param stockplaceId
		 */
		public void setStockplaceId(long stockplaceId) {
			this.stockplaceId = stockplaceId;
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
		 * 获取  标准品名id
		 * @return productId
		 */
		public long getProductId() {
			return productId;
		}
		

		/**
		 * 设置  标准品名id
		 * @param productId
		 */
		public void setProductId(long productId) {
			this.productId = productId;
		}

		public String getProNo() {
			return proNo;
		}

		public void setProNo(String proNo) {
			this.proNo = proNo;
		}

		public float getOutNum() {
			return outNum;
		}

		public void setOutNum(float outNum) {
			this.outNum = outNum;
		}

		public float getPriceOut() {
			return priceOut;
		}

		public void setPriceOut(float priceOut) {
			this.priceOut = priceOut;
		}
		public float getInNum() {
			return inNum;
		}

		public void setInNum(float inNum) {
			this.inNum = inNum;
		}

		public float getInSum() {
			return inSum;
		}

		public void setInSum(float inSum) {
			this.inSum = inSum;
		}

		public float getOutSum() {
			return outSum;
		}

		public void setOutSum(float outSum) {
			this.outSum = outSum;
		}
		
		
		
}
