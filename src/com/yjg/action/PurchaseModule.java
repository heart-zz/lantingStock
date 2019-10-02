package com.yjg.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
import com.yjg.entity.Purchase;
import com.yjg.entity.StockFlow;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.entity.UserAuth;
import com.yjg.entity.UserStockPlace;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.ComboUtils;
import com.yjg.utils.ExcelPIO;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;
import com.yjg.utils.StockFlowUtils;
import com.yjg.utils.UniqueNoUtils;

/**
 * 采购模块
 * 访问路径 pur/***
 * @author yjg
 *
 */
@IocBean
@InjectName("purchaseModule")
public class PurchaseModule {

	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	
	public static String getPurBuyerById(long uId,Dao dao) {
		User u = dao.fetch(User.class,Cnd.where("id", "=", uId));
		  if(u==null)return "该用户不存在";
		  else return u.getUsername();
	}
	
	/**
	 * 获取采购列表(审核人用)
	 * @param type 0=所有采购，1=所有待审核的，2=所有审核通过的，3=我审核通过的,4=我采购过的
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/getPurList")
	@Ok("json")
	public Result getPurList(int type,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
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
            	List<Purchase> list=null;
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"pur_getApplyList", dao)){
                    	if(type==0){
                    		list=dao.query(Purchase.class,Cnd.where("status","=",0).and(sqlGroup),dao.createPager(pageNo, pageSize));//待审核
                    		re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",0).and(sqlGroup)));
                    	}else if (type==1) {
                    		list=dao.query(Purchase.class,Cnd.where("status",">=",1).and(sqlGroup),dao.createPager(pageNo, pageSize));//已通过
                    		re.setTotal(dao.count(Purchase.class,Cnd.where("status",">=",0).and(sqlGroup)));
    					}else if (type==2) {
    						list=dao.query(Purchase.class,Cnd.where("status",">=",1).and("verifyUserId","=",user.getId()).and(sqlGroup),dao.createPager(pageNo, pageSize));//我通过的
    						re.setTotal(dao.count(Purchase.class,Cnd.where("status",">=",1).and("verifyUserId","=",user.getId()).and(sqlGroup)));
    					}else if (type==-1){
    						list=dao.query(Purchase.class,Cnd.where("status","=",-1).and(sqlGroup),dao.createPager(pageNo, pageSize));//未通过
    						re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",-1).and(sqlGroup)));
    					}else {
    						list=dao.query(Purchase.class,Cnd.where(sqlGroup),dao.createPager(pageNo, pageSize));//全部
    						re.setTotal(dao.count(Purchase.class,Cnd.where(sqlGroup)));
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
	 * 获取我的采购列表(申请人用)
	 * @param type 0=我的所有采购，1=我的待审核采购，2=我的审核通过采购，3=我的部分入库采购,4=我的全部入库采购
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/getMyPurList")
	@Ok("json")
	public Result getMyPurList(int type,int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AuthUtils.checkUserAuth(user.getId(),"aly_getMyPurList", dao)){
                	List<Purchase> list=null;
                    try {
                    	if(type==0){
                    		list=dao.query(Purchase.class,Cnd.where("status","=",0).and("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//待审核
                    		re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",0).and("appUserId","=",user.getId())));
                    	}else if (type==1) {
                    		list=dao.query(Purchase.class,Cnd.where("status","=",1).and("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//审核通过
                    		re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",1).and("appUserId","=",user.getId())));
    					}else if (type==3) {
    						list=dao.query(Purchase.class,Cnd.where("status","=",3).and("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//部分入库
    						re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",3).and("appUserId","=",user.getId())));
    					}else if (type==9) {
    						list=dao.query(Purchase.class,Cnd.where("status","=",9).and("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//全部入库
    						re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",9).and("appUserId","=",user.getId())));
    					}
    					else if (type==-1) {
    						list=dao.query(Purchase.class,Cnd.where("status","=",-1).and("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//审核不通过
    						re.setTotal(dao.count(Purchase.class,Cnd.where("status","=",-1).and("appUserId","=",user.getId())));
    					}
                    	else{
    						list=dao.query(Purchase.class,Cnd.where("appUserId","=",user.getId()),dao.createPager(pageNo, pageSize));//全部
    						re.setTotal(dao.count(Purchase.class,Cnd.where("appUserId","=",user.getId())));
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
	 * 导出指定采购数据
	 * @param ids 采购记录ids
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("pur/exportPurList")
	@Ok("json")
	public Result exportPurList(String ids,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	//测试
                	List<List<String[]>> book=new LinkedList<List<String[]>>();
                	List<String[]> sheet=new LinkedList<String[]>();
                	String[] title={"采购单号","申请人","申请日期","仓库","状态","审核人","审核日期"};
                	sheet.add(title);
                	for(int i=1;i<=10;i++){
                		String[] _record={"P201805620015","用户1","2018-06-20","仓库1","已入库","管理员","2018-06-25"};
                		sheet.add(_record);
                	}
                	String _filepath="files/tmp/";
                	String _filename=System.currentTimeMillis()+".xls";
                	ExcelPIO.write(book, Params.rootUrl+_filepath, _filename);
                	re.setObj(_filepath+_filename);//返回前端相对路径
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
	 * 
	 * @param purchase
	 * @param itemList
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("pur/addPurchase")
	@Ok("json")	
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result addPurchase(@Param("purchase")Purchase purchase,@Param("itemList")StockFlow[] itemList,@Param("type")long type,@Param("::acu.")AppHttpUtils acu,HttpSession session,HttpServletRequest req){
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
                    	if(purchase==null || itemList==null || itemList.length==0){
                    		re.setInfo("参数不完整");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                    	Purchase purchase2 = dao.fetch(Purchase.class,Cnd.where("appNo","=",purchase.getAppNo()));
                    	if(purchase2!=null&&(purchase2.getStatus()>0||purchase2.getAppUserId()!=user.getId())){
                    		re.setInfo("当前状态不能修改！");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                		if (type==1) {
                        	String stockplace= StockModule.getStockNameById(purchase.getStockplaceId(),dao);
                        	if(purchase2==null){//新增采购单
                            	if(!AuthUtils.checkUserAuth(user.getId(),"aly_addPur", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                            	}
                        		for (int i = 0; i < itemList.length; i++) {
                        			String optUser= getPurBuyerById(itemList[i].getOptUserId(),dao);
									if (itemList[i].getNumber()<=0||optUser.equals("该用户不存在")) {
		                                re.setInfo("采购数量有误或采购人不存在！");
		                                re.setStatus(Params.status_failed);
		                                return re;
									}
                        			itemList[i].setOptUser(optUser);
	    							itemList[i].setAppDate(date);
	    							itemList[i].setAppUser(user.getUsername());
	    							itemList[i].setAppUserId(user.getId());
	    							itemList[i].setAuthor(user.getUsername());
	    							itemList[i].setAuthorId(user.getId());
	    							itemList[i].setCompanyNo(user.getCompanyNo());
	    							itemList[i].setFlowType(1);
	    							itemList[i].setDateCreater(date);
	    							itemList[i].setStatus(0);
	    							itemList[i].setStockplaceId(purchase.getStockplaceId());
	    							itemList[i].setStockplace(stockplace);
	    							itemList[i].setPreAppNo(purchase.getAppNo());
	    							itemList[i].setSfType(StockFlowUtils.stockIn_purchase);
	    							itemList[i].setFlowType(1);
	    							dao.fastInsert(itemList[i]);
        						}
                        		purchase.setAppDate(date);
                        		purchase.setAppUserId(user.getId());
                        		purchase.setAppUser(user.getUsername());
                        		purchase.setAuthor(user.getUsername());
                        		purchase.setAuthorId(user.getId());
                        		purchase.setCompanyNo(user.getCompanyNo());
                        		purchase.setDateCreater(date);
                        		purchase.setStatus(0);
                        		purchase.setStockplace(stockplace);
                        		purchase.setSfType(StockFlowUtils.stockIn_purchase);
                        		dao.fastInsert(purchase);
                        	}else {//添加明细
                            	if(!AuthUtils.checkUserAuth(user.getId(),"aly_updatePur", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                            	}
	                    		for (int i = 0; i < itemList.length; i++) {
									String optUser= getPurBuyerById(itemList[i].getOptUserId(),dao);
									if (itemList[i].getNumber()<=0||optUser.equals("该用户不存在")) {
		                                re.setInfo("采购数量有误或采购人不存在！");
		                                re.setStatus(Params.status_failed);
		                                return re;
									}
	                    			StockFlow sFlow = dao.fetch(StockFlow.class,Cnd.where("preAppNo", "=", purchase.getAppNo()).and("productId","=",itemList[i].getProductId()));
	                    			if (sFlow!=null) {//追加
										sFlow.setDateUpdater(date);
										sFlow.setName(itemList[i].getName());
										sFlow.setNumber(sFlow.getNumber()+itemList[i].getNumber());
										sFlow.setUnit(itemList[i].getUnit());
										sFlow.setSum(sFlow.getNumber()*sFlow.getPrice());
										dao.updateIgnoreNull(sFlow);
									}else{
	                        			itemList[i].setOptUser(optUser);
		    							itemList[i].setAppDate(date);
		    							itemList[i].setAppUser(user.getUsername());
		    							itemList[i].setAppUserId(user.getId());
		    							itemList[i].setAuthor(user.getUsername());
		    							itemList[i].setAuthorId(user.getId());
		    							itemList[i].setCompanyNo(user.getCompanyNo());
		    							itemList[i].setFlowType(1);
		    							itemList[i].setDateCreater(date);
		    							itemList[i].setStatus(0);
		    							itemList[i].setStockplaceId(purchase.getStockplaceId());
		    							itemList[i].setStockplace(stockplace);
		    							itemList[i].setPreAppNo(purchase.getAppNo());
		    							itemList[i].setSfType(StockFlowUtils.stockIn_purchase);
		    							itemList[i].setFlowType(1);
		    							dao.fastInsert(itemList[i]);
									}
	    						}
                        		purchase2.setStatus(0);
                        		purchase2.setDateUpdater(new Date());
    							dao.update(purchase2);
							}
						}else if(type==-1){//删除明细
                        	if(!AuthUtils.checkUserAuth(user.getId(),"aly_updatePur", dao)){
                                re.setInfo(Params.str_noRequestAuth);
                                re.setStatus(Params.status_failed);
                                return re;
                        	}
							purchase2.setStatus(0);
							purchase2.setDateUpdater(new Date());
							dao.update(purchase2);
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
								StockFlow sFlow2 = dao.fetch(StockFlow.class,Cnd.where("id", "=", itemList[i].getId()));
								String optUser= getPurBuyerById(itemList[i].getOptUserId(),dao);
								if (itemList[i].getNumber()<=0||optUser.equals("该用户不存在")) {
	                                re.setInfo("采购数量有误或采购人不存在！");
	                                re.setStatus(Params.status_failed);
	                                return re;
								}
								sFlow2.setDateUpdater(date);
								sFlow2.setNumber(itemList[i].getNumber());
								sFlow2.setSum(itemList[i].getSum());
								sFlow2.setPrice(itemList[i].getPrice());
								sFlow2.setContent(itemList[i].getContent());
								sFlow2.setRemark(itemList[i].getRemark());
								sFlow2.setOptUser(optUser);
								sFlow2.setOptUserId(itemList[i].getOptUserId());
								sFlow2.setStatus(0);
								sFlow2.setDateUpdater(date);
    							dao.updateIgnoreNull(sFlow2);
    						}
							purchase2.setStatus(0);
							purchase2.setDateUpdater(new Date());
							dao.update(purchase2);
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
            re.setInfo(Params.str_notLogin);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 获取采购单唯一编号
	 * @return string
	 */
	@At("pur/getPurNo")
	@Ok("json")
	public String getCompanyNo(){
		return UniqueNoUtils.createAppNo();
	}
	
