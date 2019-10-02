/**
 *公司模块
 */

/**
 * 新增/修改/查看 公司
 * @param com 采购信息，修改或查看是 才传入
 * @param type 0=新增,2=修改
 */
function companyManager(com,type,gridId){
//	com=com || {id:0,appNo:'P201807270015',purAppSp:'',purAppUser:'',purAppDate:''};
	type=type || 0;
	var _title='',_buttons=[],i=0;
	if(type==0){
		var com={};
		com.no=ajax_list("com/getCompanyNo");
		_title='添加公司';
		_buttons=[
			{text:'提交',click:function(){		
				var re=ajax_form('com/addCompany',formId);
				closeDialog('dialog');
				if(re.status==1){
					alert(re.info);
					reloadGrid(gridId);
				   }
			}}
		];
	}
	else if(type==1){
		_title='修改公司信息';
		_buttons=[
	    	{text:'提交',click:function(){				
		    	var re=ajax_form('com/updateCompany',formId);
		    	closeDialog('dialog');
		    	if(re.status==1){
		    		alert(re.info);
					reloadGrid(gridId);
		    	}
		    }}
	    ];
	}
	var labels=[{name:'公司名称'},{name:'公司编号'},{name:'公司电话'},{name:'公司地址'},
				{name:'公司负责人'},{name:'负责人电话'},{name:'负责人工号'},{name:'公司简介'}],
	inputs=[	
			{id:'com_name',name:'name',value:com.name},
			{id:'com_no',name:'no',value:com.no},
			{id:'com_tel',name:'tel',value:com.tel},
	        {id:'com_address',name:'address',value:com.address},
	        {id:'com_admin',name:'admin',value:com.admin},
	        {id:'com_admintel',name:'adminTel',value:com.adminTel},
	        {id:'com_userID',name:'userID',value:com.userID},
	        {id:'com_contentcontent',name:'content',value:com.content}
	       ],
	dialog={
			title:_title,
			width:450,
			height:500,
			modal:true,
			buttons:_buttons
	},
	formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
	$('#com_no').attr('readonly','readonly');
	if(type==2 || type==0){
		 $('input[name="purNames"]').omSuggestion({
             dataSource : 'stm/soStockQuick?type=1',
             clientFormatter:function(data,index){
                 return data.name;
             },
             onSelect:function(rowData,text,index,event){
            	 
             }
         });

	}
}
function getCompanyList(){
	var _title='公司列表';
	var tabId=addTab('center-tab', "公司列表", "tab_companyList"),
	btnsbar=[									
		{id:'com_add',label:'添加公司'},
		{id:'com_del',label:'删除公司'},
		{id:'com_stop',label:'停止服务'},
		{id:'com_run',label:'开启服务'},
		{id:'com_reset',label:'重置密码'},
		{id:'com_export',label:'导出公司列表'},
     ],	
     _header=['公司名称','公司编号','公司电话','公司负责人','负责人电话','负责人工号','操作','服务状态','公司地址','公司简介'],
	 _name=['name','no','tel','admin','adminTel','userID','opt','comstatus','address','content'],
	 _width=[200,150,120,95,120,100,120,90,250,300],
	 _renderer=[0,0,0,0,0,0,function(v,rowData,rowIndex){
		 return '<font style="font-weight:bold;font-style:italic;">双击修改</font>';
	 },function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
		 	  if(v==0)
        	 return '<font color="blue">服务维护中</font>';
         else if(v==1)
        	 return '<font color="green">服务运行中</font>';
         else if(v==-1)
        	 return '<font color="red">服务暂停中</font>';
         else
        	 return '<font color="red">未知状态</font>';
     },0,0],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 _dataSource='com/getCompanyList',
	 grid={
		title:_title,
		colModel:_colModel,
		dataSource:_dataSource,
		height:PANEL_HEIGHT*0.9,
		singleSelect:false,
		onRowDblClick:function(rowIndex,rowData,event){//双击查看采购单明细
			companyManager(rowData,1,gridId);
		}
		},
		gridId=createTabContent(tabId, btnsbar, grid);
	
		$('#com_add').click(function(){//添加公司
			companyManager(null,0,gridId);
		});
		
		$('#com_del').click(function(){//删除公司
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			if(list[0].comstatus>0){
				alert('运行中,无法删除');
				return;
			}
			if(confirm('确实删除?')){
				var ids='',_data;
				for(var i=0;i<list.length;i++){
					ids+=list[i].id+',';	
					}
	    	    _data={companyIds:ids,opt:-1},
	    	    re=ajax_json('com/companyOpt',_data);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
			}
		});
		
		$('#com_reset').click(function(){//重置管理员密码
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}else{
				var ids='';
				for(var i=0;i<list.length;i++){
					ids+=list[i].adminId+',';	
					}
	    	    _data={userIds:ids,opt:1},
	    	    re=ajax_json('com/resetOrDel',_data);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
				}
		});
		
		$('#com_stop').click(function(){//停止服务
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			if(confirm('确实停止服务?')){
				var ids='';
				for(var i=0;i<list.length;i++){
					ids+=list[i].id+',';	
					}
	    	    _data={companyIds:ids,opt:0},
	    	    re=ajax_json('com/companyOpt',_data);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
			}
		});
		$('#com_run').click(function(){//开启服务
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			if(confirm('确实开启服务?')){
				var ids='';
				for(var i=0;i<list.length;i++){
					ids+=list[i].id+',';	
					}
	    	    _data={companyIds:ids,opt:1},
	    	    re=ajax_json('com/companyOpt',_data);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
			}
		});

}

