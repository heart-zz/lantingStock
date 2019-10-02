/**
 *申请模块
 */
/**
 * 获取品名列表
 */
function getProList(){
	var _title='品名列表',btnsbar=[],tabId,
		btnsbar=[									
			{id:'pro_add',label:'添加品名'},
//			{id:'pro_del',label:'删除品名'},
			{id:'addToStock',label:'导入仓库'},
	     ],
		tabId=addTab('center-tab', "品名管理", "tab_proManager"),
		_header=['产品名称','编号','型号','单位','备注','最后编辑人','操作'],
	 	_name=['name','proNo','model','unit','remark','author','opt'],
	 	_width=[200,200,200,120,200,120,100],
	 	_renderer=[0,0,0,0,0,0,function(v,rowData,rowIndex){
			 return '<font style="font-weight:bold;font-style:italic;">双击修改</font>';
		 }],
	 	_colModel=createModel(_header, _name, _width,_renderer),
	 	_dataSource='pur/getProList',
	 	grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			onRowDblClick:function(rowIndex,rowData,event){//双击修改品名
				var labels=[{name:'产品名称'},{name:'编号'},{name:'型号'},{name:'单位'},{name:'备注'}],
					inputs=[
						{id:'pro_name',name:'name',value:rowData.name},
						{id:'pro_No',name:'proNo',value:rowData.proNo},
						{id:'pro_model',name:'model',value:rowData.model},
						{id:'pro_unit',name:'unit',value:rowData.unit},
						{id:'pro_remark',name:'remark',value:rowData.remark},
					],
					dialog={
						title:"修改品名",
						width:450,
						height:500,
						modal:true,
						buttons:[{
						text:'提交',click:function(){
						if(confirm('修改品名会影响仓库里的该物品,确定修改?')){
							var re=ajax_form('pro/updatePro?id='+rowData.id+'',formId);
							alert(re.info);
							if(re.status==1){
								closeDialog('dialog');
								reloadGrid(gridId)
								}
						}
						}
					}]
				},
				formId=createFormDialog('dialog', labels, inputs, dialog,1, _title);
		}
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	
	$('#addToStock').click(function(){//将品名导入库存
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		$('#dialog').html('<td style="display:none"><input id="stockId" name="stockplaceId" /></td><td align="center"><input id="stockName" name="stockplace"/></td>');
		$('#dialog').omDialog({
			title:'选择仓库',
			modal:true,
			width:210,
			height:250,
			buttons:[	         
				{text:'提交',click:function(){
					var isNeed=['stockId','stockName'];
					if(!isInputNeeded(isNeed)){
						alert('请选择仓库');
						return;
					}
					var ids='',_data,re;
					for(var i=0;i<list.length;i++){
						ids+=list[i].id+',';	
						}
			  	    _data={proIds:ids,stockplaceId:parseInt($('#stockId').val())},
				    re=ajax_json('pro/addProToStock',_data);
				    alert(re.info);
					if(re.status==1){
						closeDialog('dialog');
					}
				}}
			]
		});
		var res = ajax_list("com/getMyStockListCombo");
		$("#stockName").omCombo({
			dataSource:res.list,
			onValueChange:function(target,newValue,oldValue,event){ 					    
				$("#stockName").val(target.val());//获取当前所选标签值
				$("#stockId").val(newValue);
			}
		});
	});
		
	$('#pro_add').click(function(){//添加品名
		addPro(gridId);
	});
	$('#pro_del').click(function(){//删除品名
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		if(confirm('确实删除?')){
			var ids='',_data;
			for(var i=0;i<list.length;i++){
				ids+=list[i].id+',';	
				}
    	    _data={proIds:ids,},
    	    re=ajax_json('pro/delPros',_data);
    	    alert(re.info);
    	    reloadGrid(gridId);
		}
	});
}

/**
 * 新增 领料单 明细
 */
