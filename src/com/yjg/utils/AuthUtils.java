package com.yjg.utils;

import java.util.Date;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import com.yjg.entity.Auth;
import com.yjg.entity.RoleAuth;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;

public class AuthUtils {
	/*
	 *添加用户权限
	 */
	public static void addUserAuth(long userId,String authCode,Dao dao) {
		if(dao.count(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",authCode))==0){//不包含
			UserAuth ua=new UserAuth();						
			ua.setUserId(userId);
			ua.setAuthCode(authCode);						
			try {
				dao.insert(ua);
				List<Auth> sonauths=dao.query(Auth.class, Cnd.where("fatherCode","=",authCode));
				for(Auth sonauth:sonauths){
					if(dao.count(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",sonauth.getAuthCode()))==0){//不包含
						UserAuth son=new UserAuth();
						son.setAuthCode(sonauth.getAuthCode());
						son.setUserId(userId);
						dao.insert(son);
					}								
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *根据角色添加用户权限
	 */
	public static void addUserAuthByRole(User author,long userId,long roleId,Dao dao) {
		if(dao.count(RoleAuth.class,Cnd.where("roleId","=", roleId))!=0){//不包含			
			try {
				List<RoleAuth> list = dao.query(RoleAuth.class,Cnd.where("roleId","=", roleId));
				for (RoleAuth roleAuth : list) {
					String raCode = roleAuth.getAuthCode();
					if (!checkUserAuth(userId,raCode,dao)) {
						UserAuth uAuth=new UserAuth();
						uAuth.setAuthCode(raCode);
						uAuth.setUserId(userId);
						uAuth.setAuthor(author.getUsername());
						uAuth.setAuthorId(author.getId());
						uAuth.setDateCreater(new Date());
						dao.fastInsert(uAuth);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		else {
			System.out.println("角色权限表中没有该角色");
		}
	}
	
	/*
	 *检测用户是否拥有该权限
	 */
	public static boolean checkUserAuth(long userId,String authCode,Dao dao) {	
		if(dao.count(UserAuth.class, Cnd.where("userId","=",userId).and("authCode","=",authCode))!=0){
			return true;
		}else {
			return false;
		}
	}
}