/**
 * 为用户增加/修改权限
 * @param user
 */
function userAuthManager(user){
	user=user || {id:0};
	if(user.id==0){
		alert('没有选择用户');
		return;
	}	
	var dialogHeight=PANEL_HEIGHT;
	$('#dialog').html('');
	$('#dialog').omDialog({
		title:'用户['+user.username+',角色='+(user.roleName||'未定义')+']权限管理',
		width:1290,
		height:dialogHeight
	});	
	
	$('#dialog').append('<div id="uam_bar"></div><div style="width:33%;float:left"><div id="uam_grid1"></div></div><div style="width:33%;float:left"><div id="uam_grid3"></div></div><div style="width:34%;float:left"><div id="uam_grid2" class="width50"></div></div><h3>注:记得进入仓库管理,将用户分配到相应仓库</h3>');
	$('#uam_bar').omButtonbar({
		width:'100%',
		btns:[
		      	{id:'uam_add',label:'添加权限'},
				{separtor:true},
				{id:'uam_del',label:'删除权限'}
			 ]
		
	});
	
	var _header=['权限名称','代码'],_name=['auth','authCode'],_width=[180,150],_colModel=createModel(_header, _name, _width);
	var fatherCode = "";
	$('#uam_grid1').omGrid({
		title:'父权限列表',		
		colModel:_colModel,	
		height:dialogHeight*0.8,
		singleSelect:true,
		limit:100000,
		dataSource:'auth/getUserFatherAuth',
		onRowClick:function(rowIndex,rowData,event){
			 fatherCode = rowData.authCode
			 var uam_grid3_url = "auth/getUserSonAuthNotSelected?userId="+user.id+"&fatherCode="+fatherCode;
			 var uam_grid2_url = "auth/getUserSonAuthSelected?userId="+user.id+"&fatherCode="+fatherCode;
			 $('#uam_grid3').omGrid('setData',uam_grid3_url);
			 $('#uam_grid2').omGrid('setData',uam_grid2_url);
		}
	});	
	$('#uam_grid3').omGrid({
		title:'用户未选子权限',		
		colModel:_colModel,		
		height:dialogHeight*0.8,
		singleSelect:false,
		limit:100000,
		dataSource:"auth/getUserSonAuthNotSelected?userId="+user.id+"&fatherCode="+fatherCode
	});
	$('#uam_grid2').omGrid({
		title:'用户已选子权限',		
		colModel:_colModel,		
		height:dialogHeight*0.8,
		singleSelect:false,
		limit:100000,
		dataSource:"auth/getUserSonAuthSelected?userId="+user.id+"&fatherCode="+fatherCode
	});
	
	
	/**
	 * 为用户添加权限
	 */
	$('#uam_add').click(function(){
		var selects=getGridSelections('uam_grid3');
		if(isEmpty(selects)){
			alert('请选择要添加的权限信息');
			return;
		}
		var ids='';
		for(var i=0;i<selects.length;i++){
			ids+=selects[i].authCode+",";
		}
		var re=ajax_list('auth/addUserAuth?userId='+user.id+'&authCodes='+ids);
		alert(re.info);
		if(re.status==1){
			reloadGrid('uam_grid2');
			reloadGrid('uam_grid3');
		}
	});
	
	$('#uam_del').click(function(){//删除权限
		var selects=getGridSelections('uam_grid2');
		if(isEmpty(selects)){
			alert('请选择需要删除的权限');
			return;
		}
		if(confirm('确定删除?')){
			var authCodes='';
			for(var i=0;i<selects.length;i++){
				authCodes+=selects[i].authCode+",";
			}
			var re=ajax_list('auth/delUserAuth?authCodes='+authCodes+'&userId='+user.id);
			alert(re.info);
			if(re.status==1){
				reloadGrid('uam_grid2');
				reloadGrid('uam_grid3');
			}
		}		
	});
}
/**
 * 获得公司员工列表
 */
