package com.yjg.action;


import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;

import com.alibaba.druid.pool.DruidDataSource;
import com.yjg.entity.Auth;
import com.yjg.entity.BusinessLog;
import com.yjg.entity.Role;
import com.yjg.entity.RoleAuth;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;
import com.yjg.utils.DaoInsertThread;
import com.yjg.utils.ExcelPIO;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.ServiceData;
import com.yjg.utils.SystemAuthCode;
import com.yjg.utils.SystemRoles;
import com.yjg.AppService;

/**
 * 系统初始化
 * @author yjg
 *
 */
public class InitServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 数据库操作
	 */
	@Inject
	private static Dao dao;
	
	@Inject
	private static  AppService service;
	
	private Logger logger=Logger.getLogger(InitServlet.class);
	
	
	/**
	 * 获取一个dao实例：利用 druid 数据库连接池
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Dao getDao() throws ClassNotFoundException{		
		logger.info("******实例化DAO******");
		if(service==null)
			service=new AppService();
		try {
			if(ServiceData.minaDDS==null){
				DruidDataSource ds=new DruidDataSource();
				ds.setDriverClassName(service.getParamsProperty("db_driver"));
				ds.setUrl(service.getParamsProperty("db_url"));
				ds.setUsername(service.getParamsProperty("db_user"));
				ds.setPassword(service.getParamsProperty("db_password"));
				ds.setInitialSize(1);
				ds.setMaxActive(5);
				ds.setMaxWait(5);
				ServiceData.minaDDS=ds;
			}			
			return new NutDao(ServiceData.minaDDS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();			
			logger.error("*******实例化DAO失败,原因"+e.toString()+"******");
			return null;
		}
		
	}
	
	@Override
	public void init(ServletConfig config){
		logger.info("********"+Params.serviceName+"正在初始化********");
		if(dao==null){
			try {
				dao=getDao();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("********系统初始化失败:"+e.toString()+"*********");				
			}
		}
		try {
			this.updateTable();								
			this.startThread();	
			this.setRootUrl(config.getServletContext());
			this.initRoles();
			this.initAuth();
			this.initcomAdminRoles();
			this.initAdmin();							
			
			logger.info("*****系统初始化完成*****");			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("*******系统初始化失败:"+e.toString()+"*******");
		}
	}

	/**
	 * 更新数据库表
	 */
	private void updateTable(){
		logger.info("***开始更新数据表***");
		String isUpdate=service.getParamsProperty("db_autoUpdate");
		if(isUpdate.equals("1")){				
			Daos.createTablesInPackage(dao, service.getParamsProperty("entityUrl"), false);//批量建表
		}
		
	}
	
	/**
	 * 系统自动创建唯一一个超级用户
	 */
	private void initAdmin(){
		User admin=dao.fetch(User.class,Cnd.where("rolename","=",SystemRoles.role_superAdmin));
		Role role_admin=dao.fetch(Role.class,Cnd.where("rolename","=",SystemRoles.role_superAdmin));
		Date date=new Date();
		if(admin==null){//创建超级用户			
			admin=new User();
			admin.setEmail("无");
			admin.setPassword(MyUtils.dbChangeStr(Params.password_default));
			admin.setUsername("admin");			
			if(role_admin!=null){
				admin.setRoleId(role_admin.getId());
				admin.setRoleName(role_admin.getRoleName());
				admin.setPhone("无");
				admin.setAuthor("系统");
				admin.setAuthorId(0);
				admin.setDateCreater(date);
				admin.setStatus(1);
				admin.setCompanyNo("MyCompany");
				admin.setUserID("MyID");
				admin.setPhone("Myphone");
				try {
					dao.insert(admin);					
					ServiceData.addDataWaitingInsert(new BusinessLog("***为系统创建唯一超级用户,登录名="+admin.getUsername()+",初始登录密码="+Params.password_default+"***", null, this.getClass().getName()+","+"initAdmin", Params.businessLevel_importment));
					logger.debug("***为系统创建唯一超级用户,登录名="+admin.getUsername()+",初始登录密码="+Params.password_default+"***");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		List<RoleAuth> ralist=dao.query(RoleAuth.class, Cnd.where("roleId","=",role_admin.getId()));
		for(RoleAuth ra:ralist){
			if(dao.count(UserAuth.class,Cnd.where("userId","=",admin.getId()).and("authCode","=",ra.getAuthCode()))==0){
				UserAuth ua=new UserAuth();
				ua.setAuthCode(ra.getAuthCode());						
				ua.setAuthor("系统");
				ua.setAuthorId(0);
				ua.setDateCreater(date);
				ua.setUserId(admin.getId());
				dao.fastInsert(ua);
			}
		}
	}
	
	/**
	 * 系统自动初始化权限列表-从本地文件读取(会删除原来的权限配置表，重新导入一次),并自动生成超级用户的权限
	 */
	private void initAuth(){
		try {
			String isInitAuth=service.getParamsProperty("isInitAuth");//从配置文件读取是否需要重新载入权限列表
			if("1".equals(isInitAuth)){
				logger.info("***重新载入权限表***");
				dao.clear(Auth.class);//清空原权限表
				List<List<String[]>> book=ExcelPIO.read(Params.rootUrl+Params.authlist_xls);
				Role role_admin=dao.fetch(Role.class,Cnd.where("rolename","=",SystemRoles.role_superAdmin));
				Date date=new Date();
				if(book!=null && book.size()>0){
					for(int si=0;si<book.size();si++){
						List<String[]> sheet=book.get(si);				
						int count=0;					
						for(int i=1;i<sheet.size();i++){
							String[] record=sheet.get(i);//固定格式:[0]=权限编号,[1]=父权限编号,[2]=权限名称
							if(!MyUtils.isEmpty(record[0])){
								count=dao.count(Auth.class,Cnd.where("authCode","=",record[0]));
								if(count==0){
									Auth auth=new Auth();
									auth.setAuth(record[2]);
									auth.setAuthCode(record[0]);
									auth.setFatherCode(record[1]);														
									if("0".equals(auth.getFatherCode())){
										auth.setFather("系统");
									}
									else{
										Auth _father=dao.fetch(Auth.class,Cnd.where("authCode","=",auth.getFatherCode()));
										if(_father!=null){
											auth.setFather(_father.getAuth());
										}
										else{
											auth.setFather("未定义");
										}
									}
									auth.setAuthor("系统");
									auth.setAuthorId(0);
									auth.setDateCreater(date);
									try {
										dao.insert(auth);								
										count=dao.count(RoleAuth.class,Cnd.where("roleId","=",role_admin.getId()).and("authCode","=",auth.getAuthCode()));
										if(count==0){
											RoleAuth ra=new RoleAuth();
											ra.setAuthCode(auth.getAuthCode());
											ra.setRoleId(role_admin.getId());
											ra.setDateCreater(date);
											ra.setAuthor("系统");
											ra.setAuthorId(0);
											dao.fastInsert(ra);
										}
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();								
									}
								}
							}																
						}
					}					
				}
			}
		}catch (Exception e) {
			// TODO: handle exception			
			logger.error("读取系统权限配置文件失败:"+e.toString());
		}
			
	}
	
	/**
	 * 初始化角色信息
	 */
	private void initRoles(){
		logger.info("***初始化系统内置角色***");
		String[] roles=SystemRoles.systemRols;
		int count=0;
		for(String role:roles){
			count=dao.count(Role.class,Cnd.where("rolename","=",role));
			if(count==0){
				Role _role=new Role();
				_role.setCanDel(false);
				_role.setRoleName(role);
				_role.setRemark("系统内置,不能删除");
				_role.setAuthor("系统");
				_role.setAuthorId(0);
				_role.setDateCreater(new Date());
				try {
					dao.insert(_role);					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.error("数据库操作失败:"+e.toString());
				}
			}
		}
	}
	
	/**
	 * 初始化内置公司管理员角色权限
	 * 个人信息管理及其子权限
	 * 拥有公司人事管理权限及其子权限
	 */
	private void initcomAdminRoles(){
		logger.info("***初始化内置公司管理员角色权限***");
		Role role_comAdmin=dao.fetch(Role.class,Cnd.where("rolename","=",SystemRoles.role_comAdmin));
		int count=dao.count(RoleAuth.class,Cnd.where("roleId","=",role_comAdmin.getId()));
		Date date =new Date();
		if(count==0){
			RoleAuth ra=new RoleAuth();
			List<Auth> comAuths = dao.query(Auth.class, Cnd.where("fatherCode","!=",SystemAuthCode.auth_sys).and("authCode","!=",SystemAuthCode.auth_sys));
			for (Auth auth : comAuths) {
				ra.setAuthCode(auth.getAuthCode());
				ra.setAuthor("系统");
				ra.setAuthorId(0);
				ra.setDateCreater(date);
				ra.setRoleId(role_comAdmin.getId());
				try {
					dao.insert(ra);					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.error("数据库操作失败:"+e.toString());
				}
			}
		}
	};
	
	/**
	 * 启动内置线程
	 */
	private void startThread(){
		logger.info("****启动内置线程****");
		//系统内置线程
		DaoInsertThread dit=new DaoInsertThread();
		dit.start();
	}	
	
	/**
	 * 设置系统根目录
	 * @param sc
	 */
	private void setRootUrl(ServletContext sc){
		Params.rootUrl=sc.getRealPath("/")+"/";
		logger.info("系统根目录="+Params.rootUrl);
	}	

	
	
}
