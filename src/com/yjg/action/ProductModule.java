package com.yjg.action;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.yjg.AppService;
import com.yjg.entity.Product;
import com.yjg.entity.Stock;
import com.yjg.entity.User;
import com.yjg.utils.AppHttpUtils;
import com.yjg.utils.AuthUtils;
import com.yjg.utils.MyUtils;
import com.yjg.utils.Params;
import com.yjg.utils.Result;

/**
 * 品名模块
 * 访问路径pro/***
 * @author lch
 *
 */
@IocBean
@InjectName("ProductModule")
public class ProductModule {
	@Inject
	private Dao dao;
	
	@Inject
	private AppService service;
	
	/**
	 * 获取品名列表
	 * @param pageNo 开始页码
	 * @param pageSize 每页大小
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pur/getProList")
	@Ok("json")
	public Result getPurList(@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){ 
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AppService.checkUserAuth(user.getId(),"pro_getList",dao)){
            		List<Product> list=dao.query(Product.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted),dao.createPager(pageNo, pageSize));
            		Collections.reverse(list);
            		re.setList(list);
            		re.setTotal(dao.count(Product.class,Cnd.where("companyNo","=",user.getCompanyNo()).and("isDeleted","=",Params.status_notDeleted)));
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
	 * 批量新增品名
	 * @param name 名称s
	 * @param model 型号s
	 * @param unit 单位s
	 * @param remark 备注s
	 * @param session
	 * @return
	 */
	@At("pro/addPro")
	@Ok("json")
	public Result addPro(String name,String model,String unit,String remark,String proNo,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){ 
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	if(AppService.checkUserAuth(user.getId(),"pro_add",dao)){
            		Date date=new Date();
        			String[] nameList=name.split(",");
        			String[] modelList=model.split(",");
        			String[] unitList=unit.split(",");
        			String[] remarkList=remark.split(",");
        			String[] proNoList=proNo.split(",");
        			int length=nameList.length,j=0;
        			if(modelList.length!=length ||proNoList.length!=length || unitList.length!=length||length==0){
        				re.setInfo("注意名称、编号、型号、单位必填");
        				re.setStatus(Params.status_failed);
        			}
        			else{
        				for(int i=0;i<length;i++){
        					Product product = new Product();
        					String _name="",_model="",_unit="",_remark="",_proNo="";
        					try {
        						_name=nameList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_model=modelList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_unit=unitList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_remark=remarkList[i];		
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_proNo=proNoList[i];		
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					if(MyUtils.checkProHad(_proNo,user.getCompanyNo(),dao)){
        						j++;
        						continue;
        					}
        					product.setAuthor(user.getUsername());
        					product.setAuthorId(user.getId());
        					product.setDateCreater(date);
        					product.setLastModifier(user.getUsername());
        					product.setModel(_model);
        					product.setName(_name);
        					product.setProNo(_proNo);
        					product.setRemark(_remark);
        					product.setUnit(_unit);
        					product.setCompanyNo(user.getCompanyNo());
        					dao.fastInsert(product);
        				}
        				re.setInfo("操作成功,共添加品名"+(length-j)+"条，"+j+"条品名表里已存在");
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
	 * 删除品名
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pro/delPros")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result delPros(@Param("proIds")String proIds,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
        		if(proIds==null || proIds.equals("")){
                    re.setInfo(Params.info_errr_null);
                    re.setStatus(Params.status_failed);
        		}else{
            			String [] id=proIds.split(",");
            			for(int i=0;i<id.length;i++){
            				if(!id[i].equals("")){
            					Product product =dao.fetch(Product.class,Cnd.where("id","=", id[i]));
            					product.setDeleted(true);
            					product.setDateUpdater(new Date());
            					product.setLastModifier(user.getUsername());
            					dao.update(product);
            				}
            			}
                        re.setInfo(Params.str_optSuccess);
                        re.setStatus(Params.status_success);
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
	 * 修改更新品名
	 * @param acu 移动端辅助类
	 * @param session 网页session
	 * @return {@link Result} 
	 */
	@At("pro/updatePro")
	@Ok("json")
	public Result updatePro(@Param("..")Product product,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
            else{
            	Date date =new Date();
            	if(AuthUtils.checkUserAuth(user.getId(),"com_setAuth", dao)){
            		if(product.getProNo().equals("")||product.getName().equals("")||product.getId()==0){
                        re.setInfo(Params.info_errr_null);
                        re.setStatus(Params.status_failed);
            		}else{
                        if(dao.count(Product.class,Cnd.where("proNo","=", product.getProNo()).and("companyNo","=",user.getCompanyNo()).and("id","!=",product.getId()))!=0){
                            re.setInfo("该产品编号已存在请修改！");
                            re.setStatus(Params.status_failed);
                            return re;
                        }
            			product.setDateUpdater(date);
            			product.setLastModifier(user.getUsername());
            			dao.updateIgnoreNull(product);
            			Stock stock = dao.fetch(Stock.class,Cnd.where("productId", "=", product.getId()));
            			if (stock!=null) {
                			stock.setDateUpdater(date);
                			stock.setModel(product.getModel());
                			stock.setName(product.getName());
                			stock.setUnit(product.getUnit());
                			stock.setRemark(product.getRemark());
                			stock.setOptUser(user.getUsername());
                			stock.setOptUserId(user.getId());
                			dao.updateIgnoreNull(stock);
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
	* @Title: addProToStock   
	* @Description: 添加仓库
	* @param sPlace
	* @param acu
	* @param session
	 */
	@At("pro/addProToStock")
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)//json格式参数适配器
	public Result addProToStock(@Param("proIds")String proIds,@Param("stockplaceId") long stockplaceId,@Param("::acu.")AppHttpUtils acu,HttpSession session) {
		Result re=new Result();		
		if(proIds==null||stockplaceId==0){
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
            	if(AuthUtils.checkUserAuth(user.getId(),"st_addProInto", dao)){
            		Date date =new Date();
            		String stockplace= StockModule.getStockNameById(stockplaceId,dao);
            		if (stockplace.equals("该仓库不存在")) {
            			re.setInfo("该仓库不存在");
            			re.setStatus(Params.status_failed);
            			return re;
					}
            		String [] proIdsList=proIds.split(",");
            		long i=0,j=0;
            		for (String productId : proIdsList) {
            			Product product=dao.fetch(Product.class,Cnd.where("id", "=", productId));
            			if(dao.count(Stock.class,Cnd.where("productId", "=", productId).and("stockplaceId","=",stockplaceId))!=0||product==null){
            				j++;
            				continue;
            			}
            			Stock stock = new Stock();
            			stock.setAuthor(user.getUsername());
            			stock.setAuthorId(user.getId());
            			stock.setCompanyNo(user.getCompanyNo());
            			stock.setRemark(product.getRemark());
            			stock.setDateCreater(date);
            			stock.setModel(product.getModel());
            			stock.setName(product.getName());
            			stock.setProNo(product.getProNo());
            			stock.setOptUser(user.getUsername());
            			stock.setOptUserId(user.getId());
            			stock.setProductId(product.getId());
            			stock.setStockplaceId(stockplaceId);
            			stock.setStockplace(stockplace);
            			stock.setUnit(product.getUnit());
            			dao.fastInsert(stock);
            			i++;
					}
                    re.setInfo("一共"+proIdsList.length+"品名，成功导入"+i+"条，失败"+j+"条。");
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
	 * 批量新增品名到仓库
	 * @param name 名称s
	 * @param model 型号s
	 * @param unit 单位s
	 * @param remark 备注s
	 * @param session
	 * @return
	 */
	@At("pro/fastAddProToStock")
	@Ok("json")
	public Result fastAddProToStock(String name,String model,String unit,String remark,String proNo,long stockplaceId,@Param("::acu.")AppHttpUtils acu,HttpSession session){
		Result re=new Result();
        if(MyUtils.checkRequestOk(acu, session)){ 
            User user=service.getUserInfo(acu, session);
            if(user==null){
                re.setInfo(Params.loginOutInfoStr);
                re.setStatus(Params.status_failed);
            }
      		String stockplace= StockModule.getStockNameById(stockplaceId,dao);
    		if (stockplace.equals("该仓库不存在")) {
    			re.setInfo("该仓库不存在");
    			re.setStatus(Params.status_failed);
    			return re;
			}
            else{
            	if(AppService.checkUserAuth(user.getId(),"st_addProInto",dao)){
            		Date date=new Date();
        			String[] nameList=name.split(",");
        			String[] modelList=model.split(",");
        			String[] unitList=unit.split(",");
        			String[] remarkList=remark.split(",");
        			String[] proNoList=proNo.split(",");
        			int length=nameList.length,k=0;
        			if(modelList.length!=length || unitList.length!=length||length==0){
        				re.setInfo("注意名称、编号、型号、单位必填");
        				re.setStatus(Params.status_failed);
        			}
        			else{
        				for(int i=0;i<length;i++){
        					Product product = new Product();
        					String _name="",_model="",_unit="",_remark="",_proNo="";
        					try {
        						_name=nameList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_model=modelList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_unit=unitList[i];
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_remark=remarkList[i];		
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					try {
        						_proNo=proNoList[i];		
        					} catch (Exception e) {
        						// TODO: handle exception
        					}
        					if(!MyUtils.checkProHad(_proNo,user.getCompanyNo(), dao)){
            					product.setAuthor(user.getUsername());
            					product.setAuthorId(user.getId());
            					product.setDateCreater(date);
            					product.setLastModifier(user.getUsername());
            					product.setModel(_model);
            					product.setName(_name);
            					product.setProNo(_proNo);
            					product.setRemark(_remark);
            					product.setUnit(_unit);
            					product.setCompanyNo(user.getCompanyNo());
            					dao.fastInsert(product);
        					}
        					long proId = dao.fetch(Product.class,Cnd.where("proNo","=", _proNo)).getId();
        					if(dao.count(Stock.class,Cnd.where("productId", "=", proId).and("stockplaceId","=",stockplaceId))!=0){
        						k++;
        						continue;
        					}
        					Stock stock =new Stock();
                			stock.setAuthor(user.getUsername());
                			stock.setAuthorId(user.getId());
                			stock.setCompanyNo(user.getCompanyNo());
                			stock.setRemark(_remark);
                			stock.setDateCreater(date);
                			stock.setModel(_model);
                			stock.setName(_name);
                			stock.setProNo(_proNo);
                			stock.setOptUser(user.getUsername());
                			stock.setOptUserId(user.getId());
                			stock.setProductId(proId);
                			stock.setStockplaceId(stockplaceId);
                			stock.setStockplace(stockplace);
                			stock.setUnit(_unit);
                			dao.fastInsert(stock);
        				}
        				re.setInfo("已成功添加"+length+"条品名到仓库,其中"+k+"条仓库里已存在");
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
}
