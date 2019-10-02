/**
 * 库存模块
 */

/**
 * 权限下库存管理
 * @param type 0=有效库存,1=所有库存,2=预警库存
 */
function stockManager(){	
	var _title='所有库存';
/*
	else if(type==1)
		_title='有效库存';
	else if(type==2)
		_title='预警库存';	*/

	var tabId=addTab('center-tab', "库存管理", "tab_stockManager");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="stockTab"></div><div id="stockProList"></div>');
	$('#stockTab').omButtonbar({
		width:'100%',
		btns:[
         	{id:'stm_splist',label:'仓库列表'},
         	{id:'stm_warnlist',label:'预警库存'},
         	{id:'stm_flow',label:'库存动态'},
         	{id:'stm_taking',label:'新建盘库单'},
         	{id:'stm_setWarnValue',label:'设置储备数量范围'},
			{id:'stm_so',label:'检索库存'},
			{id:'stm_export',label:'导出库存'},
			 ]
	});	 
	 var _header=['名称','编号','型号','库存数量','平均单价','单位','小计','所属仓库','最新单价','最近入库日期','储备数量范围','操作'],
	 	 _name=['name','proNo','model','number','priceIn','unit','sum','stockplace','priceLast','stockInLast','MinToMAx','opt'],
	 	 _width=[125,100,100,70,60,60,80,80,60,83,100,200],
	 	 _renderer=[0,0,0,0,0,0,0,0,0,0,function(v,rowData,rowIndex){   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	 		 if(rowData.numberMin==0&&rowData.numberMax==0)
	 		 return '<a style="color:#F00">未设置</a>';
	 		 else return '<a>'+rowData.numberMin+'~'+rowData.numberMax+'</a>';
	 	 },function(v,rowData,rowIndex){   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	 		 return '<font style="font-weight:bold;font-style:italic;">双击查看该物品动态</font>';
	 	 }],
	 	 _colModel=createModel(_header, _name, _width,_renderer),
	 	 _dataSource='stm/getStockList?type=0';
		$('#stockProList').omGrid({
			title:_title,		
			colModel:_colModel,	
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			limit:100000,
			dataSource:_dataSource,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看该物品动态
				proFlowInfo(rowData);
			}
			
		});
	var res = ajax_list("com/getMyStockListCombo"),
	stockList=res.list,spId=0,spName='';
	for(var i=0;i<stockList.length;i++){
		stockList[i].id = stockList[i].value;
		stockList[i].label = stockList[i].text;
	}
	var menu_stock_fn=function(item,event){
		$('#stm_splist').omButton({
			label:item.label
		});
		spId=item.id;
		spName=item.label;
		$('#stockProList').omGrid({
			title:item.label+"·所有库存",	
			dataSource:'stm/getStockList?type=1&stockplaceId='+spId,
		});
	};
	createMenu("menu_stm_splist","stm_splist",stockList,menu_stock_fn,tabId);
	
	$('#stm_flow').click(function(){//查看某些物品的出入库信息
		var list=getGridSelections('stockProList');
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		//库存动态
		var stockIds='';
		for(var i=0;i<list.length;i++){
			stockIds+=list[i].id+','
		}
		stockFlowManager(stockIds);
	});
	
	$('#stm_warnlist').click(function(){//预警库存
		if(spId==0){
			$('#stockProList').omGrid({
				title:"预警库存",	
				dataSource:'stm/getStockList?type=0&isWarn=1',
			});
		}else{
			$('#stockProList').omGrid({
				title:spName+"·预警库存",	
				dataSource:'stm/getStockList?type=1&isWarn=1&stockplaceId='+spId,
			});
		}
	});
	
	$('#stm_setWarnValue').click(function(){//设置物品的警戒值
		var list=getGridSelections('stockProList');
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		var _title='设置储备数量范围',_buttons=[],itemList=[];
		_buttons=[	         
			{text:'提交',click:function(){
				for(var i=0;i<list.length;i++){
					var row={};
					row.id=list[i].id;
					row.numberMin = parseInt($('#numMin_'+i+'').val());
					row.numberMax = parseInt($('#numMax_'+i+'').val());
					itemList.push(row);
				}
				var _data={itemList:itemList},
					re=ajax_json('stm/setMinAndMax',_data);
					alert(re.info);
					if(re.status==1){
						closeDialog('dialog');
						reloadGrid('stockProList');
					}
				}}
			];
		var prodialog={
				title:_title,
				width:795,
				height:600,
				modal:true,
				buttons:_buttons
			},
			labels=[{name:'名称'},{name:'编号'},{name:'型号'},{name:'最小储备数量'},{name:'最大储备数量'}],
			pickformId=createDialog('dialog',labels,prodialog);
			var content='';
			for(var i=0;i<list.length;i++){
				content+=('<tr><td><input name="name" class="width_120" value="'+list[i].name+'"/></td><td><input name="proNO" class="width_120" value="'+list[i].proNo+'"/></td><td><input name="model"  class="width_160" value="'+list[i].model+'"/></td><td><input id="numMin_'+i+'" name="numMin" class="width_60" value="'+list[i].numberMin+'"/></td><td><input id="numMax_'+i+'" name="numMax" class="width_60" value="'+list[i].numberMax+'"/></td></tr>');
			}	
		$('#'+pickformId+"_table").append(content);
		$('input[name="name').attr('readonly','readonly');
		$('input[name="proNO').attr('readonly','readonly');
		$('input[name="model').attr('readonly','readonly');
		$('input[name="numMin"]').omNumberField();
		$('input[name="numMax"]').omNumberField();
	});
	
	$('#stm_taking').click(function(){//盘库
		var list=getGridSelections('stockProList'),stockIds='';
		if(spId==0){
			alert('请先选择仓库');
			return;
		}
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		for(var i=0;i<list.length;i++){
			stockIds+=list[i].id+',';
		}
		 var re=ajax_list('skm/addStockTaking?stockIds='+stockIds);
		 alert(re.info);
	});
	
	$('#stm_so').click(function(){//模糊检索库存
		var labels=[{name:'名称'},{name:'编号'},{name:'型号'}],inputs=[{id:'stm_name'},{id:'stm_proNo'},{id:'stm_model'}],
		dialog={
				title:'模糊检索库存',
				modal:true,
				width:500,
				height:360,
				buttons:[{text:'检索',click:function(){
					
				}}]
		},
		formId=createFormDialog('dialog', labels, inputs, dialog, 1, '模糊检索');
	});
	
	$('#stm_export').click(function(){//导出
		var list=getGridSelections('stockProList');
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		waitingPage('请等待', '数据生成中...', 3000);
	});
}

