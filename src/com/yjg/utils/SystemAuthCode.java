package com.yjg.utils;
/**
 * 系统一级权限代号表
 * @author yjg
 *
 */
public class SystemAuthCode {
	
	
	/**
	 * 系统一级权限代号表:无法被删除,且唯一
	 */
	public static final String[] systemAuth={"nav-panel-0","nav-panel-1","nav-panel-2",
			"nav-panel-3","nav-panel-4","nav-panel-5","nav-panel-6","nav-panel-7"};

	/**
	 * 系统管理权限
	 */
	public static final String auth_sys=systemAuth[0];
	
	/**
	 * 个人信息管理权限
	 */
	public static final String auth_person=systemAuth[1];
	
	/**
	 * 公司人事管理权限
	 */
	public static final String auth_com=systemAuth[2];
	
	/**
	 * 采购管理权限
	 */
	public static final String auth_pur=systemAuth[3];
	
	/**
	 * 库存管理权限
	 */
	public static final String auth_stock=systemAuth[4];
	
	/**
	 * 采购申请权限
	 */
	public static final String auth_purApply=systemAuth[5];
	
	/**
	 * 品名管理权限
	 */
	public static final String auth_pro=systemAuth[6];
	/**
	 * 出库申请权限
	 */
	public static final String auth_outApply=systemAuth[7];
}