function getComUserList(){
	var _title='员工列表';
	var tabId=addTab('center-tab', "员工列表", "tab_ComUserList"),
	btnsbar=[									
		{id:'com_addUser',label:'添加员工'},
		{id:'com_delUser',label:'删除员工'},
		{id:'com_resetPwd',label:'重置密码'},
		{id:'com_soUser',label:'检索员工'},
		{id:'com_addMore',label:'批量新增员工'},
		{id:'com_export',label:'导出员工列表'},
     ],	
     _header=['姓名','工号','联系方式','所属角色','操作','创建日期','创建者'],
	 _name=['username','userID','phone','roleName','opt','dateCreater','author'],
	 _width=[150,150,150,120,120,200,150],
	 _renderer=[0,0,0,0,function(v,rowData,rowIndex){
		 return '<font style="font-weight:bold;font-style:italic;">双击修改权限</font>';
	 },0,0],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 _dataSource='com/getComUserList',
	 grid={
		title:_title,
		colModel:_colModel,
		dataSource:_dataSource,
		height:PANEL_HEIGHT*0.9,
		singleSelect:false,
		onRowDblClick:function(rowIndex,rowData,event){//双击修改权限
			userAuthManager(rowData);
			}
		},
		gridId=createTabContent(tabId, btnsbar, grid);
	
		$('#com_addUser').click(function(){//添加员工
			var labels=[{name:'姓名'},{name:'工号'},{name:'角色'},{name:'联系方式'}],
				inputs=[	
					{id:'com_name',name:'username'},
					{id:'com_ID',name:'userID'},
					{id:'com_roleName',name:'roleName'},
					{id:'com_phone',name:'phone'},
					],
				_title='添加员工',
				_buttons=[	    
					{text:'提交',click:function(){
						var re=ajax_form('com/addComUser',formId);
						alert(re.info);
						if(re.status==1){
							closeDialog('dialog');
							reloadGrid(gridId)
						}
					}}
				],
				dialog={
						title:_title,
						width:460,
						height:300,
						modal:true,
						buttons:_buttons
					},
				formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
				$('#'+formId).append('<input id="com_roleId" type="hidden" name="roleId" />');
				var res = ajax_list('com/getRoleListCombo');
				$('#com_roleName').omCombo({
				dataSource:res.list,
				width:'180px',
				editable : false,
				onValueChange:function(target,newValue,oldValue,event){ 					    
					$('#com_roleName').val(target.val());//获取当前所选标签值
					$('#com_roleId').val(newValue);
				}
			});
		});
		
		$('#com_delUser').click(function(){//删除员工
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			if(confirm('确实删除?')){
				var ids='';
				for(var i=0;i<list.length;i++){
					ids+=list[i].id+',';	
					}
	    	    _data={userIds:ids,opt:-1},
	    	    re=ajax_json('com/resetOrDel',_data);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
			}
		});
		
		$('#com_resetPwd').click(function(){//重置密码
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}else{
				var ids='';
				for(var i=0;i<list.length;i++){
					ids+=list[i].id+',';	
					}
	    	    _data={userIds:ids,opt:1},
	    	    re=ajax_json('com/resetOrDel',_data);
	    	    alert(re.info);
				}
		});
		
		$('#com_soUser').click(function(){//检索员工
			$('#dialog').html('姓名:<input id="com_soUserName" style="width:300px;" /><br/>工号:<input id="com_soUserID" />');
			$('#dialog').omDialog({
				title:'模糊检索员工',
				modal:true,
				width:450,
				height:400,
				buttons:[{text:'搜索',click:function(){
					var nameKey=$('#com_soUserName').val(),IDKey=$('#com_soUserID').val();
					$('#'+gridId).omGrid({
						title:'检索结果',
						singleSelect:false,
						dataSource:'com/soComUserList?nameKey='+nameKey+'&IDKey='+IDKey,
					});
					$('#dialog').omDialog('close');
				}}]
			});
		});
}