function addPickInfo(appNo,stockPro){
	var _title='添加到领料单',_buttons=[],pickList=[];
	_buttons=[	         
		{text:'提交',click:function(){
			for(var i=0;i<stockPro.length;i++){
				var row={};
				row.proNo=stockPro[i].proNo;
				row.productId=stockPro[i].productId;
				row.name = stockPro[i].name;
				row.model=stockPro[i].model;
				row.unit = stockPro[i].unit;
				row.remark = stockPro[i].remark;
				row.price = stockPro[i].priceIn;
				row.stockId = stockPro[i].id;
				row.sum = parseInt($('#sum_'+i+'').val());
				row.number = parseInt($('#number_'+i+'').val());
				row.content = $('#content_'+i+'').val();
				pickList.push(row);
			}
			var picking={appNo:appNo,stockplaceId:stockPro[0].stockplaceId},
				_data={picking:picking,itemList:pickList,type:1},
				re=ajax_json('pick/addPicking',_data);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('out_pickList');
					reloadGrid('applyOutList');
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
		labels=[{name:'名称'},{name:'型号'},{name:'库存数量'},{name:'出库数量'},{name:'出库总价'},{name:'用途'}],
		pickformId=createDialog('dialog',labels,prodialog);
		var content='';
		for(var i=0;i<stockPro.length;i++){
			content+=('<tr><td><input name="name" class="width_120" value="'+stockPro[i].name+'"/></td><td><input name="model"  class="width_160" value="'+stockPro[i].model+'"/></td><td><input id="stockNum_'+i+'" name="stockNum"  class="width_60" value="'+stockPro[i].number+'"/></td><td><input id="number_'+i+'" name="number" class="width_60" /></td><td><input id="sum_'+i+'" name="sum" class="width_60" /></td><td><input id="content_'+i+'" name="content" class="width_160" /></td></tr>');
		}	

	$('#'+pickformId+"_table").append(content);
	$('input[name="name').attr('readonly','readonly');
	$('input[name="model').attr('readonly','readonly');
	$('input[name="stockNum').attr('readonly','readonly');
	$('input[name="number"]').omNumberField();
	$('input[name="sum"]').omNumberField();
}

/**
 * 新增 采购单 明细 
 */
function addPurInfo(appNo,pro){
	var _title='新增到采购单',_buttons=[],i=0,purList=[];
	_buttons=[	         
		{text:'提交',click:function(){
			var isNeed=[];
			for(var j=0;j<pro.length;j++){
				var str1 = "purNumbers_"+j;
				isNeed.push(str1)
				var str2 = "purSums_"+j;
				isNeed.push(str2)
				var str3 = "purBuyers_"+j;
				isNeed.push(str3)
			}
			if(!isInputNeeded(isNeed)){
				alert('明细信息不完整，请完善！');
				return;
			}
			for(i=0;i<pro.length;i++){
				var row={};
				row.stockId=pro[i].id;
				row.productId=pro[i].productId;
				row.name = pro[i].name;
				row.model=pro[i].model;
				row.proNo=pro[i].proNo;
				row.price = $('#purPrices_'+i+'').val();
				row.number = $('#purNumbers_'+i+'').val();
				row.remark = $('#purRemarks_'+i+'').val();
				row.sum = $('#purSums_'+i+'').val();
				row.unit = $('#purUnits_'+i+'').val();
				row.optUserId = $('#purBuyers_'+i+'').val();
				row.content = $('#purContents_'+i+'').val();
				purList.push(row);
			}
			var purchase={appNo:appNo,stockplaceId:pro[0].stockplaceId},
				_data={purchase:purchase,itemList:purList,type:1},
				re=ajax_json('pur/addPurchase',_data);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('pur_sProList');
					reloadGrid('applyPurList');
				}
			}}
		];
	var prodialog={
			title:_title,
			width:900,
			height:600,
			modal:true,
			buttons:_buttons
		},
		labels=[{name:'名称'},{name:'编号'},{name:'价格'},{name:'数量'},{name:'单位'},{name:'小计'},{name:'用途'},{name:'采购人'},{name:'备注'}],
		pickformId=createDialog('dialog',labels,prodialog);
		var content='';
		for(i=0;i<pro.length;i++){
				content+=('<tr><td><input id="purNames_'+i+'" name="purNames" class="width_120" value="'+pro[i].name+'"/></td><td><input id="purNos_'+i+'" name="purNos"  class="width_60" value="'+pro[i].proNo+'" /></td><td><input id="purPrices_'+i+'" name="purPrices" class="width_60" value="'+pro[i].priceIn+'"/></td><td><input id="purNumbers_'+i+'" name="purNumbers" class="width_60" /></td><td><input id="purUnits_'+i+'" name="purUnits"  class="width_60" value="'+pro[i].unit+'"/></td><td><input id="purSums_'+i+'" name="purSums" class="width_60" /></td><td><input id="purContents_'+i+'" name="purContents" class="width_90" /></td><td><input id="purBuyers_'+i+'" name="purBuyers" class="width_60"  /></td><td><input id="purRemarks_'+i+'" name="purRemarks" class="width_90" value="'+pro[i].remark+'"/></td></tr>');
			}	
	$('#'+pickformId+"_table").append(content);
	$('input[name="purNames').attr('readonly','readonly');
	$('input[name="purNos').attr('readonly','readonly');
	$('input[name="purUnits').attr('readonly','readonly');
	$('input[name="purPrices"]').omNumberField();
	$('input[name="purNumbers"]').omNumberField();
	$('input[name="purSums"]').omNumberField();
	var res=ajax_list("com/getPurTaskerListCombo");
	$('input[name="purBuyers"]').omCombo({
		dataSource:res.list,
		width:'90px',
		listMaxHeight:150,
		editable : false
	});
}

/**
 * 修改 采购单 明细 
 */
function updatePurInfo(appNo,flow){
	var _title='修改采购单',_buttons=[],i=0,purList=[];
	_buttons=[	         
		{text:'提交',click:function(){
			for(i=0;i<flow.length;i++){
				var row={};
				row.id=flow[i].id;
				row.price = $('#purPrices_'+i+'').val();
				row.number = $('#purNumbers_'+i+'').val();
				row.remark = $('#purRemarks_'+i+'').val();
				row.sum = $('#purSums_'+i+'').val();
				row.optUser = $('#purBuyers_'+i+'').val();
				row.content = $('#purContents_'+i+'').val();
				row.optUserId=$('#purBuyerIds_'+i+'').val();
				purList.push(row);
			}
			var purchase={appNo:appNo,stockplaceId:flow[0].stockplaceId},
				_data={purchase:purchase,itemList:purList,type:0},
				re=ajax_json('pur/addPurchase',_data);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('pur_sProList');
					reloadGrid('applyPurList');
				}
			}}
		];
	var prodialog={
			title:_title,
			width:900,
			height:600,
			modal:true,
			buttons:_buttons
		},
		labels=[{name:'名称'},{name:'型号'},{name:'价格'},{name:'数量'},{name:'单位'},{name:'小计'},{name:'用途'},{name:'采购人'},{name:'备注'}],
		pickformId=createDialog('dialog',labels,prodialog);
		var content='',sum=null;
		for(i=0;i<flow.length;i++){
				content+=('<tr><td><input id="purNames_'+i+'" name="purNames" class="width_120" value="'+flow[i].name+'"/></td><td><input id="purModels_'+i+'" name="purModels"  class="width_60" value="'+flow[i].model+'" /></td><td><input id="purPrices_'+i+'" name="purPrices" class="width_60" value="'+flow[i].price+'"/></td><td><input id="purNumbers_'+i+'" name="purNumbers" class="width_60" value="'+flow[i].number+'"/></td><td><input id="purUnits_'+i+'" name="purUnits"  class="width_60" value="'+flow[i].unit+'"/></td><td><input id="purSums_'+i+'" name="purSums" class="width_60" value="'+flow[i].sum+'"/></td><td><input id="purContents_'+i+'" name="purContents" class="width_90" value="'+flow[i].content+'"/></td><td style="display:none"><input id="purBuyerIds_'+i+'" name="purBuyerIds" class="width_60" value="'+flow[i].optUserId+'"/></td><td><input id="purBuyers_'+i+'" name="purBuyers" class="width_60" value="'+flow[i].optUser+'" /></td><td><input id="purRemarks_'+i+'" name="purRemarks" class="width_90" value="'+flow[i].remark+'"/></td></tr>');
			}	
	$('#'+pickformId+"_table").append(content);
	$('input[name="purNames').attr('readonly','readonly');
	$('input[name="purModels').attr('readonly','readonly');
	$('input[name="purUnits').attr('readonly','readonly');
	$('input[name="purPrices"]').omNumberField();
	$('input[name="purNumbers"]').omNumberField();
	$('input[name="purSums"]').omNumberField();
	var res=ajax_list("com/getPurTaskerListCombo");
	$('input[name="purBuyers"]').omCombo({
		dataSource:res.list,
		width:'90px',
		lazyLoad:true,
		listMaxHeight:150,
		editable : false,
		onValueChange:function(target,newValue,oldValue,event){ 					    
			$('input[name="purBuyers"]').val(target.val());//获取当前所选标签值
			$('input[name="purBuyerIds"]').val(newValue);
		}
	});
}

/**
 * 添加品名
 */
function addPro(gridId){
	var _title='添加品名',i=0;
		_buttons=[
			{text:'追加',click:function(){	
				var tdArray=[
					{name:'name',style:'width_200'},
					{name:'proNo',style:'width_120'},
					{name:'model',style:'width_200'},
					{name:'unit',style:'width_60'},
					{name:'remark',style:'width_200'}
				]
				addTableTr(proformId+"_table",tdArray,4);
			}},
				    
			{text:'提交',click:function(){
				var re=ajax_form('pro/addPro',proformId);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid(gridId)
				}
			}}
		];
	var prodialog={
		title:_title,
		width:900,
		height:600,
		modal:true,
		buttons:_buttons
	},
	labels=[{name:'名称'},{name:'编号'},{name:'型号'},{name:'单位'},{name:'备注'}],
	proformId=createDialog('dialog',labels,prodialog);
	var content='';
	for(i=0;i<6;i++){
		content+=('<tr><td><input id="name_'+i+'" name="name" class="width_200" /></td><td><input id="proNo_'+i+'" name="proNo" class="width_120" /></td><td><input id="model_'+i+'" name="model"  class="width_200"/></td><td><input id="unit_'+i+'" name="unit"  class="width_60" /></td><td><input id="remark'+i+'" name="remark" class="width_200" /></td></tr>');
	}	
	$('#'+proformId+"_table").append(content);
}

