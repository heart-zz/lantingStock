<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/default/om-default_min.css">
<link rel="stylesheet" type="text/css" href="css/style.css?v=20180727001">
<link rel="shortcut icon" href="images/home.ico" />
<script type="text/javascript" src="js/jquery.min.js?v=20180727"></script>
<script type="text/javascript" src="js/operamasks-ui-basic.js"></script>
<script type="text/javascript" src="js/la_company.js"></script>
<script type="text/javascript" src="js/la_apply.js"></script>
<script type="text/javascript" src="js/la_picking.js"></script>
<script type="text/javascript" src="js/la_public.js?v=201805307"></script>
<script type="text/javascript" src="js/la_purchase.js?v=201807277"></script>
<script type="text/javascript" src="js/la_stock.js?v=2018072707"></script>
<script type="text/javascript">
</script>
<title>云库存管理系统</title>
    <script type="text/javascript">
        $(document).ready(function() {
        	pageFrameInit();
        });
    </script>
</head>
<body>
	<div id="myAuthList"></div>
	<div id="north-panel">
		<div id="messageTip"></div>
		<a href="index"><img alt="系统logo" src="images/logo_green2.png" title="刷新首页"></a>
		<div class="menu">
			<div class="block"><a href="#" title="账号信息" onclick="getMyInfo()"><%=session.getAttribute("username") %></a></div>
			<div class="block"><a href="#" title="修改密码保证账号安全" onclick="resetPassword();">密码修改</a></div>
			<div id='myauthlist' class="block"><a href="#" onclick="getMyAuthList();" >账号权限</a></div>
			<div class="block"><a href="exit" title="安全的离开系统">安全退出</a></div>
		</div>
	</div>
   	<div id="center-panel" style="padding: 5px 10px 0px 10px;">
    	<div id="center-tab">
	        <ul>
	            <li><a href="#tab1">办公桌面</a></li>
	        </ul>
	         <div id="tab1" style="padding: 0;position: relative;">
	         	<br />
	         	<div id="indextips">
	         		1、没有采购单，无法申请入库。<br/>
	         		2、必须从库存中选择要出库的内容，生成出库单。<br/>
	         		3、采购数据、库存数据出入库数据均支持检索、excel导出和打印。
	         	</div>
	         	<br />
	         	<div id="normalWork">
	         		<a href="#" onclick="addPur();" title="新增采购单">采购单</a>
	         		<a href="#"  title="查看我的入库申请">入库单</a>
	         		<a href="#" onclick="stockManager();" title="创建出库(领料)单">出库领料单</a>	
	         		<a href="#" onclick="stockInManager();" title="">退料单</a>	         		
	         		<a href="#"  title="采购流程示意">业务流程图</a>	         		
	         		
	         	</div>
	         	<br/>
		   		<div id="todayWork" style="color:red">
		   			${todayWorks}
		   		</div>
		   		<br />
		   		<div id="news">
		   			<c:forEach items="${announce}" var="item" varStatus="i">
		   				<div><${i.count}><font color="blue">${item.author}</font>于<fmt:formatDate value="${item.date}" pattern="yyyy-MM-dd" />:${item.content}</div>
		   			</c:forEach>
		   		</div>
		   		<br />
	   		</div>
		</div>
   	</div>
   	<div id="west-panel" class="om-accordion" style="position: relative;">
   		<div id="dialog"></div>
   		
   		<div id="nav-panel-2" class="nav-panel">
 			<div id='com_getEmployee' class="nav-item" onclick="getComUserList()">员工管理</div>
 			<div class="nav-item" onclick="getStockList()">仓库管理</div>
 			<div id="com_getRoleList" class="nav-item" onclick="getComRolesList()">角色管理</div>
 			<div id='pro_getList' class="nav-item" onclick="getProList()">品名管理</div>			 	
   		</div>
   		  
   		<div id="nav-panel-3" class="nav-panel">
   		   	<div id="aly_getProList" class="nav-item" onclick="addPur()">申请采购</div>
   			<div id='aly_getMyPurList' class="nav-item" onclick="getpurList(0)">我的采购单</div>	 	
	 		<div id='pur_getApplyList' class="nav-item" onclick="getpurList(1)">采购审核</div>	
	 		<div id='pur_PurTaskList' class="nav-item" onclick="getPurTask()">待采购清单</div>
	 		<div id='pur_PurTaskList' class="nav-item" onclick="getMyPurTask()">已采购清单</div>
	   	</div>
	   			
   		<div id="nav-panel-5" class="nav-panel">
   			<div class="nav-item" onclick="sendApplyOut()">申请领料</div>
   			<div class="nav-item" onclick="addPickReturn()">申请退料</div>
   			<div class="nav-item" onclick="getPickList(0)">我的领料单</div>	
   			<div class="nav-item" onclick="getPickList(1);">领料审核</div>	 			
   		</div>
   		  	
   		<div id="nav-panel-4" class="nav-panel">
   			<div class="nav-item" onclick="stockManager();">库存列表</div>
   			<div class="nav-item" onclick="stockInManager();">入库管理</div>
   			<div class="nav-item" onclick="stockOutManager();">出库管理</div>
   			<div class="nav-item" onclick="pickingReturnManager();">退料管理</div>
 			<div class="nav-item" onclick="StockTaking(0);">我的盘库单</div>
 			<div class="nav-item" onclick="StockTaking(1);">审核盘库单</div>			
 			<div class="nav-item" onclick="stockFlowManager()">库存动态</div> 		 			
   		</div>
   
   		<div id="nav-panel-0" class="nav-panel">
 			<div id='sys_addCom' class="nav-item" onclick="companyManager(0);">添加公司</div>
 			<div id= 'sys_getComList' class="nav-item" onclick="getCompanyList();">公司列表</div>
<!--  			<div class="nav-item" onclick="">权限管理</div>
 			<div class="nav-item" onclick="">数据管理</div>
 			<div class="nav-item" onclick="">日志管理</div> 	 -->		 	
   		</div>		
   	</div>
</body>
</html>