/**
 * 库存动态管理
 * @param stock
 */
function stockFlowManager(stockIds){
	var _title='库存动态',
	tabId=addTab('center-tab', "库存动态管理", "tab_stockFlowManager");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="stockFlowTab"></div><div id="stockFlowList"></div>');
	$('#stockFlowTab').omButtonbar({
		width:'100%',
		btns:[
			{id:'stfm_export',label:'导出'},
			{id:'stfm_so',label:'检索'},
			 ]
	});
	var _header=['单号','名称','编号','型号','目标仓库','变动前数量','变动数量','变动后数量','单位','业务类型','出入','状态'],
	  	_name=['preAppNo','name','proNo','model','stockplace','stockNumber','number','stockedNumber','unit','sfType','flowType','status'],
	  	_width=[160,135,120,120,80,80,60,80,60,80,60,70],
	    _renderer=[0,0,0,0,0,0,0,0,0,0,
	    	function(v, rowData , rowIndex){
	    		if(v==1){
	    			return '<font color="blue">入库</font>';
	    		}else if(v==-1){
	    			return '<font color="red">出库</font>';
	    		}
	    	},
	    	function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	    		if(v==0){
	            	 if(rowData.flowType==1)
	            		 return '<font color="red">待入库</font>';
	            	 else
	            		 return '<font color="red">待出库</font>';
	             }else if(v==9){
	            	 if(rowData.flowType==1)
	            		 return '<font color="green">已入库</font>'
	            	 else
	            		 return '<font color="green">已出库</font>' 
	             }
	         }],
	    _colModel=createModel(_header, _name, _width,_renderer),
	   	_dataSource='stm/getStockDynamicList?type=0';
		if(stockIds!=null) _dataSource='stm/getStockDynamicList?type=0&stockIds='+stockIds;
		$('#stockFlowList').omGrid({
			title:_title,		
			colModel:_colModel,	
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			dataSource:_dataSource,
            //展开行时使用下面的方法生成详情，必须返回一个字符串
            rowDetailsProvider:function(rowData,rowIndex){
            	if(rowData.flowType==1)
                return '<p>申请人:'+rowData.appUser+'&emsp;申请日期:'+rowData.appDate+'&emsp;审核人:'+rowData.verifyUser+'&emsp;审核日期:'+rowData.verifyDate+'&emsp;采购人:'+rowData.optUser+'&emsp;采购日期:'+rowData.optDate+'&emsp;入库审核员:'+rowData.stockUser+'&emsp;入库日期:'+rowData.stockDate+'</p>';
            	else return '<p>申请人:'+rowData.appUser+'&emsp;申请日期:'+rowData.appDate+'&emsp;审核人:'+rowData.verifyUser+'&emsp;审核日期:'+rowData.verifyDate+'&emsp;出库审核员:'+rowData.stockUser+'&emsp;出库日期:'+rowData.stockDate+'</p>';
            }
			
		});
		
		var res = ajax_list("com/getMyStockListCombo"),
		stockList=res.list;
	$('#stfm_so').click(function(){//模糊检索库存动态
		var labels=[{name:'物品名称'},{name:'物品编号'},{name:'目标仓库'},{name:'业务类型'},{name:'开始日期'},{name:'结束日期'},{name:'申请人'}],
		inputs=[{id:'stfm_name'},{id:'stfm_proNo'},{id:'stfm_splace'},{id:'stfm_type'},{id:'stfm_startDate'},{id:'stfm_endDate'},{id:'stfm_appUser'}],
		dialog={
				title:'模糊检索库存动态',
				modal:true,
				width:500,
				height:450,
				buttons:[{text:'检索',click:function(){
					var name=$('#stfm_name').val(),proNo=$('#stfm_proNo').val(),
					splaceId=$('#stfm_splace').val(),type=$('#stfm_type').val(),
					startDate=$('#stfm_startDate').val(),endDate=$('#stfm_endDate').val(),appUser=$('#stfm_appUser').val();
					$('#stockFlowList').omGrid({
						title:"搜索结果列表",		
						dataSource:"stm/soStockDynamicList?name="+name+'&proNo='+proNo+'&splaceId='+splaceId+'&type='+type
						+'&startDate='+startDate+'&endDate='+endDate+'&appUser='+appUser,		
					});
					closeDialog('dialog');
				}}]
		},
		formId=createFormDialog('dialog', labels, inputs, dialog, 1, '模糊检索');
		$('#stfm_splace').omCombo({
			dataSource:stockList,
		});
		$('#stfm_type').omCombo({
			dataSource:[{text:'所有入库',value:'所有入库'},{text:'所有出库',value:'所有出库'},{text:'采购入库',value:'采购入库'},
			            {text:'退料入库',value:'退料入库'},{text:'盘库入库',value:'盘库入库'},
			            {text:'领料出库',value:'领料出库'},{text:'销售出库',value:'销售出库'},
			            {text:'盘库出库',value:'盘库出库'}]
		});
		$('#stfm_startDate').omCalendar();
		$('#stfm_endDate').omCalendar();
	});
}


