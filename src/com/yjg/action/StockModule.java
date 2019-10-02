package com.yjg.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.dao.util.cri.Static;
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
import com.yjg.entity.Purchase;
import com.yjg.entity.Stock;
import com.yjg.entity.StockFlow;
import com.yjg.entity.StockPlace;
import com.yjg.entity.StockTaking;
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
import com.yjg.utils.StrUtils;

/**
 * 库存管理模块
 * 访问路径 stm/***
 * @author yjg
 *
 */
@InjectName("stockModule")
@IocBean
public class StockModule {

	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	public static String getStockNameById(long stockplaceId,Dao dao) {
		StockPlace sPlace = dao.fetch(StockPlace.class,Cnd.where("id", "=", stockplaceId));
		  if(sPlace==null)return "该仓库不存在";
		  else return sPlace.getStockplace();
	}
		
	/**
	 * 简易检索库存数据-不分页
	 * @param key 关键字 ，针对 名称,型号检索
	 * @param type 1=用于前端suggestion,返回结果需要经过封装
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("stm/soStockQuick")
	@Ok("json")
	public Object soStockQuick(String key,int type,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	//List<UserStockPlace> usplist=dao.query(UserStockPlace.class, Cnd.where("userId","=",user.getId()));//获取用户权限
                	
                	//测试
                	List<Stock> list=new LinkedList<Stock>();
                	for(int i=1;i<=9;i++){
                		Stock st=new Stock();
                		st.setId(i);
                		st.setName("物品"+i);
                		st.setModel("型号"+i);
                		st.setType("类型"+i);
                		st.setUnit("单位"+i);
                		list.add(st);
                	}
                	if(type==1){//用于前端suggestion组件
                		Map<String,Object> reMap=new HashMap<String, Object>();
                		reMap.put("valueField", "name");
                		reMap.put("data", list);
                		return reMap;
                	}
                	else{//普通应用场景
                		re.setList(list);
                    	re.setTotal(list.size());
                    	re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
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
	 * 获取权限下的库存记录
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param type 1=库存数大于0
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return
	 */
	@At("stm/getStockList")
	@Ok("json")
	public Result getStockList(int pageNo,int pageSize,long total,int type,long stockplaceId,int isWarn,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"st_getAllList", dao)){
                		List<Stock> list=new ArrayList<Stock>();
                    	if(type==0){//我管理的所有仓库
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
        					}else {
        						if (isWarn==1) {
        							SqlExpressionGroup sql = Cnd.exps(new Static("number > numberMax")).or(new Static("number < numberMin"));
        							list = dao.query(Stock.class,Cnd.where(sqlGroup).and(sql),dao.createPager(pageNo, pageSize));
    								re.setTotal(dao.count(Stock.class,Cnd.where(sqlGroup).and(sql)));
								}else {
									list = dao.query(Stock.class,Cnd.where(sqlGroup),dao.createPager(pageNo, pageSize));
									re.setTotal(dao.count(Stock.class,Cnd.where(sqlGroup)));
								}
							}	
                    	}else if(type==1) {//我管理的单个仓库
    						if (isWarn==1) {
    							SqlExpressionGroup sql = Cnd.exps(new Static("number > numberMax")).or(new Static("number < numberMin"));//静态比较两个字段大小
    							list = dao.query(Stock.class,Cnd.where("stockplaceId","=",stockplaceId).and(sql),dao.createPager(pageNo, pageSize));
                        		re.setTotal(dao.count(Stock.class,Cnd.where("stockplaceId","=",stockplaceId).and(sql)));
    						}else {
    							list = dao.query(Stock.class,Cnd.where("stockplaceId","=",stockplaceId),dao.createPager(pageNo, pageSize));
                        		re.setTotal(dao.count(Stock.class,Cnd.where("stockplaceId","=",stockplaceId)));
							}
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
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 设置库存数量警戒值
	 * @param itemList
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("stm/setMinAndMax")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result setMinAndMax(@Param("itemList") Stock[] itemList,@Param("::acu.")AppHttpUtils acu,HttpSession session){
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
                		Date date = new Date();
                		for (int i = 0; i < itemList.length; i++) {
                			Stock stock = dao.fetch(Stock.class,Cnd.where("id","=",itemList[i].getId()));
                			stock.setNumberMax(itemList[i].getNumberMax());
                			stock.setNumberMin(itemList[i].getNumberMin());
                			stock.setDateUpdater(date);
                			dao.update(stock);
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
	 * 获取入库记录
	 * @param pageNo
	 * @param pageSize
	 * @param total
	 * @param type 0 全面待入库申请   1 已通过的待入申请 2 我通过的待入申请
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("stm/getStockInList")
	@Ok("json")
	public Result getStockInList(int pageNo,int pageSize,long total,int type,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
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
                	if(AuthUtils.checkUserAuth(user.getId(),"st_getInApplylist", dao)){
                    	List<StockFlow> list = null;
                    	if(type==0){
                    		list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.purFinish).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn),dao.createPager(pageNo, pageSize)); //待入库记录
                        	re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.purFinish).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn)));
                    	}else if(type==1) {
                    		list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn),dao.createPager(pageNo, pageSize));//已入库记录
                        	re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn)));
    					}else if (type==2) {//我的审核记录
    						list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("stockUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn),dao.createPager(pageNo, pageSize));
                        	re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("stockUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockIn)));
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
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 审核入库申请
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param ids stockFlow List
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("stm/verifyIn")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result verifyIn(@Param("ids") String [] ids,@Param("opt") int opt,@Param("hint")String hint,@Param("::acu.")AppHttpUtils acu,HttpSession session){
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
                		Date date = new Date();
                		for (int i = 0; i < ids.length; i++) {
                			StockFlow stockFlow = dao.fetch(StockFlow.class,Cnd.where("id","=",ids[i]));
								if (opt==1) {
									
									if(stockFlow.getNumber()<=0){
										re.setInfo("入库数量必须大于零！");
										re.setStatus(Params.status_failed);
										return re;
									}
									if (stockFlow.getSfType().equals(StockFlowUtils.stockIn_return)) {
										if (dao.fetch(StockFlow.class,Cnd.where("id","=", stockFlow.getPreItemId())).getNowNum()-stockFlow.getNumber()<0) {
											re.setInfo("你退料的数量超出领料数量！");
											re.setStatus(Params.status_failed);
											return re;
										};
									}
									
									//更新stockFlow 状态
									stockFlow.setStatus(9);
									stockFlow.setStockDate(date);
									stockFlow.setStockUser(user.getUsername());
									stockFlow.setStockUserId(user.getId());
									Stock stock = dao.fetch(Stock.class,Cnd.where("id", "=",stockFlow.getStockId()));
									stockFlow.setStockNumber(stock.getNumber());
									stockFlow.setStockedNumber(stock.getNumber()+stockFlow.getNumber());
									dao.update(stockFlow);
									Purchase purchase = dao.fetch(Purchase.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									StockTaking stockTaking = dao.fetch(StockTaking.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									if(purchase!=null){//领料退料 不用判断采购单
										List<StockFlow> sflist = dao.query(StockFlow.class,Cnd.where("preAppNo","=", stockFlow.getPreAppNo()));
										//判断采购单状态
										int n=0;
										for (StockFlow stockFlow2 : sflist) {
											if (stockFlow2.getStatus()==9)n+=1;
											if (stockFlow2.getStatus()==-9) {
												n=-1;
												break;
											}
										}
										if(n<0)purchase.setStatus(-9);
										else if(n==sflist.size())purchase.setStatus(9);
										else if (n<sflist.size()) purchase.setStatus(6);
										dao.update(purchase);
									}else if (stockTaking!=null) {//盘库入库 更新盘库单
										
									}else {//更新退料单状态
										StockFlow sFlow = dao.fetch(StockFlow.class,Cnd.where("id","=", stockFlow.getPreItemId()));//退料单
										sFlow.setNowNum(sFlow.getNowNum()-stockFlow.getNumber());
										dao.update(sFlow);
									}
									
									//更新stock表
									stock.setDateUpdater(date);
									stock.setNumber(stock.getNumber()+stockFlow.getNumber());
									stock.setInNum(stock.getInNum()+stockFlow.getNumber());
									stock.setSum(stock.getSum()+stockFlow.getSum());
									stock.setInSum(stock.getInSum()+stockFlow.getSum());
									stock.setPriceIn(stock.getInSum()/stock.getInNum());
									stock.setStockInLast(date);
									stock.setPriceLast(stockFlow.getPrice());
									dao.update(stock);
								}else if (opt==-1) {
									stockFlow.setStatus(-9);//入库不通过
									stockFlow.setStockDate(date);
									stockFlow.setStockUser(user.getUsername());
									stockFlow.setStockUserId(user.getId());
									dao.update(stockFlow);
									Purchase purchase = dao.fetch(Purchase.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									StockTaking stockTaking = dao.fetch(StockTaking.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									if (purchase!=null) {
										purchase.setHint(hint);
										purchase.setStatus(-9);
										dao.update(purchase);
									}
									if (stockTaking!=null) {
										stockTaking.setHint(hint);
										stockTaking.setStatus(-9);
										dao.update(stockTaking);
									}
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
	 * 获取出库记录
	 * @param pageNo
	 * @param pageSize
	 * @param total
	 * @param type 0 全面待出库申请   1 已通过的待出库申请 2 我通过的待出库申请 3我的已出库记录
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("stm/getStockOutList")
	@Ok("json")
	public Result getStockOutList(int pageNo,int pageSize,long total,int type,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
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
                	if(AuthUtils.checkUserAuth(user.getId(),"st_getOutApplylist", dao)){
                    	List<StockFlow> list = null;
                    	if(type==0){
                    		list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.verPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut)); //待出库记录
                    		re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.verPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut)));
                    	}else if(type==1) {
                    		list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut));//已入库记录
                    		re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut)));
    					}else if (type==2) {//我的审核记录
    						list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("stockUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut));
    						re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("stockUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut)));
    					}else if (type==3) {//我的已出库记录
    						list = dao.query(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("appUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut));
    						for (StockFlow stockFlow : list) {
								stockFlow.setPrice(dao.fetch(Stock.class,Cnd.where("id","=",stockFlow.getStockId())).getPriceIn());
//								stockFlow.setNowNum(getOutNumNowTime(stockFlow.getId(),dao));
								if (stockFlow.getNowNum()<=0) {
									list.remove(stockFlow);
								}
							}
    						re.setTotal(dao.count(StockFlow.class,Cnd.where("status","=",StockFlowUtils.stockPass).and("appUserId","=", user.getId()).and(sqlGroup).and("flowType","=", StockFlowUtils.stockOut)));
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
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	/**
	 * 审核出库申请
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param ids stockFlow List
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("stm/verifyOut")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result verifyOut(@Param("ids") String [] ids,@Param("opt") int opt,@Param("hint")String hint,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	if(AuthUtils.checkUserAuth(user.getId(),"st_outApply", dao)){
                		Date date = new Date();
                		for (int i = 0; i < ids.length; i++) {
                			StockFlow stockFlow = dao.fetch(StockFlow.class,Cnd.where("id","=",ids[i]));
								if (opt==1) {
									
									//更新stockFlow 状态
									stockFlow.setStatus(9);
									stockFlow.setStockDate(date);
									stockFlow.setStockUser(user.getUsername());
									stockFlow.setStockUserId(user.getId());
									Stock stock = dao.fetch(Stock.class,Cnd.where("id", "=",stockFlow.getStockId()));
									stockFlow.setStockNumber(stock.getNumber());
									stockFlow.setStockedNumber(stock.getNumber()-stockFlow.getNumber());
																
									//更新stock表
									if(stockFlow.getNumber()<=0){
										re.setInfo("出库数量必须大于零！");
										re.setStatus(Params.status_failed);
										return re;
									}
									
									stock.setDateUpdater(date);
									stock.setOutNum(stock.getOutNum()+stockFlow.getNumber());
									stock.setOutSum(stock.getOutSum()+stockFlow.getSum());
									stock.setPriceOut(stock.getOutSum()/stock.getOutNum());
									float fNumber = stock.getNumber()-stockFlow.getNumber();
									if(fNumber<0){
										re.setInfo("出库数量不能大于库存数量！");
										re.setStatus(Params.status_failed);
										return re;
									}
									stock.setNumber(fNumber);
									stock.setStockOutLast(date);
									stock.setSum(stock.getSum()-stockFlow.getSum());
									dao.update(stock);
									dao.update(stockFlow);
									
									
									List<StockFlow> sflist = dao.query(StockFlow.class,Cnd.where("preAppNo","=", stockFlow.getPreAppNo()));
									int n=0;
									for (StockFlow stockFlow2 : sflist) {
										if (stockFlow2.getStatus()==9)n+=1;
										if (stockFlow2.getStatus()==-9) {
											n=-1;
											break;
										}
									}
									//判断领购单状态
									Picking picking = dao.fetch(Picking.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									if (picking!=null) {
										if(n<0)picking.setStatus(-9);
										else if(n==sflist.size())picking.setStatus(9);
										else if (n<sflist.size()) picking.setStatus(6);
										dao.update(picking);
									}
								}else if (opt==-1) {
									stockFlow.setStatus(-9);//出库不通过
									stockFlow.setStockDate(date);
									stockFlow.setStockUser(user.getUsername());
									stockFlow.setStockUserId(user.getId());
									dao.update(stockFlow);
									Picking picking = dao.fetch(Picking.class,Cnd.where("appNo","=", stockFlow.getPreAppNo()));
									if (picking!=null) {
										picking.setHint(hint);
										picking.setStatus(-9);
										dao.update(picking);
									}
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
	 * 获取单号详情
	 * @param pageNo 开始页码 
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param stockId 库存记录id
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("stm/getStockFlowList")
	@Ok("json")
	public Result getStockFlowList(int pageNo,int pageSize,long total,String preAppNo,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
					List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("preAppNo", "=", preAppNo));
                	re.setList(list);
                	re.setTotal(dao.count(StockFlow.class,Cnd.where("preAppNo", "=", preAppNo)));
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
 * 获取该物品所有明细
 * @param pageNo 开始页码 
 * @param pageSize 每页大小
 * @param total 总记录数
 * @param stockId 库存记录id
 * @param acu
 * @param session
 * @return
 */
@At("stm/getProFlowList")
@Ok("json")
public Result getProFlowList(int pageNo,int pageSize,long total,String stockId,@Param("::acu.")AppHttpUtils acu,HttpSession session){
	Result re=new Result();
    if(MyUtils.checkRequestOk(acu, session)){
        User user=service.getUserInfo(acu, session);
        if(user==null){
            re.setInfo(Params.loginOutInfoStr);
            re.setStatus(Params.status_failed);
        }
        else{
            try {
				List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("stockId", "=", stockId));
            	re.setList(list);
            	re.setTotal(dao.count(StockFlow.class,Cnd.where("stockId", "=", stockId)));
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
 * 获取库存动态
 * @param pageNo 开始页码 
 * @param pageSize 每页大小
 * @param total 总记录数
 * @param stockId 库存记录id
 * @param acu
 * @param session
 * @return
 */
@At("stm/getStockDynamicList")
@Ok("json")
public Result getStockDynamicList(int pageNo,int pageSize,long total,String stockIds,@Param("::acu.")AppHttpUtils acu,HttpSession session){
	Result re=new Result();
    if(MyUtils.checkRequestOk(acu, session)){
        User user=service.getUserInfo(acu, session);
        if(user==null){
            re.setInfo(Params.loginOutInfoStr);
            re.setStatus(Params.status_failed);
        }
        else{
            try {
                List<UserStockPlace> stockList = dao.query(UserStockPlace.class,Cnd.where("userId","=",user.getId()));
            	SqlExpressionGroup stockGroup = null;
            	for (UserStockPlace usp : stockList) {//我负责的仓库
            		SqlExpressionGroup stockSingle = Cnd.exps("stockplaceId","=",usp.getStockplaceId());
            		if(stockGroup==null)stockGroup=stockSingle;
            		else {
            			stockGroup = stockGroup.or(stockSingle);
					}
				}
                if (stockGroup==null) {
                	re.setInfo("你还没有管理的仓库！请联系管理员添加");
                    re.setStatus(Params.status_failed);
                    return re;
                }
                if (stockIds==null) {
    				List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("status","=",9).and("flowType",">=",-1).and(stockGroup));
    	            re.setList(list);
    	            re.setTotal(dao.count(StockFlow.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("status","=",9).and(stockGroup)));
    	            re.setInfo(Params.str_optSuccess);
    	            re.setStatus(Params.status_success);
    			}else {
    	           String[] id=stockIds.split(",");
    	           SqlExpressionGroup sqlGroup = null;
    		       for(int i=0;i<id.length;i++){
    		    	   SqlExpressionGroup sqlSingle = Cnd.exps("stockId","=",id[i]);
    		    	   if(sqlGroup==null)sqlGroup=sqlSingle;
    	           		else sqlGroup = sqlGroup.or(sqlSingle);
    				}
    				List<StockFlow> list = dao.query(StockFlow.class,Cnd.where(sqlGroup).and("companyNo","=",user.getCompanyNo()).and("status","=",9),dao.createPager(pageNo, pageSize));
    	            re.setList(list);
    	            re.setTotal(dao.count(StockFlow.class,Cnd.where(sqlGroup).and("companyNo","=",user.getCompanyNo()).and("status","=",9)));
    	            re.setInfo(Params.str_optSuccess);
    	            re.setStatus(Params.status_success);
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
 * 搜索库存动态
 * @param pageNo 开始页码 
 * @param pageSize 每页大小
 * @param total 总记录数
 * @param stockId 库存记录id
 * @param acu
 * @param session
 * @return
 */
@At("stm/soStockDynamicList")
@Ok("json")
public Result soStockDynamicList(int pageNo,int pageSize,long total
		,String name,String proNo,long splaceId,String type,Date startDate
		,Date endDate,String appUser,@Param("::acu.")AppHttpUtils acu,HttpSession session){
	Result re=new Result();
    if(MyUtils.checkRequestOk(acu, session)){
        User user=service.getUserInfo(acu, session);
        if(user==null){
            re.setInfo(Params.loginOutInfoStr);
            re.setStatus(Params.status_failed);
        }
        else{
            try {
            	String[] types = {"采购入库","领料退料入库","盘库入库","领料出库","销售出库","盘库出库"};
        		SqlExpressionGroup sql=Cnd.exps("1", "=", 1);
        		if(!StrUtils.isEmpty(name))
        			sql.and("name", "like", "%"+name+"%");
        		if(!StrUtils.isEmpty(proNo))
        			sql.and("proNo", "like", "%"+proNo+"%");
        		if(splaceId!=0)
        			sql.and("stockplaceId", "=", splaceId);
           		if(!StrUtils.isEmpty(appUser))
        			sql.and("appUser", "like", "%"+appUser+"%");
        		if(type.equals("所有入库"))
        			sql.and("flowType", "=",1);
        		if(type.equals("所有出库"))
        			sql.and("flowType", "=",-1);
        		if(StrUtils.contains(types, type))
        			sql.and("sfType", "=",type);
        		if(startDate!=null)
        			sql.and("stockDate", ">=", startDate);
        		if(endDate!=null)
        			sql.and("stockDate", "<=", endDate);
    			List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("status","=",9).and(sql),dao.createPager(pageNo, pageSize));
    	        re.setList(list);
    	        re.setTotal(dao.count(StockFlow.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("status","=",9).and(sql)));
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