/**
 * 角色权限信息管理
 * @param role 角色信息
 */
function roleAuthManager(role){
	role=role || {id:0};
	if(role.id==0){
		alert('没有选择角色');
		return;
	}	
	var dialogHeight=PANEL_HEIGHT;
	$('#dialog').html('');
	
	$('#dialog').omDialog({
		title:'角色['+role.roleName+']权限管理',
		width:1290,
		height:dialogHeight
	});	
	
//	$('#dialog').append('<div id="ram_bar"></div><div id="ram_grid1"></div><div id="ram_grid2"></div><h3>注:添加一个权限，系统会自动添加其所有子权限。系统不会重复添加。<br/>删除权限时，其对应的子权限也会自动删除</h3>');
	$('#dialog').append('<div id="ram_bar"></div><div style="width:33%;float:left"><div id="ram_grid1"></div></div><div style="width:33%;float:left"><div id="ram_grid3"></div></div><div style="width:34%;float:left"><div id="ram_grid2" class="width50"></div></div><h3>注:删除权限时，其对应的用户权限也会自动删除</h3>');
	$('#ram_bar').omButtonbar({
		width:'100%',
		btns:[
		      	{id:'ram_add',label:'添加权限'},
				{separtor:true},
				{id:'ram_del',label:'删除权限'}
			 ]
		
	});
	
	var _header=['权限名称','代码'],_name=['auth','authCode'],_width=[180,150],_colModel=createModel(_header, _name, _width);
	var fatherCode = "";
	$('#ram_grid1').omGrid({
		title:'父权限',		
		colModel:_colModel,	
		height:dialogHeight*0.8,
		singleSelect:true,
		limit:100000,
		dataSource:'auth/getUserFatherAuth',
		onRowClick:function(rowIndex,rowData,event){
			 fatherCode = rowData.authCode
			 var ram_grid3_url = "auth/getRoleSonAuthNotSelected?roleId="+role.id+"&fatherCode="+fatherCode;
			 var ram_grid2_url = "auth/getRoleSonAuthList?roleId="+role.id+"&fatherCode="+fatherCode;
			 $('#ram_grid3').omGrid('setData',ram_grid3_url);
			 $('#ram_grid2').omGrid('setData',ram_grid2_url);
		}
	});	
	$('#ram_grid3').omGrid({
		title:'角色未选子权限',		
		colModel:_colModel,		
		height:dialogHeight*0.8,
		singleSelect:false,
		limit:100000,
		dataSource:'auth/getRoleSonAuthNotSelected?roleId='+role.id+"&fatherCode="+fatherCode
	});
	$('#ram_grid2').omGrid({
		title:'角色已选子权限',		
		colModel:_colModel,		
		height:dialogHeight*0.8,
		singleSelect:false,
		limit:100000,
		dataSource:'auth/getRoleSonAuthList?roleId='+role.id+"&fatherCode="+fatherCode
	});
	
	/**
	 * 为角色添加权限
	 */
	$('#ram_add').click(function(){
		var selects=getGridSelections('ram_grid3');
		if(isEmpty(selects)){
			alert('请选择要添加的权限信息');
			return;
		}
		var ids='';
		for(var i=0;i<selects.length;i++){
			ids+=selects[i].authCode+",";
		}
		var re=ajax_list('auth/addRoleAuth?roleId='+role.id+'&authCodes='+ids);
		alert(re.info);
		if(re.status==1){
			reloadGrid('ram_grid2');
			reloadGrid('ram_grid3');
		}
	});
	
	$('#ram_del').click(function(){//删除权限
		var selects=getGridSelections('ram_grid2');
		if(isEmpty(selects)){
			alert('请选择需要删除的权限');
			return;
		}
		if(confirm('确定删除?')){
			var authIds='';
			for(var i=0;i<selects.length;i++){
				authIds+=selects[i].authCode+",";
			}
			var re=ajax_list('auth/delRoleAuth?roleAuthCodes='+authIds+'&roleId='+role.id);
			alert(re.info);
			if(re.status==1){
				reloadGrid('ram_grid2');
				reloadGrid('ram_grid3');
			}
		}		
	});
}

