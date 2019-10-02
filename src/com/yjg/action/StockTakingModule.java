package com.yjg.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.yjg.AppService;
import com.yjg.entity.Stock;
import com.yjg.entity.StockFlow;
import com.yjg.entity.StockTaking;
import com.yjg.entity.SystemLog;
import com.yjg.entity.User;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;
import com.yjg.utils.ServiceData;
import com.yjg.utils.StockFlowUtils;
import com.yjg.utils.UniqueNoUtils;

/**
 * 盘库管理模块
 * 访问路径 skm/***
 * @author lch
 *
 */
@IocBean
@InjectName("StockTakingModule")
public class StockTakingModule {
	
	
	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	/**
	 * 获取盘库单唯一编号
	 * @return string
	 */
	public static String getStockTakingNo(){
		return UniqueNoUtils.createStockTakingNo();
	}
	
	/**
	 * 获取盘库单列表
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param total 总记录数
	 * @param type 类型 
	 * @param acu
	 * @param session
	 * @return
	 */
	@At("skm/getStockTakingList")
	@Ok("json")
	public Result getStockTakingList(int pageNo,int pageSize,long total,int type,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                	List<StockTaking> list = new ArrayList<StockTaking>();
                	if (type==10) {//我的盘库单
                		list= dao.query(StockTaking.class,Cnd.where("appUserId","=", user.getId()),dao.createPager(pageNo, pageSize));
                		for (StockTaking stockTaking : list) {
    						List<StockFlow> sflist = dao.query(StockFlow.class,Cnd.where("preAppNo","=", stockTaking.getAppNo()));
    						int n=0;
    						for (StockFlow stockFlow2 : sflist) {
    							if (stockFlow2.getStatus()==9)n+=1;
    							if (stockFlow2.getStatus()==-9) {
    								n=-1;
    								break;
    							}
    						}
							if(n<0)stockTaking.setStatus(-9);
							else if(n==sflist.size())stockTaking.setStatus(9);
							dao.update(stockTaking);
						}
                		re.setTotal(dao.count(StockTaking.class,Cnd.where("appUserId","=", user.getId())));
					}
                   	if (type==0) {//待审核的盘库单
                		list= dao.query(StockTaking.class,Cnd.where("companyNo","=", user.getCompanyNo()).and("status", "=", 0),dao.createPager(pageNo, pageSize));
                		re.setTotal(dao.count(StockTaking.class,Cnd.where("companyNo","=", user.getCompanyNo()).and("status", "=", 0)));
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
            re.setInfo(Params.str_noRequestAuth);
            re.setStatus(Params.status_failed);
        }
        return re;
	}
	