/**
 * 查看单号明细
 * @param p 单号
 */
function stockFlowInfo(p){
	if(p.appNo==null||p.appNo=="undefined"){
		p.appNo=p.preAppNo;
		if(p.appNo==null||p.appNo=="undefined")p.appNo=p.preAppId;
	}
	if(p.dateUpdater==null||p.dateUpdater=="undefined"){
		p.dateUpdater=p.dateCreater;
	}
	$('#dialog').html('<div id="sfi_h1"></div><div id="sfi_h2"></div><div id="sfi_h3"></div>');
	$('#dialog').omDialog({
		title:'单号详情',
		modal:true,
		width:1200,
		height:PANEL_HEIGHT*0.95,
		buttons:[]
	});
	var content='';
	content='<h3>记录详情</h3><table class="myTable"><tr><td>单号:'+p.appNo+'</td><td>申请人:'+p.appUser+'</td><td>申请日期:'+p.dateUpdater+'</td><td>仓库:'+p.stockplace+'</td></tr></table>';
	$('#sfi_h1').html(content);
	content='<h3>业务单明细</h3><div id="sfi_grid"></div>';
	$('#sfi_h2').html(content);
	
	var _header=['名称','编号','型号','单价','数量','单位','总价','用途',"业务类型",'出入','状态','采购人'],
	  _name=['name','proNo','model','price','number','unit','sum','content',"sfType",'flowType','status','optUser'],
	  _width=[135,100,90,90,80,60,60,60,120,60,80,60,70],
	  _renderer=[0,0,0,0,0,0,0,0,0,function(v, rowData , rowIndex){
	    		if(v==1){
	    			return '<font color="green">入库</font>';
	    		}
	    		else if(v==-1){
	    			return '<font color="red">出库</font>';
	    		}else return '<font>无出入</font>';
	    	},function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	    		if(v==-2)return '<font color="red">待清点</font>';
    			else if(v==-1)return '<font color="red">未通过</font>';
    			else if(v==0)return '<font color="blue">待审核</font>';
    			else{
    	    		if(rowData.flowType==1){//入库单状态	
    		    		if(v==-9)return '<font color="yellow">入库不通过</font>';
    		    		else if(v<6)return  '<font color="blue">待采购</font>';
    		    		else if(v<9)return  '<font color="blue">待入库</font>';
    		    		else if(v==9)return '<font color="green">已入库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-1){//出库单状态
    	    			if(v==-9)return '<font color="yellow">出库不通过</font>';
    		    		else if(v<9)return  '<font color="blue">待出库</font>';
    		    		else if(v==9)return '<font color="green">已出库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-2){
    		    		 return '<font color="green">已通过</font>';
    	    		}
    			}
	         },0],
	   _colModel=createModel(_header, _name, _width,_renderer),
	   _dataSource='stm/getStockFlowList?preAppNo='+p.appNo,
	   _title='数据列表',
	    grid={
	    		title:_title,
	    		colModel:_colModel,
	    		dataSource:_dataSource,
	    		height:PANEL_HEIGHT*0.7,
	    		onRowClick : function(index,rowData,event){
					$('#grid').omGrid('editRow',index);
				}
	    };
	 $('#sfi_grid').omGrid(grid);
}

