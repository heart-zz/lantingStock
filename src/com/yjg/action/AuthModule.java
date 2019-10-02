package com.yjg.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.yjg.AppService;
import com.yjg.entity.Auth;
import com.yjg.entity.Role;
import com.yjg.entity.RoleAuth;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.SystemAuthCode;
/**
 * 权限模块-实现.所有请求路径均为  auth/###
 * @author yjg
 *
 */
@IocBean
@InjectName("authModule")
public class AuthModule{

	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	@At("auth/getAuthList")
	@Ok("json")
	public Result getAuthList(@Param("pageNo") int pageNo,@Param("pageSize") int pageSize, @Param("total") int total) {
		Result re=new Result();			
		try {
			Pager pager=dao.createPager(pageNo, pageSize);				
			if(total==0)
				total=dao.count(Auth.class);
			re.setList(dao.query(Auth.class, Cnd.where(null),pager));
			re.setTotal(total);
			re.setInfo("操作成功");
			re.setStatus(Params.status_success);
		} catch (Exception e) {
			re.setInfo("获取列表出错:"+e.getMessage());
			re.setList(null);
			re.setStatus(Params.status_failed);
		}	
		return re;
	}
	
	@At("auth/addUserAuth") //添加用户权限，添加父权限时会添加其所有子权限
	@Ok("json")
	public Result addUserAuth(@Param("userId") long userId,
			@Param("authCodes") String authCodes) {
		Result re=new Result();
		if(userId==0 || service.isEmpty(authCodes)){
			re.setInfo("参数为空");
			re.setStatus(Params.status_failed);
		}
		else{
			String[] authcode=authCodes.split(",");
			boolean hasError=false;
			for(String auth:authcode){
				if(!service.isEmpty(auth)){										
					if(dao.count(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",auth))==0){//不包含
						UserAuth ua=new UserAuth();						
						ua.setUserId(userId);
						ua.setAuthCode(auth);						
						try {
							dao.insert(ua);
							List<Auth> sonauths=dao.query(Auth.class, Cnd.where("fatherCode","=",auth));
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
							hasError=true;
						}
					}
				}
			}
			if(hasError){
				re.setStatus(Params.status_failed);
				re.setInfo("操作有错误");
			}
			else{
				re.setInfo(Params.str_optSuccess);
				re.setStatus(Params.status_success);
			}
		}		
		return re;
	}

	@At("auth/delUserAuth")
	@Ok("json")
	public Result delUserAuth(@Param("userId")long userId,@Param("authCodes") String authCodes) {
		// TODO Auto-generated method stub
		Result re=new Result();
		if(service.isEmpty(authCodes) || userId==0){
			re.setInfo("参数为空");
			re.setStatus(Params.status_failed);
		}
		else{
			String[] auths=authCodes.split(",");
			for(int i=0;i<auths.length;i++){
				if(!"".equals(auths[i])){
					UserAuth ua=dao.fetch(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",auths[i]));
					List<Auth> sonauth=dao.query(Auth.class, Cnd.where("fatherCode","=",auths[i]));
					try {
						if(ua!=null)
							dao.delete(ua);
						for(Auth son:sonauth){
							UserAuth sonua=dao.fetch(UserAuth.class,Cnd.where("userId","=",userId).and("authCode","=",son.getAuthCode()));
							if(sonua!=null){
								dao.delete(sonua);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						re.setInfo("数据库操作失败");
						re.setStatus(Params.status_failed);
						return re;
					}
					
				}
			}
			re.setInfo(Params.str_optSuccess);
			re.setStatus(Params.status_success);
		}
		
		return re;
	}	
	
	/**
	 * 检查权限列表是否包含此权限
	 * @param list 待检查的权限列表
	 * @param authCode 权限代码
	 * @return 如果包含 则返回索引，不包含，返回-1
	 */
	public static int authListContainsAuth(List<Auth> list,String authCode){	
		if(list==null)
			return -1;
		for(int i=0;i<list.size();i++){
			if(list.get(i).getAuthCode().equals(authCode))
				return i;
		}
		return -1;
	}
	
	/**
	 * 获取父权限列表(公司的父权限)
	 * @param userId 用户id
	 * @return {@link Result} 结果=list
	 */
	@At("auth/getUserFatherAuth")
	@Ok("json")
	public Result getUserFatherAuth(){
		Result re = new Result();
		try {
			List<Auth> all=dao.query(Auth.class,Cnd.where("fatherCode","=",0).and("authCode","!=",SystemAuthCode.auth_sys));
			re.setList(all);
			re.setTotal(all.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			re.setInfo("操作出错:"+e.getMessage());
			re.setStatus(Params.status_failed);
		}								
		return re;
	}
	
	/**
	 * 获取用户已有的权限列表(包含所在角色已有的权限信息)
	 * @param userId 用户Id
	 * @return {@link Result}
	 */
	@At("auth/getUserAuthSelected")
	@Ok("json")
	public Result getUserAuthSelected(@Param("userId")long userId){
		Result re=new Result();		
		try {
			User u=dao.fetch(User.class,userId);
			if(u==null){
				re.setInfo("用户信息为空");
				re.setStatus(Params.status_failed);
				return re;
			}
			List<Auth> list=new LinkedList<Auth>();
			List<UserAuth> ualist=dao.query(UserAuth.class, Cnd.where("userId","=",userId));
			for(UserAuth ua:ualist){
				Auth au=dao.fetch(Auth.class,Cnd.where("authCode","=",ua.getAuthCode()));
				if(au!=null){
					if(authListContainsAuth(list, au.getAuthCode())==-1){
						list.add(au);
					}
				}							
			}
			re.setInfo("操作成功");
			re.setStatus(Params.status_success);
			re.setList(list);
			re.setTotal(list.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			re.setInfo("数据库操作出错:"+e.getMessage());
			re.setStatus(Params.status_failed);
		}
		return re;
	}
	
	/**
	 * 获取用户未有的子权限列表
	 * @param userId 用户id
	 * @return {@link Result} 结果=list
	 */
	@SuppressWarnings("unchecked")
	@At("auth/getUserSonAuthNotSelected")
	@Ok("json")
	public Result getUserSonAuthNotSelected(@Param("userId")long userId,@Param("fatherCode")String fatherCode){
		Result re=getUserAuthSelected(userId);
		if(re.getStatus()==Params.status_success){			
			try {
				List<Auth> selects=re.getList();
				List<Auth> all = new ArrayList<Auth>();
				if(fatherCode == null)
					fatherCode = "";
			    all =dao.query(Auth.class, Cnd.where("fatherCode","=",fatherCode));
				for(Auth au:selects){
					int index=authListContainsAuth(all, au.getAuthCode());
					if(index!=-1){
						all.remove(index);
					}
				}
				re.setList(all);
				re.setTotal(all.size());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				re.setInfo("操作出错:"+e.getMessage());
				re.setStatus(Params.status_failed);
			}						
		}		
		return re;
	}
	
	/**
	 * 获取用户已有的子权限列表
	 * @param userId 用户id
	 * @return {@link Result} 结果=list
	 */
	@SuppressWarnings("unchecked")
	@At("auth/getUserSonAuthSelected")
	@Ok("json")
	public Result getUserSonAuthSelected(@Param("userId")long userId,String fatherCode){
		Result re=getUserAuthSelected(userId);
		if(re.getStatus()==Params.status_success){			
			try {
				List<Auth> haveSelect=new ArrayList<Auth>();
				List<Auth> selects=re.getList();
				List<Auth> all = new ArrayList<Auth>();
				if(fatherCode == null)
					fatherCode = "";
			    all =dao.query(Auth.class, Cnd.where("fatherCode","=",fatherCode));
				for(Auth au:selects){
					int index=authListContainsAuth(all, au.getAuthCode());
					if(index!=-1){
						haveSelect.add(au);
					}
				}
				re.setList(haveSelect);
				re.setTotal(haveSelect.size());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				re.setInfo("操作出错:"+e.getMessage());
				re.setStatus(Params.status_failed);
			}						
		}		
		return re;
	}
	/**
	 * 获取角色没有选择的子权限列表
	 * @param roleId 角色ID
	 * @return
	 */
	@At("auth/getRoleSonAuthNotSelected")
	@Ok("json")
	public Result getRoleSonAuthNotSelected(@Param("roleId")long roleId,String fatherCode){
		Result re=new Result();
		try {
			List<Auth> all=dao.query(Auth.class, Cnd.where("fatherCode","=",fatherCode));
			List<RoleAuth> ralist=dao.query(RoleAuth.class, Cnd.where("roleId","=",roleId));
			for(RoleAuth ra:ralist){
				int index=authListContainsAuth(all, ra.getAuthCode());
				if(index!=-1){
					all.remove(index);							
				}
			}			
			re.setInfo("操作成功");
			re.setStatus(Params.status_success);
			re.setList(all);
			re.setTotal(all.size());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			re.setInfo("操作出错:"+e.getMessage());
			re.setStatus(Params.status_failed);
			re.setList(new LinkedList<Auth>());
			re.setTotal(0);
			return re;
		}		
		return re;
	}
	
	/**
	 * 获取角色已选择子权限列表
	 * @param fatherCode 父权限编码
	 * @param roleId 角色id
	 * @return
	 */
	@At("auth/getRoleSonAuthList")
	@Ok("json")
	public Result getRoleSonAuthList(@Param("roleId")long roleId,String fatherCode){
		Result re=new Result();
		try {
			List<RoleAuth> list=dao.query(RoleAuth.class,Cnd.where("roleId","=",roleId));
			List<Auth> authlist=new LinkedList<Auth>();
			for(RoleAuth ra:list){
				Auth au=dao.fetch(Auth.class,Cnd.where("authCode","=",ra.getAuthCode()).and("fatherCode","=",fatherCode));
				if(au!=null)
					authlist.add(au);
			}
			re.setInfo("操作成功");
			re.setStatus(Params.status_success);
			re.setList(authlist);
			re.setTotal(authlist.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			re.setInfo("数据库操作出错:"+e.getMessage());
			re.setStatus(Params.status_failed);
			re.setList(new LinkedList<Auth>());
			re.setTotal(0);
		}		
		
		return re;
	}
	
	/**
	* @Title: addRoleAuth   
	* @Description: 为对象添加角色   
	* @param roleId 角色id
	* @param authCodes 权限编码
	* @return Result     
	 */
	@At("auth/addRoleAuth")
	@Ok("json")
	public Result addRoleAuth(@Param("roleId") long roleId,@Param("authCodes") String authCodes) {
		Result re=new Result();
		if(roleId==0 || service.isEmpty(authCodes)){
			re.setInfo("参数为空");
			re.setStatus(Params.status_failed);
		}
		else{
			String[] authcode=authCodes.split(",");
			try {
				List<User> users=dao.query(User.class, Cnd.where("roleId","=",roleId));
				for(int i=0;i<authcode.length;i++){
					if(dao.count(RoleAuth.class,Cnd.where("roleId","=",roleId).and("authCode","=",authcode[i]))==0){
						RoleAuth ra=new RoleAuth();
						ra.setAuthCode(authcode[i]);
						ra.setRoleId(roleId);
						dao.insert(ra);
						//为属于该角色的用户，添加对应权限信息
						for(User u:users){
							UserAuth ua=new UserAuth();
							ua.setAuthCode(authcode[i]);
							ua.setUserId(u.getId());
							dao.insert(ua);
						}
						
						//添加子权限信息
						List<Auth> son_auth=dao.query(Auth.class, Cnd.where("fatherCode","=",ra.getAuthCode()));
						for(Auth au:son_auth){
							RoleAuth son_ra=new RoleAuth();
							son_ra.setAuthCode(au.getAuthCode());
							son_ra.setRoleId(roleId);
							dao.insert(son_ra);
							
							for(User u:users){
								UserAuth ua=new UserAuth();
								ua.setAuthCode(au.getAuthCode());
								ua.setUserId(u.getId());
								dao.insert(ua);
							}
						}
					}
				}
				re.setInfo(Params.str_optSuccess);
				re.setStatus(Params.status_success);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				re.setStatus(Params.status_failed);
				re.setInfo("操作出错:"+e.getMessage());
			}			
		}
		
		return re;
	}
	
	/**
	 * 删除角色权限，同时删除属于该角色的用户对应权限信息
	 */
	@At("auth/delRoleAuth")
	@Ok("json")
	public Result delRoleAuth(@Param("roleAuthCodes") String roleAuthCodes,@Param("roleId")long roleId) {	
		Result re=new Result();
		if(roleId==0 || service.isEmpty(roleAuthCodes)){
			re.setInfo("参数为空");
			re.setStatus(Params.status_failed);
		}
		else{
			String[] roleAuthcodes=roleAuthCodes.split(",");			
			try {			
				List<User> users_role=dao.query(User.class, Cnd.where("roleId","=",roleId));						
				for(int i=0;i<roleAuthcodes.length;i++){
					if(!service.isEmpty(roleAuthcodes[i])){
						RoleAuth ra=dao.fetch(RoleAuth.class,Cnd.where("authCode","=",roleAuthcodes[i]).and("roleId","=",roleId));
						if(ra!=null){
							List<Auth> son_auth=dao.query(Auth.class, Cnd.where("fatherCode","=",ra.getAuthCode()));						
							
							//删除子权限相关记录:角色子权限和对应用户子权限
							for(Auth au:son_auth){
								RoleAuth son_ra=dao.fetch(RoleAuth.class,Cnd.where("roleId","=",roleId).and("authCode","=",au.getAuthCode()));							
								for(User u:users_role){
									UserAuth son_ua=dao.fetch(UserAuth.class,Cnd.where("userId","=",u.getId()).and("authCode","=",au.getAuthCode()));
									dao.delete(son_ua);
								}
								dao.delete(son_ra);
							}
							
							//删除权限：角色权限和对应用户权限
							for(User u:users_role){
								UserAuth ua=dao.fetch(UserAuth.class,Cnd.where("userId","=",u.getId()).and("authCode","=",ra.getAuthCode()));
								dao.delete(ua);
							}						
							dao.delete(ra);
						}
						
					}
				}
				re.setInfo("操作成功");
				re.setStatus(Params.status_success);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				re.setInfo("操作出错:"+e.getMessage());
				re.setStatus(Params.status_failed);
				return re;
				
			}
			
		}
		
		return re;
	}
	
	@At("auth/addRole")
	@Ok("json")
	public Result addRole(@Param("..") Role role,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
		if(role==null || service.isEmpty(role.getRoleName())){
			re.setInfo("参数为空");
			re.setStatus(Params.status_failed);
			return re;
			}
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"com_addRole", dao)){
            		role.setCanDel(true);
            		role.setAuthor(user.getUsername());
            		role.setAuthorId(user.getId());
            		role.setCompanyNo(user.getCompanyNo());
            		role.setDateCreater(new Date());
            		dao.fastInsert(role);
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
            	}else {
                    re.setInfo(Params.str_noRequestAuth);
                    re.setStatus(Params.status_failed);
				}
            }
        }
        else{
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}

	@At("auth/delRole")
	@Ok("json")
	public Result delRole(@Param("roleIds") String roleIds,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"com_delRole", dao)){
        			String [] id=roleIds.split(",");
        			for(int i=0;i<id.length;i++){
        				if(!id[i].equals("")){
        					Role role=dao.fetch(Role.class,Cnd.where("id","=",id[i]));
        					if(role!=null){
        						List<RoleAuth> list=dao.query(RoleAuth.class, Cnd.where("roleId","=",id[i]));
        						for(RoleAuth ra:list){
        							dao.clear(RoleAuth.class, Cnd.where("id","=",ra.getId()));
        						}
        						dao.clear(Role.class, Cnd.where("id","=",id[i]));
        					}
        				}
        			}
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
            	}else {
                    re.setInfo(Params.str_noRequestAuth);
                    re.setStatus(Params.status_failed);
				}
            }
        }
        else{
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	/**
	 * 获取我的权限列表(包含所在角色已有的权限信息)
	 * @return {@link Result}
	 */
	@At("auth/getMyAuthList")
	@Ok("json")
	public Result getMyAuthList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();	
		 if(MyUtils.checkRequestOk(acu, session)){
	            User user=service.getUserInfo(acu, session);
	            if(user==null){
	                re.setInfo(Params.loginOutInfoStr);
	                re.setStatus(Params.status_failed);
	            }else {
	            	if(AuthUtils.checkUserAuth(user.getId(),"p_getMyAuth", dao)){
	            		try {
	            			List<Auth> list=new ArrayList<Auth>();
	            			List<Auth> alist=new ArrayList<Auth>();
	            			List<UserAuth> ualist=dao.query(UserAuth.class, Cnd.where("userId","=",user.getId()));
	            			for(UserAuth ua:ualist){
	            				Auth au=dao.fetch(Auth.class,Cnd.where("authCode","=",ua.getAuthCode()));
	            				if(au!=null){
	            					if(authListContainsAuth(list, au.getAuthCode())==-1){
	            						list.add(au);
	            					}
	            				}							
	            			}
	            			int currIdx = (pageNo > 1 ? (pageNo -1) * pageSize : 0);
	            			for (int i = 0; i < pageSize &&i < list.size()- currIdx; i++) {
	            				Auth auth = list.get(currIdx+i);
	            				alist.add(auth);
							}
	            			re.setInfo("操作成功");
	            			re.setStatus(Params.status_success);
	            			re.setList(alist);
	            			re.setTotal(list.size());
	            		} catch (Exception e) {
	            			// TODO: handle exception
	            			e.printStackTrace();
	            			re.setInfo("数据库操作出错:"+e.getMessage());
	            			re.setStatus(Params.status_failed);
	            		}
	            	}
				} 
		 }
		return re;
	}
}
