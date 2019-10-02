package com.yjg.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.JspView;

import com.yjg.AppService;
import com.yjg.entity.Auth;
import com.yjg.entity.RoleAuth;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;

/**
 * 系统管理模块
 * 访问路径 sys/***
 * @author yjg
 *
 */
@IocBean
@InjectName("systemModule")
public class SystemModule {

	/**
	 * 数据库操作
	 */
	@Inject
	private Dao dao;

	@Inject
	private AppService service;
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	/**
	 * 用户登录-手机端登录
	 * @param username 用户名
	 * @param password 登录密码
	 * @param appKey 登录密钥
	 * @return {@link Result} 如果登录成功,result.obj=user,result.list=当前用户的权限列表
	 */
	@At
	@Ok("json")
	public Result loginApp(String username,String password,String appKey,HttpServletRequest req){
		Result re=new Result();		
		if(Strings.isEmpty(username)|| Strings.isEmpty(password)){
			re.setStatus(Params.status_failed);
			re.setInfo("登录名或密码为空");
		}
		else{
			int userCount=dao.count(User.class,Cnd.where("username","=",username).and("isDeleted","=",Params.status_notDeleted).and("status","=",Params.status_run));
			User u=null;
			if(userCount==0){
				userCount=dao.count(User.class,Cnd.where("userID","=",username).and("isDeleted","=",Params.status_notDeleted).and("status","=",Params.status_run));
				if(userCount==1){
					u=dao.fetch(User.class,Cnd.where("userID","=",username).and("password","=",MyUtils.changeStr(password)));
				}else {
					re.setStatus(Params.status_failed);
					re.setInfo("该用户不存在");
				}
			}
			else if(userCount==1){
				u=dao.fetch(User.class,Cnd.where("username","=",username).and("password","=",MyUtils.changeStr(password)));
			}
			else{
				re.setStatus(Params.status_failed);
				re.setInfo("用户名有重名,请用用户工号登录");
			}
			
			if(u!=null){
				u.setLastIP(req.getRemoteAddr());
				u.setLastDate(new Date());
				u.setLoginCount(u.getLoginCount()+1);				
				dao.update(u,"lastIP|lastDate|loginCount");				
				//添加app登录信息到缓存
				AppHttpUtils acu=new AppHttpUtils(u.getId(),appKey);
				acu.setUser(u);
				ServiceData.addAppKey(acu);	
				
				u.setPassword(""+acu.getId());
				
				List<UserAuth> ualist=dao.query(UserAuth.class, Cnd.where("userId","=",u.getId()));
				List<RoleAuth> ralist=dao.query(RoleAuth.class, Cnd.where("roleId","in",u.getRoleId()));
				List<String> authlist=new LinkedList<String>();
				for(UserAuth ua:ualist){
					if(!authlist.contains(ua.getAuthCode())){
						authlist.add(ua.getAuthCode());
					}
				}
				for(RoleAuth ra:ralist){
					if(!authlist.contains(ra.getAuthCode())){
						authlist.add(ra.getAuthCode());
					}
				}
				
				re.setInfo(Params.str_optSuccess);
				re.setStatus(Params.status_success);
				re.setObj(u);
				re.setList(authlist);
			}
			else{
				re.setStatus(Params.status_failed);
				re.setInfo("用户名和密码不匹配");
			}
		}		
		return re;
	}
	
	/**
	 * 用户登录-网页端登录(支持用户名和用户编码登录)
	 * @param username 登录名
	 * @param password 登录密码
	 * @param loginkey 登录密钥(即时性，用过即废除)
	 * @return {@link Result} 如果登录成功,result.obj=user,其中密码已处理
	 */
	@At
	@Ok("redirect:/index")
	public Object loginWeb(String username,String password,HttpServletRequest req){						
		boolean isSuccess=false;		
		if(Strings.isEmpty(username) || Strings.isEmpty(password)){
			req.setAttribute("obj", "用户名或密码为空");		
		}
		else{			
								
			int userCount=dao.count(User.class,Cnd.where("username","=",username).and("isDeleted","=",Params.status_notDeleted).and("status","=",Params.status_run));
			User u=null;
			if(userCount==0){//用户名登录失败,用工号登录
				userCount=dao.count(User.class,Cnd.where("userID","=",username).and("isDeleted","=",Params.status_notDeleted).and("status","=",Params.status_run));
				if(userCount==1){
					u=dao.fetch(User.class,Cnd.where("userID","=",username).and("password","=",MyUtils.changeStr(password)));
				}else {
					req.setAttribute("obj", "用户不存在");
				}
			}
			else if(userCount==1){
				u=dao.fetch(User.class,Cnd.where("username","=",username).and("password","=",MyUtils.changeStr(password)));
			}
			else{
				req.setAttribute("obj", "用户名有重名,请用身份证号/工号登录");
			}
			if(u!=null){
				u.setLastIP(req.getRemoteAddr());
				u.setLastDate(new Date());
				u.setLoginCount(u.getLoginCount()+1);
				dao.update(u,"lastIP|lastDate|loginCount");
				u.setPassword("");
				req.getSession().setAttribute(Params.loginUserInSessionStr, u);	
				req.getSession().setAttribute("username", u.getUsername());
				isSuccess=true;
			}
			else{
				req.setAttribute("obj", "用户名和密码不匹配");				
			}
		}						
		if(isSuccess){			
			return null;
		}
		else{//登录失败			
			return new JspView("/index");
		}
	}
	