/**
 * 查看某个产品流动详情
 * @param 产品id
 */
function proFlowInfo(pro){
	$('#dialog').html('<div id="sfi_h1"></div>');
	$('#dialog').omDialog({
		title:"现名称:"+pro.name+",编号:"+pro.proNo,
		modal:true,
		width:1200,
		height:PANEL_HEIGHT*0.95,
		buttons:[]
	});
	var content='';
	content='<h3>该物品所有明细</h3><div id="sfi_grid"></div>';
	$('#sfi_h1').html(content);
	
	var _header=['名称','编号','型号','单价','数量','单位','总价','用途',"业务类型",'出入','状态'],
	  _name=['name','proNo','model','price','number','unit','sum','content',"sfType",'flowType','status'],
	  _width=[140,80,100,80,80,80,80,120,80,80,80],
	  _renderer=[0,0,0,0,0,0,0,0,0,function(v, rowData , rowIndex){
	    		if(v==1){
	    			return '<font color="green">入库</font>';
	    		}
	    		else if(v==-1){
	    			return '<font color="red">出库</font>';
	    		}else return '<font>无出入</font>';
	    	},function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	    		if(v==-2)return '<font color="red">待清点</font>';
    			else if(v==-1)return '<font color="red">未通过</font>';
    			else if(v==0)return '<font color="blue">待审核</font>';
    			else{
    	    		if(rowData.flowType==1){//入库单状态	
    		    		if(v==-9)return '<font color="yellow">入库不通过</font>';
    		    		else if(v<6)return  '<font color="blue">待采购</font>';
    		    		else if(v<9)return  '<font color="blue">待入库</font>';
    		    		else if(v==9)return '<font color="green">已入库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-1){//出库单状态
    	    			if(v==-9)return '<font color="yellow">出库不通过</font>';
    		    		else if(v<9)return  '<font color="blue">待出库</font>';
    		    		else if(v==9)return '<font color="green">已出库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-2){
    		    		 return '<font color="green">已通过</font>';
    	    		}
    			}
	         }],
	   _colModel=createModel(_header, _name, _width,_renderer),
	   _dataSource='stm/getProFlowList?stockId='+pro.id,
	   _title='数据列表',
	    grid={
	    		title:_title,
	    		colModel:_colModel,
	    		dataSource:_dataSource,
	    		height:PANEL_HEIGHT*0.7,
	    };
	 $('#sfi_grid').omGrid(grid);
}