/**
 * 获得公司角色列表
 */
function getComRolesList(){
	var _title='角色列表';
	var tabId=addTab('center-tab', "角色列表", "tab_ComRolesList"),
	btnsbar=[									
		{id:'com_addRole',label:'新增角色'},
		{id:'com_delRole',label:'删除角色'},
     ],	
     _header=['角色名称','备注','操作','创建日期','创建者'],
	 _name=['roleName','remark','opt','dateCreater','author'],
	 _width=[150,250,120,200,150],
	 _renderer=[0,0,function(v,rowData,rowIndex){
		 return '<font style="font-weight:bold;font-style:italic;">双击修改权限</font>';
	 },0,0],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 _dataSource='com/getComRolesList',
	 grid={
		title:_title,
		colModel:_colModel,
		dataSource:_dataSource,
		height:PANEL_HEIGHT*0.9,
		singleSelect:false,
		onRowDblClick:function(rowIndex,rowData,event){//双击修改权限
			roleAuthManager(rowData);
			}
		},
		gridId=createTabContent(tabId, btnsbar, grid);
	
	
		$('#com_addRole').click(function(){//新增角色
			var labels=[{name:'角色名称'},{name:'备注'}],
				inputs=[	
					{id:'com_roleName',name:'roleName'},
					{id:'com_remark',name:'remark'}
					],
				_title='新增角色',
				_buttons=[	    
					{text:'提交',click:function(){
						var re=ajax_form('auth/addRole',formId);
						alert(re.info);
						if(re.status==1){
							closeDialog('dialog');
							reloadGrid(gridId)
						}
					}}
				],
				dialog={
						title:_title,
						width:460,
						height:300,
						modal:true,
						buttons:_buttons
					},
				formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
		});
		
		$('#com_delRole').click(function(){//删除角色
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			if(confirm('确实删除?')){
				var roleIds='';
				for(var i=0;i<list.length;i++){
					roleIds+=list[i].id+',';	
					}
	    	    re=ajax_list('auth/delRole?roleIds='+roleIds);
	    	    alert(re.info);
	    	    reloadGrid(gridId);
			}
		});

}

