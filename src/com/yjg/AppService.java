package com.yjg;

import com.yjg.entity.RoleAuth;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 跨模块服务类,用于公共服务
 * @author yjg
 *
 */
public class AppService {
		
	
	/**
	 * 存储mina ioacceptor集合
	 */
	public static Map<String,IoAcceptor> acceptorMap=new HashMap<String, IoAcceptor>();
	
	private Logger logger=Logger.getLogger(AppService.class);

	/**
	 * 获取特定服务名对应的IoAcceptor
	 * @param serviceName 服务名称
	 * @return 服务对应的IoAcceptor
	 */
	public static IoAcceptor getIoAcceptor(String serviceName){
		return acceptorMap.get(serviceName);
	}
	
	/**
	 * 根据服务名和sessionId获取对应的session-socket通讯
	 * @param serviceName 服务名
	 * @param sessionId 会话id
	 * @return {@link Result} 封装好的结果
	 */
	public static Result getSession(String serviceName,long sessionId){
		Result re=new Result();
		IoAcceptor ia=getIoAcceptor(serviceName);
		if(ia!=null){
			Map<Long,IoSession> sessionMap=ia.getManagedSessions();
			if(sessionMap==null){
				re.setInfo(serviceName+"的IoAcceptor存在,但是对应的SessionMap不存在");
				re.setStatus(Params.status_failed);
			}
			else{
				IoSession session=sessionMap.get(sessionId);
				if(session==null){
					re.setInfo("session不存在");
					re.setStatus(Params.status_failed);
				}
				else{
					re.setStatus(Params.status_success);
					re.setObj(session);
					re.setInfo("获取成功");					
				}
			}
		}
		else{
			re.setInfo(serviceName+"对应的IoAcceptor不存在");
			re.setStatus(Params.status_failed);
		}
		return re;
	}
	
		
	/**
	 * 根据变量名称从params.properties文中获取对应的值
	 * @param propertyName 变量名称
	 * @return 相应的值
	 */
	public String getParamsProperty(String propertyName){
		if(propertyName==null || propertyName.equals(""))
			return "";
		InputStream is=this.getClass().getClassLoader().getResourceAsStream("params.properties");
		Properties p=new Properties();
		try{
			p.load(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		String re;
		try {
			re = new String(p.getProperty(propertyName).getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			re="";
		}
		return re;
	}
	
	/**
	 * 将整型值转换为两个字节对应的值
	 * @param value
	 * @return
	 */
	public byte[] intToByteValue(int value){
		int high=value/256;
		int low=value%256;
		byte[] re={(byte)high,(byte)low};
		return re;
	}
	
	/**
	 * 检查字符串是否为空
	 * @param input
	 * @return
	 */
	public boolean isEmpty(String input){
		if(input==null || "".equals(input))
			return true;		
		return false;
	}
		

	/**
	 * 从httpsession 获取登录信息
	 * @param session
	 * @return
	 */
	public static User getLoginUserInSession(HttpSession session){
		if(session==null)
			return null;
		Object obj=session.getAttribute(Params.loginUserInSessionStr);
		if(obj!=null)
			return (User)obj;
		return null;
	}
	
	/**
	 * 通用的获取用户信息
	 * @param userId 用户ID
	 * @param appKey app端访问密钥
	 * @param session 网页session
	 * @param dao 数据库操作
	 * @return {@link User}
	 */ 
	public static User getUserInfo(long userId,String appKey,HttpSession session,Dao dao){
		User user=getLoginUserInSession(session);		
		return user;
	}
	
	/**
	 * 检查用户是否有权限
	 * @param userId 用户id
	 * @param authCode 权限代码
	 * @param dao 数据库操作
	 * @return {@link Boolean}
	 */
	public static boolean checkUserAuth(long userId,String authCode,Dao dao){
		if(dao.count(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",authCode))==0){
			User u=dao.fetch(User.class,userId);
			if(u!=null){
				if(dao.count(RoleAuth.class,Cnd.where("roleId","=",u.getRoleId()).and("authCode","=",authCode))==0)
					return false;
				else
					return true;
			}
			else
				return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * 通用的获取用户信息(网页端和APP端通用),本操作时在请求合法性检查之后
	 * @param acu {@link AppHttpUtils} appkey辅助类
	 * @param session 网页session
	 * @return {@link User}
	 */ 
	public User getUserInfo(AppHttpUtils acu,HttpSession session){
		AppHttpUtils _acu=null;
		User user=null;
		if(acu!=null){
			_acu=ServiceData.getAppKeyInfo(acu.getId());
		}				
		if(_acu!=null){
			user=_acu.getUser();
		}		
		if(user==null){
			user=getLoginUserInSession(session);	
		}			
		return user;
	}
		
}