/**
 * 入库管理
 * @param
 */
function stockInManager(){
	var _title='待入库记录';
	
	var tabId=addTab('center-tab', "入库管理", "tab_stockInManager"),
	btnsbar=[									
	         	{id:'stmi_waitList',label:'待入库记录'},
	         	{id:'stmi_pass',label:'允许入库'},
	         	{id:'stmi_fail',label:'拒绝入库'},
	         	{id:'stmi_alList',label:'已入库记录'},
	         	{id:'stmi_myVerifyList',label:'我入库的记录'},
				{id:'stmi_so',label:'检索记录'},
				{id:'stmi_export',label:'导出记录'},
	         ],		 
	  //btnsbar=createUnitShow(btnsbar),		 
	 _header=['(原)订单号','名称','型号','目标仓库','入库数量','单位','入库类型','状态','申请人','申请日期','入库审核人','入库日期','操作'],
	 _name=['preAppNo','name','model','stockplace','number','unit','sfType','status','appUser','appDate','stockUser','stockDate','opt'],
	 _width=[150,135,90,80,80,60,80,80,80,180,80,180,180],
	 _renderer=[
		 function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	         if(v==null||v==''){
	        	 return '<font>'+rowData.preAppId+'</font>';
	         }else{
	        	 return '<font>'+rowData.preAppNo+'</font>';
	         }
	     },0,0,0,0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
         if(v==6){
        	 return '<font color="red">待入库</font>';
         }
         else if(v==9){
        	 return '<font color="green">已入库</font>'
         }
     },0,0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
    	 return '<font style="font-weight:bold;font-style:italic;">双击订单详情</font>';
     }],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 _dataSource='stm/getStockInList?type=0',
	 grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看详情
				stockFlowInfo(rowData,'dialog');
			}
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	
	$('#stmi_alList').click(function(){
		$('#'+gridId).omGrid({
			title:'已入库记录',
			dataSource:'stm/getStockInList?type=1'
		});
		var currentBtn1 = document.getElementById("stmi_pass");
		currentBtn1.style.display = "none";
		var currentBtn2 = document.getElementById("stmi_fail");
		currentBtn2.style.display = "none";
	});
	
	$('#stmi_waitList').click(function(){
		$('#'+gridId).omGrid({
			title:'待入库记录',
			dataSource:'stm/getStockInList?type=0'
		});
//		btnsbar.splice(1,0,{id:'stmi_pass',label:'允许入库'},
//	         	{id:'stmi_fail',label:'拒绝入库'});
		var currentBtn1 = document.getElementById("stmi_pass");
		currentBtn1.style.display = "block";
		var currentBtn2 = document.getElementById("stmi_fail");
		currentBtn2.style.display = "block";
	});
	
	$('#stmi_pass').click(function(){
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		var ids=[],_data;
		for(var i=0;i<list.length;i++){
			ids[i]=list[i].id;	
			}
	    _data={ids:ids,opt:1},
	    re=ajax_json('stm/verifyIn',_data);
	    alert(re.info);
	    if(re.status){
	    	reloadGrid(gridId);
	    }
	});
	
	$('#stmi_fail').click(function(){
		var list=getGridSelections(gridId),
		_buttons=[
	    	{text:'提交',click:function(){
	    		_data={ids:ids,opt:-1,hint:"入库不通过原因："+$('#hint').val()};
	    		re=ajax_json('stm/verifyIn',_data);
	    	    alert(re.info);
	    	    if(re.status){
	    	    	closeDialog('dialog');
	    	    	reloadGrid(gridId);
	    	    }
		    }}
	    ];
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		$('#dialog').html('<textarea id="hint" name="content" rows="6" cols="40" onpropertychange="if(this.scrollHeight>40) this.style.posHeight=this.scrollHeight+5"></textarea>');
		$('#dialog').omDialog({
			title:'请输入不通过原因',
			modal:true,
			width:330,
			height:190,
			buttons:_buttons
		});
		var ids=[],_data;
		for(var i=0;i<list.length;i++){
			ids[i]=list[i].id;	
			}
	});
	
	$('#stmi_so').click(function(){//模糊检索
		var labels=[{name:'单号'},{name:'申请人'},{name:'名称'},{name:'类型'},{name:'状态'}],
			inputs=[{id:'stmi_so_k1'},{id:'stmi_so_k2'},{id:'stmi_so_k3'},{id:'stmi_so_k4'},{id:'stmi_so_k5'}],
			dialog={
				title:'模糊检索入库',
				modal:true,
				width:460,
				height:380,
				buttons:[{text:'检索',click:function(){
					
				}}]
		},
		formId=createFormDialog('dialog', labels, inputs, dialog, 1, '模糊检索');
		$('#stmi_so_k5').omCombo({
			dataSource:[{text:'未入库',value:0},{text:'已入库',value:9}]
		})
		
	});
	
	$('#stmi_export').click(function(){//导出
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		waitingPage('请等待', '数据生成中...', 3000);
	});
}

