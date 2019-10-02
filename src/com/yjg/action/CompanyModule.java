package com.yjg.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.yjg.AppService;
import com.yjg.entity.Auth;
import com.yjg.entity.BusinessLog;
import com.yjg.entity.Company;
import com.yjg.entity.Role;
import com.yjg.entity.RoleAuth;
import com.yjg.entity.StockPlace;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.entity.UserStockPlace;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.ComboUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;
import com.yjg.utils.SystemAuthCode;
import com.yjg.utils.SystemRoles;
import com.yjg.utils.UniqueNoUtils;

/**
 * 公司模块
 * 访问路径 com/***
 * @author lch
 *
 */
@IocBean
@InjectName("companyModule")
public class CompanyModule {
	
	@Inject
	private Dao dao;	
	
	@Inject
	private AppService service;
	
	/**
	 * 获取公司唯一编号
	 * @return string
	 */
	@At("com/getCompanyNo")
	@Ok("json")
	public String getCompanyNo(){
		return UniqueNoUtils.createComNo();
	}
	
	/**
	 * 获取公司列表
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/getCompanyList")
	@Ok("json")
	public Result getCompanyList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("::acu.")AppHttpUtils acu,HttpSession session){

		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"sys_getComList", dao)){
            		List<Company> list=dao.query(Company.class,Cnd.where("isDeleted","=",Params.status_notDeleted),dao.createPager(pageNo, pageSize));
            		int total=dao.count(Company.class,Cnd.where("isDeleted","=",Params.status_notDeleted));
            		re.setList(list);
            		re.setTotal(total);
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
	 * 重置账号密码 删除员工
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @param userIds 被操作者 id 
	 * @param opt -1 删除 公司员工  0 超级管理员重置公司管理员密码 1公司管理员重置员工密码
	 * @return {@link Result} 
	 */
	@At("com/resetOrDel")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result resetUserPwd(@Param("userIds")String userIds,@Param("opt") int opt,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
    		if(userIds==null || userIds.equals("")){
                re.setInfo(Params.info_errr_null);
                re.setStatus(Params.status_failed);
                return re;
    		}
			if(opt==-1){//删除员工
	            if(!AuthUtils.checkUserAuth(user.getId(),"com_delEmployee", dao)){
	                re.setInfo(Params.str_noRequestAuth);
	                re.setStatus(Params.status_failed);
	                return re;
	            }
	            String [] id=userIds.split(",");
	            for(int i=0;i<id.length;i++){
	            	if(!id[i].equals("")){
	            		User u=dao.fetch(User.class,Cnd.where("id","=",id[i]));
	            		if(u!=null){
	            			if(u.getRoleId()==2){
	                            re.setInfo("公司管理员不被删除！！");
	                            re.setStatus(Params.status_failed);
	                            return re;
	            			}
	        				u.setDeleted(true);
	        				u.setDateUpdater(new Date());
	        				dao.update(u);	
	            		}
	            	}
	            }
			}else if(opt==0) {//超级管理员重置公司管理员密码
	                if(!AuthUtils.checkUserAuth(user.getId(),"sys_resetComPwd", dao)){
	                    re.setInfo(Params.str_noRequestAuth);
	                    re.setStatus(Params.status_failed);
	                    return re;
	                }
		            String [] id=userIds.split(",");
		            for(int i=0;i<id.length;i++){
		            	if(!id[i].equals("")){
		            		User u=dao.fetch(User.class,Cnd.where("id","=",id[i]));
		            		if(u!=null){
		            			u.setPassword(MyUtils.dbChangeStr(Params.password_default));
		            			u.setDateUpdater(new Date());
		            			dao.update(u);	
		            		}
		            	}
		            }
			}
			else if(opt==1) {//公司管理员重置员工密码
	            if(!AuthUtils.checkUserAuth(user.getId(),"com_resetPwd", dao)){
	                re.setInfo(Params.str_noRequestAuth);
	                re.setStatus(Params.status_failed);
	                return re;
	            }
	            String [] id=userIds.split(",");
	            for(int i=0;i<id.length;i++){
	            	if(!id[i].equals("")){
	            		User u=dao.fetch(User.class,Cnd.where("id","=",id[i]));
	            		if(u!=null){
	            			u.setPassword(MyUtils.dbChangeStr(Params.password_default));
	            			u.setDateUpdater(new Date());
	            			dao.update(u);	
	            		}
	            	}
	            }
		}
            re.setInfo(Params.str_optSuccess);
            re.setStatus(Params.status_success);
            }
        else{
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 搜索公司员工列表
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/soComUserList")
	@Ok("json:{locked:'password',ignoreNull:true}")
	public Result soComUserList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("nameKey")String nameKey,@Param("IDKey")String IDKey,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"com_getEmployee", dao)){
                		SqlExpressionGroup sql=Cnd.exps("username", "like", "%"+nameKey+"%").and("userID", "like",  "%"+IDKey+"%").and("companyNo", "=", user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted);
                		List<User> list = dao.query(User.class,Cnd.where(sql),dao.createPager(pageNo, pageSize));
                    	re.setList(list);
                    	re.setTotal(dao.count(User.class,Cnd.where(sql)));
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
                	}else {
                        re.setInfo(Params.str_noRequestAuth);
                        re.setStatus(Params.status_failed);
					}
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
	
	/**
	 * 更新公司信息及管理员信息
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/updateCompany")
	@Ok("json")
	public Result updateCompany(@Param("..")Company company,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"sys_updateCom", dao)){
            		User comAdmin = dao.fetch(User.class, Cnd.where("companyNo","=",company.getNo()));
            		comAdmin.setDateUpdater(new Date());
            		comAdmin.setPhone(company.getAdminTel());
            		comAdmin.setUsername(company.getAdmin());
            		comAdmin.setUserID(company.getUserID());
            		dao.update(comAdmin);
            		Company thisCompany = dao.fetch(Company.class,Cnd.where("no","=",company.getNo()));
            		thisCompany.setAddress(company.getAddress());
            		thisCompany.setAdmin(company.getAdmin());
            		thisCompany.setAdminTel(company.getAdminTel());
            		thisCompany.setContent(company.getContent());
            		thisCompany.setTel(company.getTel());
            		thisCompany.setDateUpdater(new Date());
            		thisCompany.setName(company.getName());
            		thisCompany.setUserID(company.getUserID());
            		dao.update(thisCompany);
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
	 * 删除公司 停止开启公司服务
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/companyOpt")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result companyOpt(@Param("companyIds")String companyIds,@Param("opt") int opt,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"sys_delCom", dao)){
            		if(companyIds==null || companyIds.equals("")){
                        re.setInfo(Params.info_errr_null);
                        re.setStatus(Params.status_failed);
            		}
            		else{
            			String [] id=companyIds.split(",");
            			for(int i=0;i<id.length;i++){
            				if(!id[i].equals("")){
            					Company company=dao.fetch(Company.class,Cnd.where("id","=",id[i]));
            					if(company!=null){
            						Date date =new Date();
            						if(opt==-1){//删除公司
            							company.setDeleted(true);
            							company.setDateUpdater(date);
            							dao.update(company);
            							List<User> list = dao.query(User.class,Cnd.where("companyNo","=",company.getNo()));
            							for (User p : list) {
											p.setDeleted(true);
											p.setDateUpdater(date);
											dao.update(p);
										}
            						}else if(opt==1) {//开启公司服务
            							company.setComstatus(1);;
            							company.setDateUpdater(date);
            							dao.update(company);
            							List<User> list = dao.query(User.class,Cnd.where("companyNo","=",company.getNo()));
            							for (User p : list) {
											p.setStatus(1);;
											p.setDateUpdater(date);
											dao.update(p);
										}
									}else if (opt==0) {//停止公司服务
	         							company.setComstatus(-1);;
            							company.setDateUpdater(date);
            							dao.update(company);
            							List<User> list = dao.query(User.class,Cnd.where("companyNo","=",company.getNo()));
            							for (User p : list) {
											p.setStatus(-1);;
											p.setDateUpdater(date);
											dao.update(p);
										}
									}
            					}
            				}
            			}
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
            		}
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
	 * 添加公司 并创建公司唯一管理员账号
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/addCompany")
	@Ok("json")
	public Result addCompany(@Param("..")Company company,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"sys_addCom", dao)){
            		User comadmin = new User();
            		comadmin.setAuthor(user.getUsername());
            		comadmin.setAuthorId(user.getId());
            		comadmin.setDateCreater(new Date());
            		comadmin.setCompanyNo(company.getNo());
            		comadmin.setPhone(company.getAdminTel());
            		comadmin.setUsername(company.getAdmin());
            		comadmin.setRoleId(2);
            		comadmin.setUserID(company.getUserID());
            		comadmin.setRoleName(SystemRoles.role_comAdmin);
            		comadmin.setPassword(MyUtils.dbChangeStr(Params.password_default));
            		comadmin.setStatus(1);
            		dao.fastInsert(comadmin);
            		User thisAdmin = dao.fetch(User.class,Cnd.where("userID","=",comadmin.getUserID()).and("companyNo","=",comadmin.getCompanyNo()));
            		company.setAuthor(user.getUsername());
            		company.setAuthorId(user.getId());
            		company.setDateCreater(new Date());
            		company.setAdminId(thisAdmin.getId());
            		AuthUtils.addUserAuthByRole(user, thisAdmin.getId(),thisAdmin.getRoleId(),dao);
            		company.setComstatus(1);
            		initComRoles(company.getNo());
            		dao.fastInsert(company);
            		StockPlace sPlace = new StockPlace();
            		sPlace.setExactAddr("略");
            		sPlace.setStockplace("默认仓库");
            		sPlace.setRemark("公司建立时初始化的仓库");
            		sPlace.setAuthor("系统");
            		sPlace.setAuthorId(0);
            		sPlace.setCompanyNo(company.getNo());
            		sPlace.setDateCreater(new Date());
            		dao.fastInsert(sPlace);
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
	 * 获取公司所有员工列表
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/getComUserList")
	@Ok("json:{locked:'password',ignoreNull:true}")
	public Result getComUserList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"com_getEmployee", dao)){
                    	List<User> list=dao.query(User.class,Cnd.where("companyNo", "=", user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted),dao.createPager(pageNo, pageSize));
                    	Collections.reverse(list);
                    	re.setList(list);
                    	re.setTotal(dao.count(User.class,Cnd.where("companyNo", "=", user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted)));
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
                	}else {
                        re.setInfo(Params.str_noRequestAuth);
                        re.setStatus(Params.status_failed);
					}
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
	/**
	 * 添加员工
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/addComUser")
	@Ok("json")
	public Result addComUser(@Param("..")User person,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            if(dao.count(User.class, Cnd.where("userID","=", person.getUserID()))!=0){
                re.setInfo("该工号已存在");
                re.setStatus(Params.status_failed);
                return re;
            }
            if(person.getUsername().isEmpty()||person.getRoleId()==0||person.getPhone().isEmpty()||person.getUserID().isEmpty()){
                re.setInfo("参数不能为空！");
                re.setStatus(Params.status_failed);
                return re;
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"com_addEmployee", dao)){
                    	person.setAuthor(user.getUsername());
                    	person.setAuthorId(user.getId());
                    	person.setCompanyNo(user.getCompanyNo());
                    	person.setDateCreater(new Date());
                    	person.setPassword(MyUtils.dbChangeStr(Params.password_default));
                    	person.setStatus(1);
                    	dao.fastInsert(person);
                    	User thisPerson = dao.fetch(User.class,Cnd.where("userID", "=", person.getUserID()).and("companyNo","=",person.getCompanyNo()));
                    	AuthUtils.addUserAuthByRole(user, thisPerson.getId(), thisPerson.getRoleId(), dao);
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
                	}else {
                        re.setInfo(Params.str_noRequestAuth);
                        re.setStatus(Params.status_failed);
					}
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
	
	/**
	 * 自动初始化公司角色
	 */
	private void initComRoles(String companyNo){
		Date date =new Date();
		Role role =new Role();
		role.setCanDel(true);
		role.setCompanyNo(companyNo);
		role.setDateCreater(date);
		role.setRoleName("普通员工");
		role.setRemark("通用公司角色，可修改权限");
		role.setAuthor("系统");
		role.setAuthorId(0);
		dao.fastInsert(role);
		/**
		 * 初始化角色权限
		 */
		Role comEmp=dao.fetch(Role.class,Cnd.where("rolename","=","普通员工"));//普通员工
		RoleAuth ra=new RoleAuth();
		List<Auth> comAuths = dao.query(Auth.class, Cnd.where("fatherCode","=",SystemAuthCode.auth_person));
		for (Auth auth : comAuths) {
			ra.setAuthCode(auth.getAuthCode());
			ra.setAuthor("系统");
			ra.setAuthorId(0);
			ra.setDateCreater(date);
			ra.setRoleId(comEmp.getId());
			ra.setCompanyNo(companyNo);
			dao.fastInsert(ra);
		}
	}
	/**
	 * 获取公司角色列表
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("com/getComRolesList")
	@Ok("json")
	public Result getComRolesList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"com_getRoleList", dao)){
                    	List<Role> list=dao.query(Role.class,Cnd.where("companyNo", "=", user.getCompanyNo()),dao.createPager(pageNo, pageSize));
                    	re.setList(list);
                    	re.setTotal(dao.count(Role.class,Cnd.where("companyNo", "=", user.getCompanyNo())));
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
                	}else {
                        re.setInfo(Params.str_noRequestAuth);
                        re.setStatus(Params.status_failed);
					}
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
	/**
	 * 获取角色列表-combo封装
	 * @return
	 */
	@At("com/getRoleListCombo")
	@Ok("json")
	public Result getRoleListCombo(@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
    	List<ComboUtils> rolelist=new LinkedList<ComboUtils>();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	List<Role> list=dao.query(Role.class, Cnd.where("companyNo","=",user.getCompanyNo()));
            	for(Role role:list){
            		ComboUtils cu=new ComboUtils();
            		cu.setText(role.getRoleName());
            		cu.setValue(role.getId()+"");
            		rolelist.add(cu);
            	}
            	re.setList(rolelist);
                re.setInfo(Params.str_optSuccess);
                re.setStatus(Params.status_success);     	
            }
        }
        return re;
	}
	
	/**
	 * 获取仓库列表
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("com/getStockList")
	@Ok("json")
	public Result getStockInList(@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"com_getStockList", dao)){
                    	List<StockPlace> list = dao.query(StockPlace.class, Cnd.where("companyNo","=", user.getCompanyNo()));
                    	re.setList(list);
                    	re.setTotal(list.size());
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
                	}else {
                        re.setInfo(Params.str_noRequestAuth);
                        re.setStatus(Params.status_failed);
					}
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
	 * 获取我的仓库列表-combo封装
	 * @return
	 */
	@At("com/getMyStockListCombo")
	@Ok("json")
	public Result getStockListCombo(@Param("::acu.")AppHttpUtils acu,HttpSession session){
    	List<ComboUtils>stocklist=new LinkedList<ComboUtils>();
    	Result re =new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);      	
            }
            else{
            	List<UserStockPlace> list=dao.query(UserStockPlace.class, Cnd.where("companyNo","=",user.getCompanyNo()).and("userId","=",user.getId()));
            	for(UserStockPlace usp:list){
            		ComboUtils cu=new ComboUtils();
            		cu.setText(usp.getStockplace());
            		cu.setValue(usp.getStockplaceId()+"");
            		stocklist.add(cu);
            	}
            	re.setList(stocklist);
                re.setInfo(Params.str_optSuccess);
                re.setStatus(Params.status_success);
            }
        }
        return re;
	}
	
	/**
	 * 获取仓库的管理员
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("com/getLeaderBySt")
	@Ok("json:{locked:'password',ignoreNull:true}")
	public Result getLeaderBySt(long stockplaceId,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	List<User> list = new ArrayList<User>();
                	List<User> all = dao.query(User.class,Cnd.where("companyNo","=", user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted));
                	for (User user2 : all) {
						if (dao.count(UserStockPlace.class, Cnd.where("userId","=", user2.getId()).and("stockplaceId","=",stockplaceId))!=0) {
							list.add(user2);
						}
					}
                	re.setList(list);
                	re.setTotal(list.size());
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
	 * 获取不在此仓库仓库的管理员
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("com/getLeaderNotInSt")
	@Ok("json:{locked:'password',ignoreNull:true}")
	public Result getLeaderNotInSt(long stockplaceId,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	List<User> list = new ArrayList<User>();
                	List<User> all = dao.query(User.class,Cnd.where("companyNo","=", user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted));
                	for (User user2 : all) {
						if (dao.count(UserStockPlace.class, Cnd.where("stockplaceId","=",stockplaceId).and("userId","=",user2.getId()))==0) {
							list.add(user2);
						}
					}
                	re.setList(list);
                	re.setTotal(list.size());
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
	
	@At("com/addLeaders") //添加 删除仓库管理员
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result addLeaders(@Param("stockplace")String stockplace,@Param("stockplaceId")long stockplaceId,@Param("userIds")String userIds,@Param("opt") int opt,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"com_setAuth", dao)){
            		if(userIds==null || userIds.equals("")){
                        re.setInfo(Params.info_errr_null);
                        re.setStatus(Params.status_failed);
            		}else if(stockplaceId==0 || service.isEmpty(stockplace)){
            			re.setInfo("参数为空");
            			re.setStatus(Params.status_failed);
            		}
            		else{
            			String [] id=userIds.split(",");
            			for(int i=0;i<id.length;i++){
            				if(!id[i].equals("")){
            					User u=dao.fetch(User.class,Cnd.where("id","=",id[i]));
            					if(u!=null){
            						if(opt==-1){
            							dao.clear(UserStockPlace.class,Cnd.where("stockplaceId", "=", stockplaceId).and("userId","=",u.getId()));
            						}else if(opt==1) {
            							UserStockPlace usp = new UserStockPlace();
            							usp.setAuthor(user.getUsername());
            							usp.setAuthorId(user.getId());
            							usp.setCompanyNo(user.getCompanyNo());
            							usp.setDateCreater(new Date());
            							usp.setStockplace(stockplace);
            							usp.setStockplaceId(stockplaceId);
            							usp.setUserId(u.getId());
            							usp.setUsername(u.getUsername());
            							dao.fastInsert(usp);
									}
            					}
            				}
            			}
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
            		}
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
	* @Title: addStock   
	* @Description: 添加仓库
	* @param sPlace
	* @param acu
	* @param session
	 */
	@At("com/addStock")
	@Ok("json")
	public Result addStock(@Param("..") StockPlace sPlace,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
		if(sPlace==null || service.isEmpty(sPlace.getStockplace())){
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
            	if(AuthUtils.checkUserAuth(user.getId(),"com_addStock", dao)){
            		sPlace.setAuthor(user.getUsername());
            		sPlace.setAuthorId(user.getId());
            		sPlace.setCompanyNo(user.getCompanyNo());
            		sPlace.setDateCreater(new Date());
            		dao.fastInsert(sPlace);
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
	* @Title: delStock   
	* @Description: 删除仓库
	* @param sPlace
	* @param acu
	* @param session
	 */
	@At("com/delStock")
	@Ok("json")
	public Result delStock(@Param("..") StockPlace sPlace,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
		if(sPlace==null||sPlace.getId()==0){
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
            	if(AuthUtils.checkUserAuth(user.getId(),"com_delStock", dao)){
            		dao.clear(StockPlace.class, Cnd.where("id","=",sPlace.getId()));
            		dao.clear(UserStockPlace.class,Cnd.where("stockplaceId", "=", sPlace.getId()));
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
	* @Title: getMyInfo   
	* @Description: 个人账号信息
	* @param sPlace
	* @param acu
	* @param session
	 */
	@At("my/getMyInfo")
	@Ok("json:{locked:'password',ignoreNull:true}")
	public Result getMyInfo(@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"p_myInfo", dao)){
            		User p= dao.fetch(User.class, Cnd.where("id","=", user.getId()));
					re.setObj(p);
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
	* @Title: updateMyInfo   
	* @Description: 更新个人账号信息
	* @param sPlace
	* @param acu
	* @param session
	 */
	@At("my/updateMyInfo")
	@Ok("json")
	public Result updateMyInfo(@Param("..")User p,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"p_myInfo", dao)){
					p.setDateUpdater(new Date());
					dao.update(p,"phone|dateUpdater");
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
	 * 用户修改密码
	 * @param id 用户id
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("my/setPassWd")
	@Ok("json")
	public Result setPassWd(@Param("::acu.")AppHttpUtils acu,HttpSession session,@Param("prePass")String prePass,@Param("password")String password){
		Result re = new Result();
		 if(MyUtils.checkRequestOk(acu, session)){
	            User user=service.getUserInfo(acu, session);
	            if(user==null){
	                re.setInfo(Params.loginOutInfoStr);
	                re.setStatus(Params.status_failed);
	            }
	            else{
	                try {
	                    if(user.getId()<=0){
	                    	 re.setInfo("服务器提示：非法Id");
	     	                re.setStatus(Params.status_failed);
	     	                return re;
	                    }
	                   final User _user= dao.fetch(User.class,user.getId());
	                   if (_user == null){
	                		 re.setInfo("找不到该用户信息");
		     	             re.setStatus(Params.status_failed);
		     	             return re;
	                   }
	    
	                   if (_user.getPassword().equals(MyUtils.changeStr(prePass))) {
						user.setPassword(MyUtils.changeStr(password));
						dao.update(user,"password");
	                   	} else {
	                		 re.setInfo("旧密码错误");
		     	             re.setStatus(Params.status_failed);
		     	             return re;
	                   	}
	                    re.setInfo(Params.str_optSuccess);
	                    re.setStatus(Params.status_success);
	                    ServiceData.addDataWaitingInsert(new BusinessLog("修改用户密码[编号="+_user.getUserID()+",名称="+_user.getUsername()+"]",user,"修改用户密码",Params.businessLevel_importment));
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
	
}