	@At("skm/addStockTaking")
	@Ok("json")	
	public Result addPicking(@Param("stockIds")String stockIds,@Param("::acu.")AppHttpUtils acu,HttpSession session,HttpServletRequest req){
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
                		String [] stockId=stockIds.split(",");
                    	if(stockIds==null || stockId.length==0){
                    		re.setInfo("参数不完整");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                        if(!AuthUtils.checkUserAuth(user.getId(),"aly_addPick", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                        }
                        String appNo =  getStockTakingNo();
                       for (int i = 0; i < stockId.length; i++) {
                    	   	Stock stock =dao.fetch(Stock.class,Cnd.where("id","=", stockId[i]));
                    	   	StockFlow stockFlow = new StockFlow();
        					stockFlow.setAppDate(date);
        					stockFlow.setAppUser(user.getUsername());
        					stockFlow.setAppUserId(user.getId());
        					stockFlow.setAuthor(user.getUsername());
        					stockFlow.setAuthorId(user.getId());
        					stockFlow.setCompanyNo(user.getCompanyNo());
        					stockFlow.setDateCreater(date);
        					stockFlow.setStatus(-2);
        					stockFlow.setStockId(stock.getId());
        					stockFlow.setPreAppNo(appNo);
        					stockFlow.setStockplaceId(stock.getStockplaceId());
        					stockFlow.setStockplace(stock.getStockplace());
        					stockFlow.setName(stock.getName());
        					stockFlow.setModel(stock.getModel());
        					stockFlow.setNowNum(stock.getNumber());
        					stockFlow.setPrice(stock.getPriceIn());
        					stockFlow.setProductId(stock.getProductId());
        					stockFlow.setProNo(stock.getProNo());
        					stockFlow.setUnit(stock.getUnit());
        					stockFlow.setSfType("盘库");
        					dao.fastInsert(stockFlow);
        				}
                       StockTaking stockTaking =  new StockTaking();
                       stockTaking.setAppNo(appNo);
                       stockTaking.setAppDate(date);
                       stockTaking.setAppUser(user.getUsername());
                       stockTaking.setAppUserId(user.getId());
                       stockTaking.setAuthor(user.getUsername());
                       stockTaking.setAuthorId(user.getId());
                       stockTaking.setCompanyNo(user.getCompanyNo());
                       stockTaking.setDateCreater(date);
                       stockTaking.setStatus(-2);
                       stockTaking.setStockplace(dao.fetch(Stock.class,Cnd.where("id","=", stockId[0])).getStockplace());
                       stockTaking.setStockplaceId(dao.fetch(Stock.class,Cnd.where("id","=", stockId[0])).getStockplaceId());
                       dao.fastInsert(stockTaking);
                       re.setInfo(Params.str_optSuccess+",盘库单号:"+appNo);
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
	
	@At("skm/countStockTaking")
	@Ok("json")	
	public Result countStockTaking(@Param("flowIds")String flowIds,@Param("numbers")String numbers,@Param("::acu.")AppHttpUtils acu,HttpSession session,HttpServletRequest req){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
                try {
                		String [] flowId=flowIds.split(",");
                		String [] number=numbers.split(",");
                    	if(flowIds==null || flowId.length==0||number.length!=flowId.length){
                    		re.setInfo("参数不完整");
                    		re.setStatus(Params.status_failed);
                    		return re;
                    	}
                        if(!AuthUtils.checkUserAuth(user.getId(),"aly_addPick", dao)){
                                    re.setInfo(Params.str_noRequestAuth);
                                    re.setStatus(Params.status_failed);
                                    return re;
                        }
                       for (int i = 0; i < flowId.length; i++) {
                    	   	StockFlow stockFlow =dao.fetch(StockFlow.class,Cnd.where("id","=", flowId[i]));
                    	   	if (Long.parseLong(number[i])>0) {
                    	   		stockFlow.setSfType(StockFlowUtils.stockIn_stockTaking);
                    	   		stockFlow.setFlowType(1);
                    	   		stockFlow.setNumber(Long.parseLong(number[i]));
                    	   		stockFlow.setStatus(0);
							}else if(Long.parseLong(number[i])<0) {
	                    	   	stockFlow.setSfType(StockFlowUtils.stockOut_stockTaking);
	                    	   	stockFlow.setFlowType(-1);
	                    	   	stockFlow.setNumber(Long.parseLong(number[i])*-1);
	                    	   	stockFlow.setStatus(0);
							}else{
								stockFlow.setSfType("盘库");
								stockFlow.setStatus(0);
								stockFlow.setFlowType(-2);
								stockFlow.setNumber(0);
							}
                    	   	stockFlow.setSum(stockFlow.getPrice()*stockFlow.getNumber());
        					dao.update(stockFlow);
        					int flag=1;
        					List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("preAppNo", "=", stockFlow.getPreAppNo()));
        					for (StockFlow stockFlow2 : list) {
								if (stockFlow2.getStatus()==-2) {
									flag=0;
									break;
								}
							}
        					if(flag==1){
        						StockTaking stockTaking =dao.fetch(StockTaking.class, Cnd.where("appNo", "=", stockFlow.getPreAppNo()));
        						stockTaking.setStatus(0);
        						dao.update(stockTaking);
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
	
	/**
	 * 审核盘库单
	 * @param hint 回执信息
	 * @param opt 操作码 1 通过 -1  不通过
	 * @param purNos 采购号list
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("skm/verifyStockTaking")
	@Ok("json")
	public Result verifyStockTaking(@Param("preAppNo") String preAppNo,@Param("opt") int opt,@Param("hint")String hint,@Param("::acu.")AppHttpUtils acu,HttpSession session){
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
                		StockTaking stockTaking = dao.fetch(StockTaking.class, Cnd.where("appNo","=",preAppNo));
    						if (opt==1) {
    							stockTaking.setStatus(1);
    							stockTaking.setHint("");
    						}else if (opt==-1) {
    							stockTaking.setStatus(-1);
    							stockTaking.setHint(hint);
    						}else {
    				            re.setInfo("操作码错误");
    				            re.setStatus(Params.status_failed);
    							return re;
    						}
    						stockTaking.setVerifyDate(new Date());
    						stockTaking.setVerifyUserId(user.getId());
    						stockTaking.setVerifyUser(user.getUsername());
    						dao.update(stockTaking,"status|verifyDate|verifyUserId|verifyUser|hint");
    						List<StockFlow> list = dao.query(StockFlow.class,Cnd.where("preAppNo","=",preAppNo));
    						for (StockFlow stockFlow : list) {
    							if (opt==1) {
    								if (stockFlow.getFlowType()==1) {
    									stockFlow.setStatus(6);
									}else if(stockFlow.getFlowType()==-1){
    									stockFlow.setStatus(1);
									}else stockFlow.setStatus(9);
    							}else if (opt==-1) {
    								stockFlow.setStatus(-1);
    							}
    							stockFlow.setVerifyDate(new Date());
    							stockFlow.setVerifyUser(user.getUsername());
    							stockFlow.setVerifyUserId(user.getId());
    							dao.update(stockFlow,"status|verifyDate|verifyUserId|verifyUser");
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