/**
 * 出库管理
 */
function stockOutManager(){
	var _title='待出库记录';
	var tabId=addTab('center-tab', "出库管理", "tab_stockOutManager"),
	btnsbar=[									
	         	{id:'stmo_waitList',label:'待出库记录'},
	         	{id:'stmo_alList',label:'已出库记录'},
	         	{id:'stmo_pass',label:'允许出库'},
	         	{id:'stmo_fail',label:'拒绝出库'},
	         	{id:'stmo_myVerifyList',label:'我出库的记录'},
				{id:'stmo_so',label:'检索记录'},
				{id:'stmo_export',label:'导出记录'},
	         ],		 	 
	 _header=['订单号','名称','型号','出库仓库','出库数量','单位','出库类型','状态','申请人','申请日期','出库审核人','出库日期','操作'],
	 _name=['preAppNo','name','model','stockplace','number','unit','sfType','status','appUser','appDate','stockUser','stockDate','opt'],
	 _width=[150,135,90,80,80,60,80,80,80,180,80,180,180],
	 _renderer=[0,0,0,0,0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
         if(v==1){
        	 return '<font color="red">待出库</font>';
         }
         else if(v==9){
        	 return '<font color="green">已出库</font>'
         }
     },0,0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
    	 return '<font style="font-weight:bold;font-style:italic;">双击订单详情</font>';
     }],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 _dataSource='stm/getStockOutList?type=0',
	 grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看详情
				stockFlowInfo(rowData,'dialog');
			}
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	
	$('#stmo_alList').click(function(){
		$('#'+gridId).omGrid({
			title:'已出库记录',
			dataSource:'stm/getStockOutList?type=1'
		});
	});
	
	$('#stmo_waitList').click(function(){
		$('#'+gridId).omGrid({
			title:'待出库记录',
			dataSource:'stm/getStockOutList?type=0'
		});
	});
	
	$('#stmo_pass').click(function(){
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		var ids=[],_data;
		for(var i=0;i<list.length;i++){
			ids[i]=list[i].id;	
			}
	    _data={ids:ids,opt:1},
	    re=ajax_json('stm/verifyOut',_data);
	    alert(re.info);
	    if(re.status){
	    	reloadGrid(gridId);
	    }
	});
	
	$('#stmo_fail').click(function(){
		var list=getGridSelections(gridId),
		_buttons=[
	    	{text:'提交',click:function(){
	    		_data={ids:ids,opt:-1,hint:"出库不通过原因："+$('#hint').val()};
	    		re=ajax_json('stm/verifyOut',_data);
	    	    alert(re.info);
	    	    if(re.status){
	    	    	closeDialog('dialog');
	    	    	reloadGrid(gridId);
	    	    }
		    }}
	    ];
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		$('#dialog').html('<textarea id="hint" name="content" rows="6" cols="40" onpropertychange="if(this.scrollHeight>40) this.style.posHeight=this.scrollHeight+5"></textarea>');
		$('#dialog').omDialog({
			title:'请输入不通过原因',
			modal:true,
			width:330,
			height:190,
			buttons:_buttons
		});
		var ids=[],_data;
		for(var i=0;i<list.length;i++){
			ids[i]=list[i].id;	
			}
	});
	
	$('#stmo_so').click(function(){//模糊检索
		var labels=[{name:'单号'},{name:'申请人'},{name:'名称'},{name:'类型'},{name:'状态'}],
			inputs=[{id:'stmi_so_k1'},{id:'stmi_so_k2'},{id:'stmi_so_k3'},{id:'stmi_so_k4'},{id:'stmi_so_k5'}],
			dialog={
				title:'模糊检索入库',
				modal:true,
				width:460,
				height:380,
				buttons:[{text:'检索',click:function(){
					
				}}]
		},
		formId=createFormDialog('dialog', labels, inputs, dialog, 1, '模糊检索');
		$('#stmi_so_k5').omCombo({
			dataSource:[{text:'未入库',value:0},{text:'已入库',value:9}]
		})
		
	});
	
	$('#stmo_export').click(function(){//导出
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		waitingPage('请等待', '数据生成中...', 3000);
	});
}