/**
 * 申请出库 添加到领料单
 */
function sendApplyOut(appNo,stockplaceId){
	if(appNo==null){
		appNo = ajax_list("pick/getPickNo");
	}
	var dialogHeight=PANEL_HEIGHT;
	var tabId=addTab('center-tab', "领料申请", "tab_ApplyOut");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="applyOutTab"></div><div style="width:50%;float:left"><div id="applyOutList"></div></div><div style="width:50%;float:left"><div id="out_pickList"></div></div>');
	$('#applyOutTab').omButtonbar({
		width:'100%',
		btns:[
				{id:'out_add',label:'添加明细'},
				{id:'out_del',label:'删除明细'},
				{id:'out_update',label:'修改出库数量'},
				{separtor:true},
			 ]
	});
	if(stockplaceId==null){
		$('#applyOutTab').omButtonbar({
			width:'100%',
			btns:[
					{id:'out_stockList',label:'选择出库仓库'},
				 ]
		});
	}
	var _header1=['名称','编号','型号','库存数量','单位','平均单价','所属仓库','操作'],
 		_name1=['name','proNo','model','number','unit','priceIn','stockplace','opt'],
 		_width1=[100,100,100,60,60,60,80,80],
 		_renderer1=[0,0,0,0,0,0,0,function(v,rowData,rowIndex){ 
 			return '<font style="font-weight:bold;font-style:italic;">双击添加</font>';
 		}],
 		_colModel1=createModel(_header1, _name1, _width1,_renderer1),
 		_header2=['名称','编号','型号','出库数量','单位','出库总价','出库状态','用途'],
	 	_name2=['name','proNo','model','number','unit','sum','status','content'],
	 	_width2=[100,100,120,60,60,60,60,100],
	 	_renderer2=[0,0,0,0,0,0,function(v, rowData , rowIndex) {
    		if(v<1)return '<font color="blue">待审核</font>';
    		else if(v==-9)return '<font color="yellow">出库失败</font>';
    		else if(v<9)return  '<font color="blue">待出库</font>';
    		else if(v==9)return '<font color="green">已出库</font>';
         },0],
		_colModel2=createModel(_header2, _name2, _width2,_renderer2);
	$('#out_pickList').omGrid({
		title:'库存列表',		
		colModel:_colModel1,	
		height:dialogHeight*0.9,
		singleSelect:false,
		limit:100000,
		onRowDblClick:function(rowIndex,rowData,event){
			var row=[];
			row.push(rowData);
			addPickInfo(appNo,row);
		}
		
	});
	if(stockplaceId!=null){
		$('#out_pickList').omGrid({
			dataSource:'stm/getStockList?type=1&stockplaceId='+stockplaceId,
		});
	}
	$('#applyOutList').omGrid({
		title:'领料单:单号'+appNo,		
		colModel:_colModel2,		
		height:dialogHeight*0.9,
		singleSelect:false,
		limit:100000,
		dataSource:"pick/getPickInfo?pickNo="+appNo
	});
	var res = ajax_list("com/getMyStockListCombo"),
	stockList=res.list;
	for(var i=0;i<stockList.length;i++){
		stockList[i].id = stockList[i].value;
		stockList[i].label = stockList[i].text;
	}
	var menu_stock_fn=function(item,event){
		$('#out_stockList').omButton({
			label:item.label
		});
		$('#out_pickList').omGrid({
			dataSource:'stm/getStockList?type=1&stockplaceId='+item.id,
		});
	};
	createMenu("menu_applyOutStock","out_stockList",stockList,menu_stock_fn,tabId);
	
	/**
	 * 添加到领料单
	 */
	$('#out_add').click(function(){
		try{ 
			var selects=getGridSelections('out_pickList');
			}catch(error){ 
				alert('请先选择仓库');
				return;
			}	
		if(isEmpty(selects)){
			alert('请选择要添加的产品');
			return;
		}
		addPickInfo(appNo,selects);
	});
	
	/**
	 * 从领料单删除
	 */
	$('#out_del').click(function(){
		var selects=getGridSelections('applyOutList');
		if(isEmpty(selects)){
			alert('请选择需要删除的明细');
			return;
		}
		if(confirm('确定删除?')){
			var pickList=[];
			for(var j=0;j<selects.length;j++){
				var row={};
				row.id=selects[j].id;
				pickList.push(row);
			}
			var picking={appNo:appNo,stockplaceId:selects[0].stockplaceId},
			_data={picking:picking,itemList:pickList,type:-1},
			re=ajax_json('pick/addPicking',_data);
			alert(re.info);
			if(re.status==1){
				closeDialog('dialog');
				reloadGrid('out_pickList');
				reloadGrid('applyOutList');
			}
		}		
	});
	
	/**
	 * 修改领料明细（出库数量）
	 */
	$('#out_update').click(function(){
		try{ 
			var selects=getGridSelections('applyOutList');
			}catch(error){ 
				alert('请先选择仓库');
				return;
			}	
		if(isEmpty(selects)){
			alert('请选择要修改的产品');
			return;
		}
		updatePickInfo(appNo,selects);
	});
}

