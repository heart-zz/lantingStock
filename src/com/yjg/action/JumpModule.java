package com.yjg.action;
import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.view.JspView;

import com.yjg.utils.CheckUserInfoFilter;



/**
 * 页面跳转模块
 * @author yjg
 *
 */
@InjectName
@IocBean
public class JumpModule {
	
	/**
	 * 离开主页
	 * @param session
	 * @return
	 */
	@At
	public Object exit(HttpServletRequest req){
		req.getSession().invalidate();		
		return new JspView("/index");
	}
	
	/**
	 * 跳转到主页,刷新主页内容
	 * @return
	 */
	@At
	@Filters({@By(type=CheckUserInfoFilter.class)})
	public Object index(HttpServletRequest req){		
		return new JspView("/main");
	}
	

	/**
	 * 跳转到登录界面
	 * @return
	 */
	@At	
	public Object loginPage(HttpServletRequest req){			
		return new JspView("/index");
	}
	
}