	/**
	 * 审核采购申请
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param purNos 采购号list
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/verifyPur")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result verifyPur(@Param("purNos") String [] purNos,@Param("opt") int opt,@Param("hint")String hint,@Param("::acu.")AppHttpUtils acu,HttpSession session){
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
                		for (int i = 0; i < purNos.length; i++) {
    						Purchase purchase = dao.fetch(Purchase.class, Cnd.where("appNo","=",purNos[i]));
    						if (opt==1) {
    							purchase.setStatus(1);
    							purchase.setStatusInfo("已通过");
    							purchase.setHint("");
    						}else if (opt==-1) {
    							purchase.setStatus(-1);
    							purchase.setStatusInfo("不通过");
    							purchase.setHint(hint);
    						}else {
    				            re.setInfo("操作码错误");
    				            re.setStatus(Params.status_failed);
    							return re;
    						}
    						purchase.setVerifyDate(new Date());
    						purchase.setVerifyUserId(user.getId());
    						purchase.setVerifyUsername(user.getUsername());
    						dao.update(purchase,"status|verifyDate|verifyUserId|verifyUsername|hint");
    						List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("preAppNo","=",purNos[i]));
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
	 * 获取审核通过采购单明细,采购人的采购任务
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/getPurTask")
	@Ok("json")
	public Result getPurTask(int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"pur_getTaskList", dao)){
                    	List<StockFlow> list = dao.query(StockFlow.class, Cnd.where("optUserId","=",user.getId()).and("status","=",StockFlowUtils.verPass),dao.createPager(pageNo, pageSize));
                    	re.setList(list);
                    	re.setTotal(dao.count(StockFlow.class, Cnd.where("optUserId","=",user.getId()).and("status","=",StockFlowUtils.verPass)));
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
	 * 采购人完成采购任务
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/doPurTask")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result doPurTask(@Param("itemList")StockFlow[] itemList,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"pur_getTaskList", dao)){
                    	for (int i = 0; i < itemList.length; i++) {
    						StockFlow stockFlow = dao.fetch(StockFlow.class, Cnd.where("id","=",itemList[i].getId()));
    						stockFlow.setStatus(StockFlowUtils.purFinish);
    						stockFlow.setPrice(itemList[i].getPrice());
    						stockFlow.setSum(itemList[i].getSum());
    						stockFlow.setOptDate(new Date());
    						dao.update(stockFlow,"status|price|sum|optDate");
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
	 * 获取采购人列表-combo封装
	 * @return
	 */
	@At("com/getPurTaskerListCombo")
	@Ok("json")
	public Result getPurTaskerListCombo(@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re =new Result();
    	List<ComboUtils> purTaskerList=new LinkedList<ComboUtils>();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);  
            }
            else{
            	List<User> list =new ArrayList<User>();
            	List<User> userList=dao.query(User.class, Cnd.where("companyNo","=",user.getCompanyNo()));
            	for (User user2 : userList) {
					if(dao.count(UserAuth.class,Cnd.where("userId","=", user2.getId()).and("authCode","=","pur_getTaskList"))!=0){
						list.add(user2);
					}
				}
            	for(User p:list){
            		ComboUtils cu=new ComboUtils();
            		cu.setText(p.getUsername());
            		cu.setValue(p.getId()+"");
            		purTaskerList.add(cu);
            	}
            	re.setList(purTaskerList);
                re.setInfo(Params.str_optSuccess);
                re.setStatus(Params.status_success);
            }
        }
        return re;
	}
	
	/**
	 * 获取我的已采购任务列表
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/getMyPurTask")
	@Ok("json")
	public Result getMyPurTask(int pageNo,int pageSize,long total,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"pur_getTaskList", dao)){
                    	List<StockFlow> list = dao.query(StockFlow.class, Cnd.where("optUserId","=",user.getId()).and("status",">",StockFlowUtils.verPass),dao.createPager(pageNo, pageSize));
                    	re.setList(list);
                    	re.setTotal(dao.count(StockFlow.class, Cnd.where("optUserId","=",user.getId()).and("status",">",StockFlowUtils.verPass)));
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
	 * 删除采购单
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param purNos 采购号list
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/delPur")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result delPur(@Param("purNos") String [] purNos,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"aly_delPur", dao)){
                		
                		for (int i = 0; i < purNos.length; i++) {
                			Purchase purchase = dao.fetch(Purchase.class, Cnd.where("appNo","=",purNos[i]));
                        	if(purchase!=null&&(purchase.getStatus()>0||purchase.getAppUserId()!=user.getId()||purchase.getStatus()==-9)){
                        		re.setInfo("当前状态不能修删除！");
                        		re.setStatus(Params.status_failed);
                        		return re;
                        	}
                			dao.clear(Purchase.class, Cnd.where("appNo","=",purNos[i]));
    						dao.clear(StockFlow.class,Cnd.where("preAppNo","=",purNos[i]));
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
}
