package com.yjg.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import com.yjg.entity.Picking;
import com.yjg.entity.Stock;
import com.yjg.entity.StockFlow;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.entity.UserStockPlace;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;
import com.yjg.utils.StockFlowUtils;
import com.yjg.utils.UniqueNoUtils;

/**
 * 领退料模块
 * 访问路径 pick/***
 * @author lch
 *
 */
@IocBean
@InjectName("PickingModule")
public class PickingModule {
	
	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	/**
	 * 获取领料单唯一编号
	 * @return string
	 */
	@At("pick/getPickNo")
	@Ok("json")
	public String getPickNo(){
		return UniqueNoUtils.createPickNo();
	}
	
	@At("pick/addPicking")
	@Ok("json")	
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result addPicking(@Param("picking")Picking picking,@Param("itemList")StockFlow[] itemList,@Param("type")long type,@Param("::acu.")AppHttpUtils acu,HttpSession session,HttpServletRequest req){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                		Date date =new Date();
                    	if(picking==null || itemList==null || itemList.length==0){
                    		re.setInfo("参数不完整");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                    	Picking picking2 = dao.fetch(Picking.class,Cnd.where("appNo","=",picking.getAppNo()));
                    	if(picking2!=null&&(picking2.getStatus()>0||picking2.getAppUserId()!=user.getId())){
                    		re.setInfo("当前状态不能修改！");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                		if (type==1) {
                        	String stockplace= StockModule.getStockNameById(picking.getStockplaceId(),dao);
                        	if(picking2==null){//新增采购单
                            	if(!AuthUtils.checkUserAuth(user.getId(),"aly_addPick", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                            	}
                        		for (int i = 0; i < itemList.length; i++) {
									if (itemList[i].getNumber()<=0) {
		                                re.setInfo("出库数量必须大于零！");
		                                re.setStatus(Params.status_failed);
		                                return re;
									}
        							itemList[i].setAppDate(date);
        							itemList[i].setAppUser(user.getUsername());
        							itemList[i].setAppUserId(user.getId());
        							itemList[i].setAuthor(user.getUsername());
        							itemList[i].setAuthorId(user.getId());
        							itemList[i].setCompanyNo(user.getCompanyNo());
        							itemList[i].setDateCreater(date);
        							itemList[i].setStatus(0);
        							itemList[i].setPreAppNo(picking.getAppNo());
        							itemList[i].setStockplaceId(picking.getStockplaceId());
        							itemList[i].setStockplace(stockplace);
        							itemList[i].setSfType(StockFlowUtils.stockOut_use);
        							itemList[i].setFlowType(-1);
        							itemList[i].setNowNum(itemList[i].getNumber());
        							if (itemList[i].getSum()==0) {
        								itemList[i].setSum(itemList[i].getPrice()*itemList[i].getNumber());
									}else {
										itemList[i].setPrice(itemList[i].getSum()/itemList[i].getNumber());
									}
        							dao.fastInsert(itemList[i]);
        						}
                        		picking.setAppDate(date);
                        		picking.setAppUser(user.getUsername());
                        		picking.setAppUserId(user.getId());
                        		picking.setAuthor(user.getUsername());
                        		picking.setAuthorId(user.getId());
                        		picking.setCompanyNo(user.getCompanyNo());
                        		picking.setDateCreater(date);
                        		picking.setStatus(0);
                        		picking.setType(0);
                        		picking.setStockplace(stockplace);
                        		picking.setSfType(StockFlowUtils.stockOut_use);
                        		dao.fastInsert(picking);
                        	}else {//添加明细
                            	if(!AuthUtils.checkUserAuth(user.getId(),"aly_updatePick", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                            	}
	                    		for (int i = 0; i < itemList.length; i++) {
	    							if (itemList[i].getNumber()<=0) {
		                                re.setInfo("出库数量必须大于零！");
		                                re.setStatus(Params.status_failed);
		                                return re;
									}
	                    			StockFlow sFlow = dao.fetch(StockFlow.class,Cnd.where("preAppNo", "=", picking.getAppNo()).and("productId","=",itemList[i].getProductId()));
	                    			if (sFlow!=null) {//追加
										sFlow.setDateUpdater(date);
										sFlow.setName(itemList[i].getName());
										sFlow.setNumber(sFlow.getNumber()+itemList[i].getNumber());
										sFlow.setUnit(itemList[i].getUnit());
										sFlow.setSum(sFlow.getNumber()*sFlow.getPrice());
										dao.updateIgnoreNull(sFlow);
									}else{
		    							itemList[i].setAppDate(date);
		    							itemList[i].setAppUser(user.getUsername());
		    							itemList[i].setAppUserId(user.getId());
		    							itemList[i].setAuthor(user.getUsername());
		    							itemList[i].setAuthorId(user.getId());
		    							itemList[i].setCompanyNo(user.getCompanyNo());
		    							itemList[i].setDateCreater(date);
		    							itemList[i].setStatus(0);
		    							itemList[i].setPreAppNo(picking.getAppNo());
		    							itemList[i].setStockplaceId(picking.getStockplaceId());
		    							itemList[i].setStockplace(stockplace);
		    							itemList[i].setSfType(StockFlowUtils.stockOut_use);
		    							itemList[i].setFlowType(-1);
		    							itemList[i].setNowNum(itemList[i].getNumber());
		    							itemList[i].setSum(itemList[i].getPrice()*itemList[i].getNumber());
		    							dao.fastInsert(itemList[i]);
									}
	    						}
                            	picking2.setStatus(0);
                            	picking2.setDateUpdater(new Date());
    							dao.update(picking2);
							}
						}else if(type==-1){//删除明细
                        	if(!AuthUtils.checkUserAuth(user.getId(),"aly_updatePick", dao)){
                                re.setInfo(Params.str_noRequestAuth);
                                re.setStatus(Params.status_failed);
                                return re;
                        	}
                        	picking2.setStatus(0);
                        	picking2.setDateUpdater(new Date());
							dao.update(picking2);
							for (int i = 0; i < itemList.length; i++) {
    							dao.clear(StockFlow.class,Cnd.where("id","=",itemList[i].getId()));
    						}
						}else if (type==0) {//修改明细
                        	if(!AuthUtils.checkUserAuth(user.getId(),"aly_updatePur", dao)){
                                re.setInfo(Params.str_noRequestAuth);
                                re.setStatus(Params.status_failed);
                                return re;
                        	}
							for (int i = 0; i < itemList.length; i++) {
    							if (itemList[i].getNumber()<=0) {
	                                re.setInfo("出库数量必须大于零！");
	                                re.setStatus(Params.status_failed);
	                                return re;
								}
								StockFlow sFlow2 = dao.fetch(StockFlow.class,Cnd.where("id", "=", itemList[i].getId()));
								sFlow2.setDateUpdater(date);
								sFlow2.setNumber(itemList[i].getNumber());
								sFlow2.setNowNum(itemList[i].getNumber());
								sFlow2.setSum(itemList[i].getPrice()*itemList[i].getNumber());
								sFlow2.setPrice(itemList[i].getPrice());
								sFlow2.setContent(itemList[i].getContent());
								sFlow2.setRemark(itemList[i].getRemark());
								sFlow2.setStatus(0);
								sFlow2.setDateUpdater(date);
    							dao.updateIgnoreNull(sFlow2);
    						}
							picking2.setStatus(0);
							picking2.setDateUpdater(new Date());
							dao.update(picking2);
						}
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
	 * 获取某个领料单明细
	 * @param purNo 采购单号
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/getPickInfo")
	@Ok("json")
	public Result getPickInfo(@Param("pickNo") String pickNo,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	List<StockFlow> list = dao.query(StockFlow.class, Cnd.where("preAppNO","=",pickNo));
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
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 获取我的领料单列表(申请人用)
	 * @param type 0=我的待审核领料单，1=我的审核通过领料单，3=我的部分出库领料单,4=我的全部出库领料单
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/getMyPickList")
	@Ok("json")
	public Result getMyPickList(int type,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	List<UserStockPlace> stockList = dao.query(UserStockPlace.class,Cnd.where("userId","=",user.getId()));
            	SqlExpressionGroup sqlGroup = null;
            	for (UserStockPlace usp : stockList) {//我负责的仓库
            		SqlExpressionGroup sqlSingle = Cnd.exps("stockplaceId","=",usp.getStockplaceId());
            		if(sqlGroup==null)sqlGroup=sqlSingle;
            		else {
						sqlGroup = sqlGroup.or(sqlSingle);
					}
				}
            	if (sqlGroup==null) {
                    re.setInfo("你还没有管理的仓库！请联系管理员添加");
                    re.setStatus(Params.status_failed);
                    return re;
				}
            	if(AuthUtils.checkUserAuth(user.getId(),"aly_getMyPickList", dao)){
                	List<Picking> list=null;
                    try {
                    	if(type==0){
                    		list=dao.query(Picking.class,Cnd.where("status","=",0).and("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//待审核
                    		re.setTotal(dao.count(Picking.class,Cnd.where("status","=",0).and("appUserId","=",user.getId()).and(sqlGroup)));
                    	}else if (type==1) {
                    		list=dao.query(Picking.class,Cnd.where("status","=",1).and("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//审核通过
                    		re.setTotal(dao.count(Picking.class,Cnd.where("status","=",1).and("appUserId","=",user.getId()).and(sqlGroup)));
    					}else if (type==3) {
    						list=dao.query(Picking.class,Cnd.where("status","=",3).and("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//部分入库
    						re.setTotal(dao.count(Picking.class,Cnd.where("status","=",3).and("appUserId","=",user.getId()).and(sqlGroup)));
    					}else if (type==9) {
    						list=dao.query(Picking.class,Cnd.where("status","=",9).and("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//全部入库
    						re.setTotal(dao.count(Picking.class,Cnd.where("status","=",9).and("appUserId","=",user.getId()).and(sqlGroup)));
    					}
    					else if (type==-1) {
    						list=dao.query(Picking.class,Cnd.where("status","=",-1).and("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//审核不通过
    						re.setTotal(dao.count(Picking.class,Cnd.where("status","=",-1).and("appUserId","=",user.getId()).and(sqlGroup)));
    					}
                    	else{
    						list=dao.query(Picking.class,Cnd.where("appUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//全部
    						re.setTotal(dao.count(Picking.class,Cnd.where("appUserId","=",user.getId()).and(sqlGroup)));
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
	 * 获取我的退料物品列表(申请人用)
	 * @param type 0=我的待审核领料单，1=我的审核通过领料单，3=我的部分出库领料单,4=我的全部出库领料单
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/getMyPickRnList")
	@Ok("json")
	public Result getMyPickRnList(int type,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"aly_getMyPickList", dao)){
                	List<StockFlow> list=null;
                    try {
                    	if(type==6){//待审核(待入库)
                    		list = dao.query(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",6),dao.createPager(pageNo, pageSize));
                    		re.setTotal(dao.count(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",6)));
                    	}else if (type==9) {
                    		list = dao.query(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",9),dao.createPager(pageNo, pageSize));
                     		re.setTotal(dao.count(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",9)));
						}else if (type==-9) {
							list = dao.query(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",-9),dao.createPager(pageNo, pageSize));
							re.setTotal(dao.count(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return).and("status","=",-9)));
						}else {
							list = dao.query(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return),dao.createPager(pageNo, pageSize));
							re.setTotal(dao.count(StockFlow.class,Cnd.where("appUserId", "=", user.getId()).and("sfType","=", StockFlowUtils.stockIn_return)));
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
	 * 获取领料单列表(审核人用)
	 * @param type 0=所有待审核的，1=所有审核通过的，2=我审核通过的
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/getPickList")
	@Ok("json")
	public Result getPickList(int type,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	List<UserStockPlace> stockList = dao.query(UserStockPlace.class,Cnd.where("userId","=",user.getId()));
            	SqlExpressionGroup sqlGroup = null;
            	for (UserStockPlace usp : stockList) {//我负责的仓库
            		SqlExpressionGroup sqlSingle = Cnd.exps("stockplaceId","=",usp.getStockplaceId());
            		if(sqlGroup==null)sqlGroup=sqlSingle;
            		else {
						sqlGroup = sqlGroup.or(sqlSingle);
					}
				}
            	if (sqlGroup==null) {
                    re.setInfo("你还没有管理的仓库！请联系管理员添加");
                    re.setStatus(Params.status_failed);
                    return re;
				}
            	List<Picking> list=null;
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"st_getInApplylist", dao)){
                    	if(type==0){
                    		list=dao.query(Picking.class,Cnd.where("status","=",0).and(sqlGroup),dao.createPager(pageNo, pageSize));//待审核
                    		re.setTotal(dao.count(Picking.class,Cnd.where("status","=",0).and(sqlGroup)));
                    	}else if (type==1) {
                    		list=dao.query(Picking.class,Cnd.where("status",">",0).and(sqlGroup),dao.createPager(pageNo, pageSize));//已通过
                    		re.setTotal(dao.count(Picking.class,Cnd.where("status",">",0).and(sqlGroup)));
    					}else if (type==2) {
    						list=dao.query(Picking.class,Cnd.where("status",">",0).and("verifyUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//我通过的
    						re.setTotal(dao.count(Picking.class,Cnd.where("status",">",0).and("verifyUserId","=",user.getId()).and(sqlGroup)));
    					}else if (type==-1){
    						list=dao.query(Picking.class,Cnd.where("status","=",-1).and(sqlGroup),dao.createPager(pageNo, pageSize));//未通过
    						re.setTotal(dao.count(Picking.class,Cnd.where("status","=",-1).and(sqlGroup)));
    					}else {
    						list=dao.query(Picking.class,Cnd.where(sqlGroup),dao.createPager(pageNo, pageSize));//全部
    						re.setTotal(dao.count(Picking.class,Cnd.where(sqlGroup)));
    					}
                    	re.setList(list);
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
	 * 审核出库申请
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param purNos 采购号list
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/verifyPick")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result verifyPick(@Param("pickNos") String [] pickNos,@Param("opt") int opt,@Param("hint")String hint,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"pur_apply", dao)){
                		for (int i = 0; i < pickNos.length; i++) {
                			Picking picking = dao.fetch(Picking.class, Cnd.where("appNo","=",pickNos[i]));
    						if (opt==1) {
    							picking.setStatus(1);
    							picking.setHint("");
    						}else if (opt==-1) {
    							picking.setStatus(-1);
    							picking.setHint(hint);
    						}else {
    				            re.setInfo("操作码错误");
    				            re.setStatus(Params.status_failed);
    							return re;
    						}
    						picking.setVerifyDate(new Date());
    						picking.setVerifyUserId(user.getId());
    						picking.setVerifyUser(user.getUsername());
    						dao.update(picking,"status|verifyDate|verifyUserId|verifyUser|hint");
    						List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("preAppNo","=",pickNos[i]));
    						for (int j = 0; j < list.size(); j++) {
    							StockFlow stockFlow = list.get(j);
    							if (opt==1) {
    								stockFlow.setStatus(1);
    							}else if (opt==-1) {
    								stockFlow.setStatus(-1);
    							}
    							stockFlow.setVerifyDate(new Date());
    							stockFlow.setVerifyUser(user.getUsername());
    							stockFlow.setVerifyUserId(user.getId());
    							dao.update(stockFlow,"status|verifyDate|verifyUserId|verifyUser");
    						}
    					}
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
	 * 删除领料单
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param purNos 采购号list
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pick/delPick")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result delPick(@Param("pickNos") String [] pickNos,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"aly_delPick", dao)){
                		
                		for (int i = 0; i < pickNos.length; i++) {
                			Picking picking = dao.fetch(Picking.class, Cnd.where("appNo","=",pickNos[i]));
                        	if(picking!=null&&(picking.getStatus()>0||picking.getAppUserId()!=user.getId())){
                        		re.setInfo("当前状态不能修删除！");
                        		re.setStatus(Params.status_failed);
                        		return re;
                        	}
                			dao.clear(Picking.class, Cnd.where("appNo","=",pickNos[i]));
    						dao.clear(StockFlow.class,Cnd.where("preAppNo","=",pickNos[i]));
    					}
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
	
	@At("pick/addPickReturn")
	@Ok("json")	
	public Result addPickReturn(@Param("numbers")String numbers,@Param("flowIds")String flowIds,@Param("::acu.")AppHttpUtils acu,HttpSession session,HttpServletRequest req){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                		Date date =new Date();
        	            String [] id=flowIds.split(",");
        	            String [] number=numbers.split(",");
                    	if(id.length==0||id.length!=number.length){
                    		re.setInfo("参数不完整");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
        	            for(int i=0;i<id.length;i++){
        	            	if(!id[i].equals("")){
        	            		StockFlow stockFlow = dao.fetch(StockFlow.class,Cnd.where("id","=", id[i]));
        	            		StockFlow sFlow = new StockFlow();
        	            		sFlow.setAppDate(date);
        	            		sFlow.setAppUser(user.getUsername());
        	            		sFlow.setAppUserId(user.getId());
        	            		sFlow.setAuthor(user.getUsername());
        	            		sFlow.setAuthorId(user.getId());
        	            		sFlow.setCompanyNo(user.getCompanyNo());
        	            		sFlow.setDateCreater(date);
        	            		sFlow.setName(stockFlow.getName());
        	            		sFlow.setModel(stockFlow.getModel());
        	            		sFlow.setNowNum(stockFlow.getNumber());
        	            		sFlow.setNumber(Float.parseFloat(number[i]));
        	            		sFlow.setPreAppId(stockFlow.getPreAppNo());
        	            		sFlow.setPreItemId(stockFlow.getId());
        	            		sFlow.setStockId(stockFlow.getStockId());
        	            		sFlow.setProNo(stockFlow.getProNo());
        	            		sFlow.setProductId(stockFlow.getProductId());
        	            		sFlow.setPrice(dao.fetch(Stock.class,Cnd.where("id","=",stockFlow.getStockId())).getPriceIn());
        	            		sFlow.setStatus(6);
        	            		sFlow.setSfType(StockFlowUtils.stockIn_return);
        	            		sFlow.setFlowType(1);
        	            		sFlow.setSum(sFlow.getNumber()*sFlow.getPrice());
        	            		sFlow.setUnit(stockFlow.getUnit());
        	            		sFlow.setStockplace(stockFlow.getStockplace());
        	            		sFlow.setStockplaceId(stockFlow.getStockplaceId());
        	            		dao.fastInsert(sFlow);
        	            	}
        	            }
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
}
