package com.yjg.utils;

/**
 * 用于显示的IoSession类
 * @author yjg
 *
 */
public class UserIoSession extends IdEntity{

	private String localAddr;
	private String remoteAddr;
	private String serviceAddr;
	/**
	 * 获取 localAddr
	 * @return localAddrlocalAddr
	 */
	public String getLocalAddr() {
		return localAddr;
	}
	/**
	 * 设置 localAddr
	 * @param localAddrlocalAddr
	 */
	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}
	/**
	 * 获取 remoteAddr
	 * @return remoteAddrremoteAddr
	 */
	public String getRemoteAddr() {
		return remoteAddr;
	}
	/**
	 * 设置 remoteAddr
	 * @param remoteAddrremoteAddr
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	/**
	 * 获取 serviceAddr
	 * @return serviceAddrserviceAddr
	 */
	public String getServiceAddr() {
		return serviceAddr;
	}
	/**
	 * 设置 serviceAddr
	 * @param serviceAddrserviceAddr
	 */
	public void setServiceAddr(String serviceAddr) {
		this.serviceAddr = serviceAddr;
	}
}
