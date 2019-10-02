package com.yjg.utils;

import com.yjg.entity.User;

/**
 * 客户终端Http请求的辅助类,用于验证客户端请求的合法性
 * @author yjg
 *
 */
public class AppHttpUtils extends IdEntity{
	
	private static long _idCount=0;
	
	/**
	 * 生成app-http请求的辅助实例(自动生成一个ID和当前时间戳)
	 * @param _userId 用户ID
	 * @param _appkey 终端key
	 */
	public AppHttpUtils(long _userId,String _appkey){
		userId=_userId;
		appKey=_appkey;
		timestamp=System.currentTimeMillis();
		long _id=_idCount+1;
		setId(_id);		
	}
	
	public AppHttpUtils(){
		
	}

	/**
	 * 服务器分配给终端的appKey字符串,与id唯一确定终端http请求合法性,
	 * 每次登录时,服务器随机分配
	 */
	//http请求密钥
	private String appKey;
	//用户ID
	private long userId;
	//对应的用户信息
	private User user;
	//时间戳
	private long timestamp;
	//客户端上传token
	private String token;
	/**
	 * 获取 http请求密钥
	 * @return appKey
	 */
	public String getAppKey() {
		return appKey;
	}
	

	/**
	 * 设置http请求密钥
	 * @param appKeyappKey
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	

	/**
	 * 获取  用户ID
	 * @return userId
	 */
	public long getUserId() {
		return userId;
	}
	

	/**
	 * 设置  用户ID
	 * @param userIduserId
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	

	/**
	 * 获取  对应的用户信息
	 * @return user
	 */
	public User getUser() {
		return user;
	}
	

	/**
	 * 设置  对应的用户信息
	 * @param useruser
	 */
	public void setUser(User user) {
		this.user = user;
	}
	

	/**
	 * 获取  时间戳
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	

	/**
	 * 设置  时间戳
	 * @param timestamptimestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 获取  客户端上传token
	 * @return token
	 */
	public String getToken() {
		return token;
	}
	

	/**
	 * 设置  客户端上传token
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	

	
}