/**
 * 盘库管理
 */
function StockTaking(type){
	var _title='我的盘库单',_dataSource='skm/getStockTakingList?type=10';
	if(type==1){
		_title='盘库单审核';
		_dataSource='skm/getStockTakingList?type=0'
	}
	var tabId=addTab('center-tab', "盘库管理", "tab_StockTaking"),
	btnsbar=[									
	         	{id:'stkm_add',label:'新建盘库单'},	
				{id:'stkm_so',label:'检索盘库单'},
	         ],		 
	 _header=['盘库单号','申请人','申请日期','目标仓库','状态','审核人','审核日期','回执','操作'],
	 _name=['appNo','appUser','appDate','stockplace','status','verifyUser','verifyDate','hint','opt'],
	 _width=[150,90,160,100,110,90,160,120,100],
	 _renderer=[0,0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
		if(v==-2)
			 return '<font color="red">待清点</font>';
        else if(v==-1)
        	 return '<font color="red">未通过</font>';
        else if(v==0)
        	 return '<font color="blue">待审核</font>'
        else if(v<9)
        	 return '<font color="blue">盘库中</font>';
 		else if(v==9)
 			return '<font color="green">盘库成功</font>';
 		else if(v==-9)
 			return '<font color="red">盘库失败</font>';
 		else
 			return '<font color="red">未知状态！</font>';
     },0,0,0,function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
    	 return '<font style="font-weight:bold;font-style:italic;">双击订单详情</font>';
     }],
	 _colModel=createModel(_header, _name, _width,_renderer),
	 
	 grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看详情
				if(type==0)stockTakingInfo(rowData,0);
				if(type==1)stockTakingInfo(rowData,1);
			}
	
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	
	$('#stkm_add').click(function(){//创建盘库单
		stockManager();
	});
	
}



/**
 * 盘库单详情
 */