	/**
	 * 获取session 中用户信息
	 * @param session httpSession
	 * @return {@link Result} result.obj=用户信息，password已处理
	 */
	@At("sys/getUserInSession")
	@Ok("json")
	public Result getUserInSession(HttpSession session){
		Result re=new Result();
		User u=AppService.getLoginUserInSession(session);
		re.setObj(u);
		re.setInfo(Params.str_optSuccess);
		re.setStatus(Params.status_success);
		return re;
	}
	
	/**
	 * 获取用户权限信息
	 * @param acu
	 * @param session
	 * @return obj={@link User},list=该用户的权限列表
	 */
	@At("sys/getUserAuthInfo")
	@Ok("json")
	public Result getUserAuthInfo(@Param("::acu.")AppHttpUtils acu, HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	user.setPassword("********");
                	List<UserAuth> ualist=dao.query(UserAuth.class, Cnd.where("userId","=",user.getId()));                	
                	List<String> list=new LinkedList<String>();
                	for(UserAuth ua:ualist){
                		if(MyUtils.containString(list, ua.getAuthCode())==-1){
                			list.add(ua.getAuthCode());
                		}
                	}                	
                	re.setList(list);
                	re.setObj(user);
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
                } catch (Exception e) {
                    // TODO: handle exception
                    re.setInfo("操作出错:"+e.toString());
                    re.setStatus(Params.status_failed);
                    ServiceData.addDataWaitingInsert(new SystemLog(this.getClass().getName(), e.toString(), "", ""));
                }
            }
        }
        else{
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}	
	
	/**
	 * 获取系统日志列表
	 * @param pageNo 开始页码
	 * @param pageSize  每页大小
	 * @param total 总记录数
	 * @param session
	 * @return
	 */
	@At("sys/getSysLogList")
	@Ok("json")
	public Result getSysLogList(int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();		
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(pageNo<=0)
                        pageNo=Params.pageNoInit;
                    if(pageSize<=0)
                        pageSize=Params.pageSizeDefault;
                    Pager pager=dao.createPager(pageNo, pageSize);
                    List<SystemLog> list=dao.query(SystemLog.class, Cnd.where(null).desc("date"),pager);
                    if(total<=0)
                    	total=dao.count(SystemLog.class);                    
                    re.setList(list);
                    re.setTotal(total);
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
                } catch (Exception e) {
                    // TODO: handle exception
                    re.setInfo("操作出错:"+e.toString());
                    re.setStatus(Params.status_failed);
                    ServiceData.addDataWaitingInsert(new SystemLog(this.getClass().getName(), e.toString(), "", ""));
                }
            }
        }
        else{
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 检索系统日期
	 * @param startdate 开始日期
	 * @param enddate 结束日期
	 * @param pageNo 开始页码
	 * @param pageSize  每页大小
	 * @param total 总记录数
	 * @param session
	 * @return
	 */
	@At("sys/soSysLogList")
	@Ok("json")
	public Result soSysLogList(String startdate,String enddate,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();		
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(pageNo<=0)
                        pageNo=Params.pageNoInit;
                    if(pageSize<=0)
                        pageSize=Params.pageSizeDefault;
                    Pager pager=dao.createPager(pageNo, pageSize);
                    SqlExpressionGroup sql=null;
                    if(!MyUtils.isEmpty(startdate)){
                    	sql=Cnd.exps("date",">=",startdate+" 00:00:00");                    	
                    }
                    if(!MyUtils.isEmpty(enddate)){
                    	if(sql==null)
                    		sql=Cnd.exps("date","<=",enddate+" 23:59:59");
                    	else
                    		sql=sql.and("date","<=",enddate+" 23:59:59");
                    }
                    List<SystemLog> list=dao.query(SystemLog.class, Cnd.where(sql).desc("date"),pager);
                    if(total<=0)
                    	total=dao.count(SystemLog.class,Cnd.where(sql));                    
                    re.setList(list);
                    re.setTotal(total);
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
                } catch (Exception e) {
                    // TODO: handle exception
                    re.setInfo("操作出错:"+e.toString());
                    re.setStatus(Params.status_failed);
                    ServiceData.addDataWaitingInsert(new SystemLog(this.getClass().getName(), e.toString(), "", ""));
                }
            }
        }
        else{
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	/**
	 * 获取未拥有权限列表(前端移除相应的div)
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("sys/getAuthUnauthed")
	@Ok("json")
	public Result getAuthUnauthed(AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	FieldFilter ff = FieldFilter.create(Auth.class, "^authCode$");
                	FieldFilter ff2 = FieldFilter.create(UserAuth.class, "^authCode$");
                	List<Auth> list = Daos.ext(dao, ff).query(Auth.class,Cnd.where("fatherCode","!=", 0));
                	List<UserAuth> hadList = Daos.ext(dao, ff2).query(UserAuth.class, Cnd.where("userId","=",user.getId()));
    				for(UserAuth au:hadList){
    					int index=AuthModule.authListContainsAuth(list, au.getAuthCode());
    					if(index!=-1){
    						list.remove(index);
    					}
    				}
                	re.setList(list);
                    re.setInfo(Params.str_optSuccess);
                    re.setStatus(Params.status_success);
                } catch (Exception e) {
                    // TODO: handle exception
                    re.setInfo("操作出错:"+e.toString());
                    re.setStatus(Params.status_failed);
                    ServiceData.addDataWaitingInsert(new SystemLog(this.getClass().getName(), e.toString(), "", ""));
                }
            }
        }
        else{
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
}