/**
 * 仓库管理
 */
function getStockList(){	
	var dialogHeight=PANEL_HEIGHT;
	var tabId=addTab('center-tab', "仓库列表", "tab_StockList");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="stockTab"></div><div style="width:33%;float:left"><div id="stockGrid1"></div></div><div style="width:33%;float:left"><div id="stockGrid3"></div></div><div style="width:34%;float:left"><div id="stockGrid2"></div></div>');
	$('#stockTab').omButtonbar({
		width:'100%',
		btns:[
				{id:'com_addStock',label:'新增仓库'},
				{id:'com_delStock',label:'删除仓库'},
				{separtor:true},
				{id:'com_addStUser',label:'添加员工'},
				{id:'com_delStUser',label:'删除员工'}
			 ]
		
	});
	var _header1=['仓库名称','备注','创建者'],_name1=['stockplace','remark','author'],_width1=[150,232,100],
		_colModel1=createModel(_header1, _name1, _width1),
		_header2=['员工名称','联系方式','工号'],_name2=['username','phone','userID'],_width2=[120,200,100],
		_colModel2=createModel(_header2, _name2, _width2);
	var stockplaceId = "",stockplace='';
	$('#stockGrid1').omGrid({
		title:'仓库列表',		
		colModel:_colModel1,	
		height:dialogHeight*0.9,
		singleSelect:true,
		limit:100000,
		dataSource:'com/getStockList',
		onRowClick:function(rowIndex,rowData,event){
			stockplaceId = rowData.id
			stockplace = rowData.stockplace
			 var stockGrid3_url = "com/getLeaderBySt?stockplaceId="+stockplaceId;
			 var stockGrid2_url = "com/getLeaderNotInSt?stockplaceId="+stockplaceId;
			 $('#stockGrid3').omGrid('setData',stockGrid3_url);
			 $('#stockGrid2').omGrid('setData',stockGrid2_url);
		}
	});	
	$('#stockGrid3').omGrid({
		title:'此仓库已有员工',		
		colModel:_colModel2,		
		height:dialogHeight*0.9,
		singleSelect:false,
		limit:100000,
//		dataSource:"com/getLeaderBySt?stockplaceId="+stockplaceId
	});
	$('#stockGrid2').omGrid({
		title:'此仓库未有员工',		
		colModel:_colModel2,		
		height:dialogHeight*0.9,
		singleSelect:false,
		limit:100000,
//		dataSource:"com/getLeaderNotInSt?stockplaceId="+stockplaceId
	});
	
	
	/**
	 * 为仓库添加员工
	 */
	$('#com_addStUser').click(function(){
		var selects=getGridSelections('stockGrid2');
		if(isEmpty(selects)){
			alert('请选择要添加的员工');
			return;
		}
		var ids='';
		for(var i=0;i<selects.length;i++){
			ids+=selects[i].id+",";
		}
		var data={stockplace:stockplace,stockplaceId:stockplaceId,userIds:ids,opt:1},
		re=ajax_json('com/addLeaders',data);
		alert(re.info);
		if(re.status==1){
			reloadGrid('stockGrid2');
			reloadGrid('stockGrid3');
		}
	});
	
	/**
	 * 为仓库删除员工
	 */
	$('#com_delStUser').click(function(){
		var selects=getGridSelections('stockGrid3');
		if(isEmpty(selects)){
			alert('请选择需要删除的员工');
			return;
		}
		if(confirm('确定删除?')){
			var ids='';
			for(var i=0;i<selects.length;i++){
				ids+=selects[i].id+",";
			}
			var data={stockplace:stockplace,stockplaceId:stockplaceId,userIds:ids,opt:-1},
			re=ajax_json('com/addLeaders',data);
			alert(re.info);
			if(re.status==1){
				reloadGrid('stockGrid2');
				reloadGrid('stockGrid3');
			}
		}		
	});
	
	/**
	 * 添加仓库
	 */
	$('#com_addStock').click(function(){
		var labels=[{name:'仓库名称'},{name:'仓库地址'},{name:'备注'}],
		inputs=[	
			{id:'stockName',name:'stockplace'},
			{id:'stockAddr',name:'exactAddr'},
			{id:'stock_remark',name:'remark'}
			],
		_title='新增仓库',
		_buttons=[	    
			{text:'提交',click:function(){
				var re=ajax_form('com/addStock',formId);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('stockGrid1');
				}
			}}
		],
		dialog={
				title:_title,
				width:460,
				height:300,
				modal:true,
				buttons:_buttons
			},
		formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
	});
	
	/**
	 * 删除仓库
	 */
	$('#com_delStock').click(function(){
		var selects=getGridSelections('stockGrid1');
		if(isEmpty(selects)){
			alert('请选择需要删除的仓库');
			return;
		}
		if(confirm('确定删除?')){
			var re=ajax_list('com/delStock?id='+selects[0].id);
			alert(re.info);
			if(re.status==1){
				closeDialog('dialog');
				reloadGrid('stockGrid1');
			}
		}		
	});
}
function getMyInfo(){
	var re = ajax_list("my/getMyInfo"),
	p=re.obj;
	var _title='个人信息',
		_buttons=[
	    	{text:'提交',click:function(){				
		    	var re=ajax_list('my/updateMyInfo?phone='+$('#p_tel').val()+'&id='+p.id);
		    	closeDialog('dialog');
		    	if(re.status==1){
		    		alert(re.info);
		    	}
		    }}
	    ],
	    labels=[
	    	{name:'姓名'},{name:'工号'},{name:'联系方式'},{name:'角色'},
	    	{name:'登录次数'},{name:'注册日期'}
			],
		inputs=[	
			{id:'p_name',name:'username',value:p.username},
			{id:'p_Id',name:'userID',value:p.userID},
			{id:'p_tel',name:'phone',value:p.phone},
	        {id:'p_role',name:'roleName',value:p.roleName},
	        {id:'p_loginCount',name:'loginCount',value:p.loginCount},
	        {id:'p_dateCreater',name:'dateCreater',value:p.dateCreater},
	       ],
	    dialog={
			title:_title,
			width:450,
			height:500,
			modal:true,
			buttons:_buttons
			},
	formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
	$('#p_name').attr('readonly','readonly');
	$('#p_Id').attr('readonly','readonly');
	$('#p_role').attr('readonly','readonly');
	$('#p_loginCount').attr('readonly','readonly');
	$('#p_dateCreater').attr('readonly','readonly');
}

