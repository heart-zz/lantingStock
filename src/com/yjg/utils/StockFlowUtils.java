package com.yjg.utils;

public class StockFlowUtils {

	//采购入库
	public static final String stockIn_purchase="采购入库";	
	//领料退料入库
	public static final String stockIn_return="领料退料入库";	
	//销售退料入库
	public static final String stockIn_saleReturn="销售退料";
	//盘库入库
	public static final String stockIn_stockTaking="盘库入库";
	//领料入库
	public static final String stockOut_use="领料出库";
	//盘库入库
	public static final String stockOut_stockTaking="盘库出库";
	//销售出库
	public static final String stockOut_sale="销售出库";
	
	//入库
	public static final int stockIn=1;
	//出库
	public static final int stockOut=-1;
	//采购审核通过
	public static final int verPass=1;
	//入库通过
	public static final int stockPass=9;
	//采购完成
	public static final int purFinish=6;
	//采购审核不通过
	public static final int verfail=1;
}
