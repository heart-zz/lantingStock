package com.yjg.utils;

import java.util.List;

/**
 * 系统静态变量集
 * @author yjg
 *
 */
public class Params {
	
	/**
	 * 默认初始密码
	 */
	public static final String password_default="123456";
	
	/**
	 * 状态-失败：-1
	 */
	public static final int status_failed=-1;
	/**
	 * 状态-已被删除：true
	 */
	public static final boolean status_isDeleted=true;
	
	/**
	 * 状态-未被删除：false
	 */
	public static final boolean status_notDeleted=false;
	/**
	 * 状态-成功：1
	 */
	public static final int status_success=1;
	
	/**
	 * 状态-正在服务：1
	 */
	public static final int status_run=1;
	
	/**
	 * 状态-服务停止：*1
	 */
	public static final int status_stop=-1;
		
	
	/**
	 * 系统默认服务名
	 */
	public static String serviceName="lantingSource system";
	
	/**
	 * 系统根路径
	 */
	public static String rootUrl=null;
	
	/**
	 * 睡眠时间-长
	 */
	public static final long sleepTime_long=2000;
	
	/**
	 * 睡眠时间-中
	 */
	public static final long sleepTime_middle=1000;
	
	/**
	 * 睡眠时间-短
	 */
	public static final long sleepTime_short=700;
	
	/**
	 * session中记录登录用户信息key
	 */
	public static final String loginUserInSessionStr="user";
	
	/**
	 * 用户未登录时提示信息
	 */
	public static final String loginOutInfoStr="登录信息已失效,请重新登录";
	
	/**
	 * 用户未登录时提示信息
	 */
	public static final String str_notLogin="你还未登录请登录";
	
	/**
	 * 操作成功提示信息
	 */
	public static final String str_optSuccess="操作成功";
	
	/**
	 * 权限配置文件路径
	 */
	public static final String authlist_xls="files/authlist.xls";
	
	/**
	 * app用户在线心跳时间 15分钟
	 */
	public static final int appUserActiveMs=900000;
	
	/**
	 * 系统存储的请假类型(从配置文件读取)
	 */
	public static List<ComboUtils> askforleaveType=null;
	
	/**
	 * 业务逻辑级别:高敏感
	 */
	public static final int businessLevel_importment=9;
	/**
	 * 业务逻辑级别:普通
	 */
	public static final int businessLevel_normal=5;
	/**
	 * 业务逻辑级别:低级
	 */
	public static final int businessLevel_low=1;
	
	/**
	 * 没有权限访问
	 */
	public static final String str_noRequestAuth="对不起 你还没有该权限!";
	
	/**
	 * 默认开始页码
	 */
	public static final int pageNoInit=1;
	
	/**
	 * 默认每页大小
	 */
	public static final int pageSizeDefault=20;
	
	/**
	 * 提示信息:参数不完整
	 */
	public static final String info_err_notFull="参数不完整";
	
	/**
	 * 提示信息:参数为空
	 */
	public static final String info_errr_null="参数为空";
	
	
	
	/**
	 * 分割符:英文逗号
	 */
	public static final String split_comma=",";
	
	/**
	 * 分割符：英文分号
	 */
	public static final String split_semi=";";
	
	/**
	 * 钉钉 corpId
	 */
	public static final String ddCorpId="ding53d691e997e48e0b35c2f4657eb6378f";
	/**
	 * 钉钉 ssosecret
	 */
	public static final String ddSSOsecret="qffRWI6dduDDs1WsYHIzoGXiQOF444ir1J3D6tQqcfnxNmaw4Xc3Wbc6uKoKHEsx";
	
	/**
	 * 钉钉 测试应用secret
	 */
	public static final String testAppSecret="Gd5RasRY_maTEtr8vc723Pa0cEKAZSd7w8DnbL9lHX2lbPux7afxXgaMVTatXqvT";
	
	/**
	 * 钉钉 测试应用accessToken
	 */
	public static String testAppAccessToken="";
	
	/**
	 * 钉钉api访问域名
	 */
	public static final String ddUrl="https://oapi.dingtalk.com";
		
}