/**
 * 修改密码
 */
function resetPassword(){
	var labels=[{name:'原密码',style:'fontRed'},{name:'新密码',style:'fontRed'},{name:'确认密码',style:'fontRed'}];
	var inputs=[{id:'rp_pass',type:'password'},{id:'rp_pass1',type:'password'},{id:'rp_pass2',type:'password'}];
	var dialog={
			title:'修改密码',
			width:450,
			height:260,
			buttons:[{text:'提交',click:function(){
				setPassBtn();
			}}]
	};
	createFormDialog('dialog', labels, inputs, dialog, 1);
	
	//设置回车事件
	document.onkeydown = function(e){ 
	    var ev = document.all ? window.event : e;
	    if(ev.keyCode==13) {
	         setPassBtn();	    	
	     }
	};
}

/**
 * 修改密码提交事件
 */
function setPassBtn(){
	var isNeed=['rp_pass','rp_pass1','rp_pass2'];
	if(isInputNeeded(isNeed)){
		var pass1=getElemValue('rp_pass1');
		var pass2=getElemValue('rp_pass2');
		if(pass1==pass2){
			var param=[{name:'prePass',value:encodeMD5(getElemValue('rp_pass'))},{name:'password',value:encodeMD5(pass1)}];
			var re=ajax_param('my/setPassWd', param);
			alert(re.info);
			if(re.status==status_success){
				closeDialog();
			}
			
		}
		else{
			alert('新密码与确认密码不一致');
		}
	}
	else{
		alert('信息不完整,请检查');
	}
}
/**
 * 系统权限管理
 * @param divId 目标div id
 */
