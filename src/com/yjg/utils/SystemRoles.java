package com.yjg.utils;
/**
 * 系统内置角色信息
 * @author yjg
 *
 */
public class SystemRoles {
	
	/**
	 * 系统内置角色:无法被删除,且唯一
	 */
	public static final String[] systemRols={"超级管理员","公司管理员"};
	
	/**
	 * 系统内置角色（常量）:超级管理员
	 */
	public static final String role_superAdmin=systemRols[0];
	
	/**
	 * 系统内置角色（常量）:公司管理员
	 */
	public static final String role_comAdmin=systemRols[1];

}
