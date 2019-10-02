package com.yjg.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 存储服务器与下位机交互用到的各类数据
 * @author yjgpc
 *
 */
public class ServiceData {

	/**
	 * mina 专用数据库连接池
	 */
	public static DruidDataSource minaDDS=null;
	
	/**
	 * 待写入数据库数据队列
	 */
	public static Queue<Object> datasWaitingInsert=new LinkedList<Object>();	
	
	/**
	 * 存储终端http请求的辅助数据,用于验证终端请求的合法性.一般为用户成功登陆后记录。
	 */
	private static Map<Long,AppHttpUtils> appKeys=new HashMap<Long,AppHttpUtils>();	

	
	/**
	 * 添加待写入的记录到队列
	 * @param obj
	 */
	public static void addDataWaitingInsert(Object obj){
		datasWaitingInsert.add(obj);
	}
	

	/**
	 * 添加appKey信息,直接覆盖原数据(如果有)
	 * @param ahu
	 */
	public static void addAppKey(AppHttpUtils ahu){
		if(appKeys==null)
			appKeys=new HashMap<Long,AppHttpUtils>();
		appKeys.put(ahu.getId(), ahu);
		//logger.info("addAppKey:appKeys-size="+appKeys.size()+",toString="+appKeys.toString());
		
	}
	
	/**
	 * 移除appKey信息
	 * @param userId 用户ID
	 */
	public static void delAppKey(long appKeyId){
		if(appKeys.containsKey(appKeyId)){
			appKeys.remove(appKeyId);
		}
	}
	
	/**
	 * 获取终端用户的appKey信息
	 * @param userId
	 * @return
	 */
	public static AppHttpUtils getAppKeyInfo(long acuId){
		return appKeys.get(acuId);
	}	
	
	/**
	 * 返回appKey所有数据
	 * @return {@link Iterator}
	 */
	public static Iterator<AppHttpUtils> getAppKeyList(){
		//logger.info("getAppKeyList:appKeys-size="+appKeys.size()+",toString="+appKeys.toString());
		if(appKeys==null)
			return null;
		Collection<AppHttpUtils> col=appKeys.values();
		if(col==null)
			return null;
		else
			return col.iterator();
	}
	
	/**
	 * 返回appKey缓存数据
	 * @return
	 */
	public static Map<Long, AppHttpUtils> getAppKeys(){
		//logger.info("getAppKeys:appKeys-size="+appKeys.size()+",toString="+appKeys.toString());
		return appKeys;
	}
}