function stockTakingInfo(p,type){
	var _title='盘库单详情',btnsbar;
	if(type==0){
		btnsbar=[								
	     	{id:'stkinfo_count',label:'盘库'},
	     	{id:'stkinfo_export',label:'导出该盘库单'},	
	     ];
	}
	if(type==1){
		btnsbar=[								
	     	{id:'stkinfo_pass',label:'通过该盘库单'},
	     	{id:'stkinfo_fail',label:'不通过该盘库单'},
	     	{id:'stkinfo_export',label:'导出该盘库单'},	
	     ];
	}
	var tabId=addTab('center-tab', "盘库单详情", "tab_StockTakingInfo"),
	_header=['名称','编号','型号','单位','原始数量','盘库数量','变动数量',"业务类型",'出入','状态'],
	_name=['name','proNo','model','unit','nowNum','stNum','number',"sfType",'flowType','status'],
	_width=[140,120,120,60,80,80,80,80,60,90,120],
	_renderer=[0,0,0,0,0,function(v, rowData , rowIndex){
				if(rowData.flowType==-2){
					return '<font>'+rowData.nowNum+'</font>';
				}
				else if(rowData.flowType==1){
					var stNum  =rowData.nowNum+rowData.number;
					return '<font>'+stNum+'</font>';
				}else if(rowData.flowType==-1){
					var stNum  =rowData.nowNum-rowData.number;
					return '<font>'+stNum+'</font>';
				}else return '<font></font>';
			},0,0,function(v, rowData , rowIndex){
				if(v==-2) return '<font>数量一致</font>';
				else if(v==1) return '<font color="blue">入库</font>';
	    		else if(v==-1) return '<font color="red">出库</font>';
	    		else return '<font color="red">尚未确定</font>';
	    	},function(v, rowData , rowIndex) {//列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
	    		if(v==-2)return '<font color="red">待清点</font>';
    			else if(v==-1)return '<font color="red">未通过</font>';
    			else if(v==0)return '<font color="blue">待审核</font>';
    			else{
    	    		if(rowData.flowType==1){//入库单状态	
    		    		if(v==-9)return '<font color="yellow">入库不通过</font>';
    		    		else if(v<9)return  '<font color="blue">待入库</font>';
    		    		else if(v==9)return '<font color="green">已入库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-1){//出库单状态
    	    			if(v==-9)return '<font color="yellow">入库不通过</font>';
    		    		else if(v<9)return  '<font color="blue">待出库</font>';
    		    		else if(v==9)return '<font color="green">已出库</font>';
    		    		else return '<font color="red">未知状态</font>';
    	    		}else if(rowData.flowType==-2){
    		    		 return '<font color="green">已通过</font>';
    	    		}
    			}
	         }],
	   _colModel=createModel2(_header, _name, _width,_renderer),
	   _dataSource='stm/getStockFlowList?preAppNo='+p.appNo,
	   _title='盘库单号:'+p.appNo,
	   grid={
			title:_title,
	    	colModel:_colModel,
	    	dataSource:_dataSource,
	    	height:PANEL_HEIGHT*0.9,
	    	singleSelect:false,
	    },
		gridId=createTabContent(tabId, btnsbar, grid);
		$('#stkinfo_count').click(function(){//清点库存
			if(p.status>=1){
				alert('已通过盘库单不能修改！');
				return;
			}
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			$('#dialog').html('<table id="sttiTable" class="myTable"><tr><th>名称</th><th>编号</th><th>型号</th><th>原始数量</th><th>盘库数量</th></tr></table>');
			var content='';
			for(var i=0;i<list.length;i++){
				content+='<tr><td>'+list[i].name+'</td><td>'+list[i].proNo+'</td><td>'+list[i].model+'</td><td>'+list[i].nowNum+'</td><td><input id="stNum_'+i+'" name="stNum"/></td></tr>';
			}
			$('#sttiTable').append(content);
			$('#dialog').omDialog({
				title:'盘库单详情',
				modal:true,
				width:570,
				height:480,
				buttons:[
			    	{text:'提交',click:function(){
			    		var flowIds='',numbers='',re='';
			    		for(var i=0;i<list.length;i++){
			    			var number = parseInt($('#stNum_'+i+'').val())-list[i].nowNum;
			    			if(number==null||isNaN(number)){
			    				alert('请输入盘库数量');
			    				return;
			    			}
			    			flowIds+=list[i].id+',',
			    			numbers+=number+','
			    		}
			    		re=ajax_list('skm/countStockTaking?flowIds='+flowIds+'&numbers='+numbers);
			    	    alert(re.info);
			    	    if(re.status){
			    	    	closeDialog('dialog');
			    	    	reloadGrid(gridId);
			    	    }
				    }}
				]
			});
			$('input[name="stNum"]').omNumberField();
		});
		
		$('#stkinfo_pass').click(function(){//通过
		    re=ajax_list('skm/verifyStockTaking?preAppNo='+p.appNo+"&opt=1");
		    alert(re.info);
		    if(re.status){
		    	reloadGrid(gridId);
		    }
		});
		
		$('#stkinfo_fail').click(function(){//不通过
			var list=getGridSelections(gridId),
			_buttons=[
		    	{text:'提交',click:function(){
		    		re=ajax_list('skm/verifyStockTaking?preAppNo='+p.appNo+"&opt=-1&hint="+$('#hint').val());
		    	    alert(re.info);
		    	    if(re.status){
		    	    	closeDialog('dialog');
		    	    	reloadGrid(gridId);
		    	    }
			    }}
		    ];
			$('#dialog').html('<textarea id="hint" name="content" rows="6" cols="40" onpropertychange="if(this.scrollHeight>40) this.style.posHeight=this.scrollHeight+5"></textarea>');
			$('#dialog').omDialog({
				title:'请输入不通过原因',
				modal:true,
				width:330,
				height:190,
				buttons:_buttons
			});
		});
}