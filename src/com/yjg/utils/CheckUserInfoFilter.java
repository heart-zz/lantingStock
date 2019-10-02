package com.yjg.utils;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.JspView;
/**
 * 检查用户是否登录，若登录，跳转到目标页面，否则，跳转到登录页面
 * @author yjg 
 *
 */
public class CheckUserInfoFilter implements ActionFilter{

	@Override
	public View match(ActionContext context){
		if(context.getRequest().getSession().getAttribute("user")==null){
			context.getRequest().setAttribute("obj", "您未登录,请先登录");						
			return new JspView("/index");
		}
		else 
			return null;
	}
}
