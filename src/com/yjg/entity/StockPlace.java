package com.yjg.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

@Table("st_stockPlace")
public class StockPlace extends IdEntity {
		
	//仓库名称
	@Column
	private String stockplace;
	
	//备注
	@Column
	private String remark;
	
	//仓库具体地址
	@Column
	private String exactAddr;

	public String getStockplace() {
		return stockplace;
	}

	public void setStockplace(String stockplace) {
		this.stockplace = stockplace;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExactAddr() {
		return exactAddr;
	}

	public void setExactAddr(String exactAddr) {
		this.exactAddr = exactAddr;
	}
	
	
}