function authManager(divId){
	divId=divId || 'center-tab';
	var tabId=addTab(divId, "权限管理", "tab_authManager"),
	btnsbar=[
	         {id:'tam_add',label:'新增权限'},
			 {id:'tam_del',label:'删除权限'}
	        ],
	_dataSource='auth/getAuthList',_title="顶级权限列表",_header=['权限名称','代码'],_name=['auth','authCode'],_width=[180,150],_colModel=createModel(_header, _name, _width),
	grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	
	//btnbar actions
	$('#tam_add').click(function(){//添加权限
		addAuth(gridId);
	});
	
	$('#tam_del').click(function(){//删除权限
		var selects=getGridSelections(gridId);
		if(isEmpty(selects)){
			alert('请选择权限');
			return;
		}
		if(confirm('确定删除?所选权限的子权限也会删除')){
			var authcodes='';
			for(var i=0;i<selects.length;i++){
				authcodes+=selects[i].authCode+',';
			}
			var re=ajax_list('auth/delAuth?authcodes='+authcodes);
			//alert(re.info);
			if(re.status==status_success){
				reloadGrid(gridId);
			}
		}
		
	});
}
/**
 * 新增权限
 * @param gridId 当前操作grid id
 * @param dialogId 
 */
function addAuth(gridId,dialogId){
	gridId=gridId ||'';
	if(isEmpty(gridId)){
		alert('参数出错');
		return;
	}
	var select=getGridSelections(gridId),param=[];	
	if(isEmpty(select)){
		if(confirm('你没有选择记录?直接添加顶级权限?')){
			param.push({name:'auth_father',value:"无"});
			param.push({name:'auth_fatherCode',value:'0'});
		}
		else{
			return;
		}
	}
	else{
		select=select[0];
		param.push({name:'auth_father',value:select.auth});
		param.push({name:'auth_fatherCode',value:select.fatherCode});
	}
	
	dialogId=dialogId ||'dialog';
	
	var formId='',labels=[{name:'权限名称'},{name:'权限代码'}],inputs=[{name:'addAuth_auth'},{name:'addAuth_authCode'}],
		dialog={
			title:'批量新增权限',
			width:650,
			height:400,
			buttons:[{text:'添加',click:function(){
				$('#'+formId+'_table').append('<tr><td><label class="leftlabel">权限名称:</label><input class="leftinput" name="addAuth_auth" /></td><td><label class="leftlabel">权限代码:</label><input class="leftinput" name="addAuth_authCode" /></td></tr>');
			}},{text:'提交',click:function(){
				var authname='',authCode='';
				$('input[name="addAuth_auth"]').each(function(){
					authname+=this.value+',';
				});
				$('input[name="addAuth_authCode"]').each(function(){
					authCode+=this.value+',';
				});
				param.push({name:'auths',value:authname});
				param.push({name:'authcodes',value:authCode});
				var re=ajax_param('auth/addAuth', param);
				alert(re.info);
				if(re.status==status_success){
					reloadGrid(gridId);
					closeDialog(dialogId);
				}
			}}]
	};
	formId=createFormDialog(dialogId, labels, inputs, dialog, 2, "新增权限");
	$('#'+dialogId).append('<h3>点击[添加]按钮，可以继续追加权限内容。<br/>点击[提交]按钮，会正式提交到服务器。<br/>父权限下同名子权限只添加一条。</h3>');
}