package com.yjg.action;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Dao;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.yjg.AppService;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;

/**
 * 处理基本业务模块
 * @author yjg
 *
 */
@IocBean
@InjectName("indexModule")
public class IndexModule {

	@Inject
	private AppService service;
	
	@Inject
	private Dao dao;	
	
	
	/**
	 * 获取首页数据内容
	 * @return {@link Result} list[0]=公告列表,list[1]=待处理事项列表
	 */
	@At("index/getIndexInfo")
	@Ok("json")
	public Result getIndexInfo(@Param("::acu.")AppHttpUtils acu, HttpSession session){
		 Result re=new Result();
		 if(MyUtils.checkRequestOk(acu, session)){
	            User user=service.getUserInfo(acu, session);
	            if(user==null){
	                re.setInfo(Params.loginOutInfoStr);
	                re.setStatus(Params.status_failed);
	            }
	            else{
	                try {
	                	List<Object> list=new LinkedList<Object>();	                	
	                	list.add(getUserTodayWorkWeb(user).getList());
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
	            re.setInfo(Params.str_noRequestAuth);
	            re.setStatus(Params.status_failed);
	        }
	       
		return re;
	}
	
	/**
	 * 获取指定用户的待处理事项(网页端)
	 * @param user 用户 {@link User}
	 * @return
	 */
	private Result getUserTodayWorkWeb(User user){
		Result re=new Result();
		
		return re;
	}	
}