/**
 * 修改 领料单 明细
 */
function updatePickInfo(appNo,flow){
	var _title='修改领料单',_buttons=[],pickList=[];
	_buttons=[	         
		{text:'提交',click:function(){
			for(var i=0;i<flow.length;i++){
				var row={};
				row.id=flow[i].id;
				row.sum = parseInt($('#sum_'+i+'').val());
				row.number = parseInt($('#number_'+i+'').val());
				row.content = $('#content_'+i+'').val();
				pickList.push(row);
			}
			var picking={appNo:appNo,stockplaceId:stockPro[0].stockplaceId},
				_data={picking:picking,itemList:pickList,type:0},
				re=ajax_json('pick/addPicking',_data);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('out_pickList');
					reloadGrid('applyOutList');
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
		labels=[{name:'名称'},{name:'型号'},{name:'出库数量'},{name:'出库总价'},{name:'用途'}],
		pickformId=createDialog('dialog',labels,prodialog);
		var content='';
		for(var i=0;i<flow.length;i++){
			content+=('<tr><td><input name="name" class="width_120" value="'+flow[i].name+'"/></td><td><input name="model"  class="width_160" value="'+flow[i].model+'"/></td><td><input id="number_'+i+'" name="number" class="width_60" value="'+flow[i].number+'"/></td><td><input id="sum_'+i+'" name="sum" class="width_60" /></td><td><input id="content_'+i+'" name="content" class="width_160" /></td></tr>');
		}	

	$('#'+pickformId+"_table").append(content);
	$('input[name="name').attr('readonly','readonly');
	$('input[name="model').attr('readonly','readonly');
	$('input[name="number"]').omNumberField();
	$('input[name="sum"]').omNumberField();
}

/**
 * 新增采购单
 */
function addPur(appNo,stockplaceId){
	if(appNo==null){
		appNo = ajax_list("pur/getPurNo");
	}
	var dialogHeight=PANEL_HEIGHT;
	var tabId=addTab('center-tab', "采购申请", "tab_ApplyPur");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="applyPurTab"></div><div style="width:50%;float:left"><div id="applyPurList"></div></div><div style="width:50%;float:left"><div id="pur_sProList"></div></div>');
	$('#applyPurTab').omButtonbar({
		width:'100%',
		btns:[
				{id:'pur_add',label:'添加明细'},
				{id:'pur_del',label:'删除明细'},
				{id:'pur_update',label:'修改明细'},
				{id:'pur_fastIntoStock',label:'新增库存'},
				{separtor:true},
			 ]
	});
	if(stockplaceId==null){
		$('#applyPurTab').omButtonbar({
			width:'100%',
			btns:[
					{id:'pur_stockList',label:'选择采购仓库'},
				 ]
		});
	}
	var _header1=['名称','编号','型号','库存数量','单位','平均单价','所属仓库','操作'],
	 	_name1=['name','proNo','model','number','unit','priceIn','stockplace','opt'],
	 	_width1=[100,100,100,60,60,60,80,80],
	 	_renderer1=[0,0,0,0,0,0,0,function(v,rowData,rowIndex){ 
	    	 return '<font style="font-weight:bold;font-style:italic;">双击添加</font>';
	     }],
		_colModel1=createModel(_header1, _name1, _width1,_renderer1),
		_header2=['名称','编号','型号','采购数量','采购单价','状态','用途','采购人'],
	 	_name2=['name','proNo','model','number','price','status','content','optUser'],
	 	_width2=[100,100,100,60,60,60,100,80],
	 	_renderer2=[0,0,0,0,0,function(v, rowData , rowIndex) {
    		if(v==-1)return '<font color="red">未通过</font>';
    		else if(v==0)return '<font color="blue">待审核</font>';
    		else if(v<6)return '<font color="blue">待采购</font>';
    		else if(v==-9)return '<font color="yellow">入库失败</font>';
    		else if(v<9)return  '<font color="blue">待入库</font>';
    		else if(v==9)return '<font color="green">已入库</font>';
    		else return '<font color="red">未知状态</font>';
         },0,0],
		_colModel2=createModel(_header2, _name2, _width2,_renderer2);
	$('#pur_sProList').omGrid({
		title:'库存列表',		
		colModel:_colModel1,	
		height:dialogHeight*0.9,
		singleSelect:false,
		onRowDblClick:function(rowIndex,rowData,event){
			var row=[];
			row.push(rowData);
			addPurInfo(appNo,row);
		}
	});
	if(stockplaceId!=null){
		$('#pur_sProList').omGrid({
			dataSource:'stm/getStockList?type=1&stockplaceId='+stockplaceId,
		});
	}
	$('#applyPurList').omGrid({
		title:'采购单:单号'+appNo,		
		colModel:_colModel2,		
		height:dialogHeight*0.9,
		singleSelect:false,
		limit:100000,
		dataSource:"pick/getPickInfo?pickNo="+appNo
	});
	var res = ajax_list("com/getMyStockListCombo"),
	stockList=res.list;
	for(var i=0;i<stockList.length;i++){
		stockList[i].id = stockList[i].value;
		stockList[i].label = stockList[i].text;
	}
	var menu_stock_fn=function(item,event){
		stockplaceId=item.id
		$('#pur_stockList').omButton({
			label:item.label
		});
		$('#pur_sProList').omGrid({
			dataSource:'stm/getStockList?type=1&stockplaceId='+item.id,
		});
	};
	createMenu("menu_applyPurStock","pur_stockList",stockList,menu_stock_fn,tabId);
	
	/**
	 * 添加到采购单
	 */
	$('#pur_add').click(function(){
		try{ 
			var selects=getGridSelections('pur_sProList');
			}catch(error){ 
				alert('请先选择仓库');
				return;
			}	
		if(isEmpty(selects)){
			alert('请选择要添加的产品');
			return;
		}
		addPurInfo(appNo,selects);
	});
	
	/**
	 * 从采购单删除
	 */
	$('#pur_del').click(function(){
		var selects=getGridSelections('applyPurList');
		if(isEmpty(selects)){
			alert('请选择需要删除的明细');
			return;
		}
		if(confirm('确定删除?')){
			var purList=[];
			for(var j=0;j<selects.length;j++){
				var row={};
				row.id=selects[j].id;
				purList.push(row);
			}
			var purchase={appNo:appNo,stockplaceId:selects[0].stockplaceId},
			_data={purchase:purchase,itemList:purList,type:-1},
			re=ajax_json('pur/addPurchase',_data);
			alert(re.info);
			if(re.status==1){
				closeDialog('dialog');
				reloadGrid('pur_sProList');
				reloadGrid('applyPurList');
			}
		}		
	});
	
	/**
	 * 修改采购明细
	 */
	$('#pur_update').click(function(){
		try{ 
			var selects=getGridSelections('applyPurList');
			}catch(error){ 
				alert('请先选择仓库');
				return;
			}	
		if(isEmpty(selects)){
			alert('请选择要修改的产品');
			return;
		}
		updatePurInfo(appNo,selects);
	});
	
	/**
	 * 直接添加品名到仓库
	 */
	$('#pur_fastIntoStock').click(function(){
		if(stockplaceId==null){
			alert('请先选择仓库');
			return;
		}
		var _title='新增品名到仓库';
		_buttons=[
			{text:'追加',click:function(){	
				var tdArray=[
					{name:'name',style:'width_200'},
					{name:'proNo',style:'width_120'},
					{name:'model',style:'width_200'},
					{name:'unit',style:'width_60'},
					{name:'remark',style:'width_200'}
				]
				addTableTr(proformId+"_table",tdArray,4);
			}},
				    
			{text:'提交',click:function(){
				
				var re=ajax_form('pro/fastAddProToStock',proformId);
				alert(re.info);
				if(re.status==1){
					closeDialog('dialog');
					reloadGrid('pur_sProList')
				}
			}}
		];
	var prodialog={
		title:_title,
		width:900,
		height:600,
		modal:true,
		buttons:_buttons
	},
	labels=[{name:'名称'},{name:'编号'},{name:'型号'},{name:'单位'},{name:'备注'}],
	proformId=createDialog('dialog',labels,prodialog);
	var content='';
	for(i=0;i<6;i++){
		content+=('<tr><td><input id="name_'+i+'" name="name" class="width_200" /></td><td><input id="proNo_'+i+'" name="proNo" class="width_120" /></td><td><input id="model_'+i+'" name="model"  class="width_200"/></td><td><input id="unit_'+i+'" name="unit"  class="width_60" /></td><td><input id="remark'+i+'" name="remark" class="width_200" /></td></tr>');
	}	
	$('#'+proformId+"_table").append(content);
	$('#'+proformId+"_table").append('<td style="display:none"><input id="stockId" name="stockplaceId" value="'+stockplaceId+'"/></td>');
	});
}

/**
 * 申请退料
 */
function addPickReturn(){
	var dialogHeight=PANEL_HEIGHT,_title='我的领料物品列表',flag=0;
	var tabId=addTab('center-tab', "退料申请", "tab_pickReturn");
	$('#'+tabId).html('');
	$('#'+tabId).append('<div id="pickReturnTab"></div><div id="pickProList"></div>');
	$('#pickReturnTab').omButtonbar({
		width:'100%',
		btns:[
				{id:'pickRn_list',label:'点击切换列表'},
				{id:'pickRn_apply',label:'申请退料'}
			 ]
	});
	var _header=['原领料单号','名称','编号','型号','原领料数量','剩余数量','入库平均价','所属仓库','出库日期','操作'],
	 	_name=['preAppNo','name','proNo','model','number','nowNum','price','stockplace','stockDate','opt'],
	 	_width=[150,120,120,120,80,80,80,80,150,120],
	 	_renderer=[0,0,0,0,0,0,0,0,0,function(v,rowData,rowIndex){ 
	    	 return '<font style="font-weight:bold;font-style:italic;">双击查看原领料单</font>';
	     }],
		_colModel=createModel(_header, _name, _width,_renderer),
		_header2=['原领料单号','名称','编号','型号','退料数量','入库平均价','所属仓库','状态','入库审核人','入库日期','操作'],
	 	_name2=['preAppId','name','proNo','model','number','price','stockplace','status','stockUser','stockDate','opt'],
	 	_width2=[150,100,100,120,60,80,60,60,80,150,120],
	 	_renderer2=[0,0,0,0,0,0,0,function(v, rowData , rowIndex) {
    		if(v==-9)return '<font color="yellow">入库失败</font>';
    		else if(v<9)return  '<font color="blue">待入库</font>';
    		else if(v==9)return '<font color="green">已入库</font>';
    		else return '<font color="red">未知状态</font>';
         },0,0,function(v,rowData,rowIndex){ 
	    	 return '<font style="font-weight:bold;font-style:italic;">双击查看原领料单</font>';
	     }],
		_colModel2=createModel(_header2, _name2, _width2,_renderer2),
		menu_prBn_data=[
 			{id:'prBnList_allFlow',label:'全部记录'},
 			{id:'prBnList_wait',label:'待入库'},
 			{id:'prBnList_fail',label:'入库失败'},
 			{id:'prBnList_stock',label:'已入库'},
 		],
		menu_prBn_fn=function(item,event){
		   var _dataSource =  '';
		if(item.id=='prBnList_wait'){
			_dataSource='pick/getMyPickRnList?type=6';
		}
		else if(item.id=='prBnList_fail'){
			_dataSource='pick/getMyPickRnList?type=-9';
		}
		else if(item.id=='prBnList_stock'){
			_dataSource='pick/getMyPickRnList?type=9';
		}
		else if(item.id=='prBnList_allFlow'){
			_dataSource='pick/getMyPickRnList?type=10';
		}
		$('#pickRn_list').omButton({
			label:item.label
		});
		$('#pickProList').omGrid({
			title:item.label,
			colModel:_colModel2,	
			dataSource:_dataSource
		});
		flag=1;
	};
		createMenu('menu_applyPickRn','pickRn_list',menu_prBn_data, menu_prBn_fn, tabId);
	$('#pickProList').omGrid({
		title:_title,		
		colModel:_colModel,	
		height:dialogHeight*0.9,
		singleSelect:false,
		dataSource:"stm/getStockOutList?type=3",
		onRowDblClick:function(rowIndex,rowData,event){
			stockFlowInfo(rowData,'dialog');
		}
	});

	/**
	 * 申请退料
	 */
	$('#pickRn_apply').click(function(){
		if(flag==1){
			flag=0;
			$('#pickProList').omGrid({
				title:'我的领料物品列表',	
				colModel:_colModel,
				dataSource:'stm/getStockOutList?type=3'
			});
		}else{
			var selects=getGridSelections('pickProList');
			if(isEmpty(selects)){
				alert('请选择需要退料的物品');
				return;
			}
			var _title='申请退料',_buttons=[],itemList=[];
				_buttons=[	         
					{text:'提交',click:function(){
							var flowIds='',re,numbers='';
							for(var i=0;i<selects.length;i++){
								if(selects[i].nowNum<parseInt($('#number_'+i+'').val())){
									alert('退料数量过多！');
									return;
								}
								flowIds+=selects[i].id+',';
								numbers+=parseInt($('#number_'+i+'').val())+',';
							}
							re=ajax_list('pick/addPickReturn?flowIds='+flowIds+'&numbers='+numbers);
							alert(re.info);
							if(re.status==1){
								closeDialog('dialog');
								reloadGrid('pickProList');
						}
					}}
				];
			var prodialog={
				title:_title,
				width:660,
				height:600,
				modal:true,
				buttons:_buttons
				},
				labels=[{name:'名称'},{name:'编号'},{name:'型号'},{name:'领料数量'},{name:'退料数量'}],
				pickformId=createDialog('dialog',labels,prodialog);
				var content='';
				for(var i=0;i<selects.length;i++){
					content+=('<tr><td><input name="name" class="width_120" value="'+selects[i].name+'"/></td><td><input name="name" class="width_120" value="'+selects[i].proNo+'"/></td><td><input name="model"  class="width_160" value="'+selects[i].model+'"/></td><td><input id="nowNum_'+i+'" name="nowNum" class="width_60" value="'+selects[i].nowNum+'"/></td><td><input id="number_'+i+'" name="number" class="width_60" value="'+selects[i].number+'"/></td></tr>');
				}	
				$('#'+pickformId+"_table").append(content);
				$('input[name="name').attr('readonly','readonly');
				$('input[name="proNo').attr('readonly','readonly');
				$('input[name="model').attr('readonly','readonly');
				$('input[name="number"]').omNumberField();
				$('input[name="nowNum"]').attr('readonly','readonly');	
		}
	});
	
	/**
	 * 从采购单删除
	 */
	$('#pickRn_del').click(function(){
		var selects=getGridSelections('applyPurList');
		if(isEmpty(selects)){
			alert('请选择需要删除的明细');
			return;
		}
		if(confirm('确定删除?')){
			var purList=[];
			for(var j=0;j<selects.length;j++){
				var row={};
				row.id=selects[j].id;
				purList.push(row);
			}
			var purchase={appNo:appNo,stockplaceId:selects[0].stockplaceId},
			_data={purchase:purchase,itemList:purList,type:-1},
			re=ajax_json('pur/addPurchase',_data);
			alert(re.info);
			if(re.status==1){
				closeDialog('dialog');
				reloadGrid('pur_sProList');
				reloadGrid('applyPurList');
			}
		}		
	});
	
	/**
	 * 修改采购明细
	 */
	$('#pickRn_update').click(function(){
		try{ 
			var selects=getGridSelections('applyPurList');
			}catch(error){ 
				alert('请先选择仓库');
				return;
			}	
		if(isEmpty(selects)){
			alert('请选择要修改的产品');
			return;
		}
		updatePurInfo(appNo,selects);
	});
}