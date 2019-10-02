/**
 * 采购模块
 */

/**
 * 采购管理-用户
 * type 0 采购申请管理 我申请的所有采购，待审核采购，已审核采购，部分入库采购，全部入库采购
 * type 1 采购审核管理 所有采购，所有待审核的，所有审核通过的，我审核通过的,我采购(经办)过的
 */
function getpurList(type){
	var _header=['采购单号','申请人','申请日期','目标仓库','操作','状态','审核人','审核日期','回执'],
 		_name=['appNo','appUser','appDate','stockplace','status','status','verifyUsername','verifyDate','hint'],
 		_width=[190,100,155,95,95,95,100,155,200],
 		_renderer=[0,0,0,0,function(v,rowData,rowIndex){
 			var optMsg;
 			if(v<1&&type==0){
 				optMsg="双击修改明细";
 			}else{
 				optMsg="双击查看明细";
 			}
 		return '<font style="font-weight:bold;font-style:italic;">'+optMsg+'</font>';
 		},function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
 			if(v==0)
 				return '<font color="blue">待审核</font>';
 			else if(v==1)
 				return '<font color="blue">待采购</font>';
 			else if(v==6)
 				return '<font color="#EE7621">已部分入库</font>';
 			else if(v==9)
 				return '<font color="green">已全部入库</font>';
 			else if(v==-9)
 				return '<font color="red">入库失败</font>';
 			else if(v==-1)
 				return '<font color="red">未通过</font>';
 			else
 				return '<font color="red">未知状态！</font>';
 		},0,0],
 		_colModel=createModel(_header, _name, _width,_renderer);
	
	if(type==0){
		var _title='采购申请列表',_tab="采购申请管理",_tabId="tab_purApply",
		btnsbar=[									
	     	{id:'purApply_list',label:'点击切换列表'},
			{id:'purm_add',label:'新增采购单'},
			{id:'purm_del',label:'删除采购单'},
			{id:'purm_so',label:'检索采购单'},
			{id:'purm_export',label:'导出采购单'},
	     ],
	    _dataSource='pur/getMyPurList?type=10',//我的
	    tabId=addTab('center-tab',_tab, _tabId);
	}else if(type==1){
		var _title='采购审核列表',_tab="采购审核管理",_tabId="tab_purVerify",
		btnsbar=[									
	     	{id:'purVerify_list',label:'点击切换列表'},
			{id:'purm_pass',label:'通过审核'},
			{id:'purm_fail',label:'不通过审核'},
			{id:'purm_so',label:'检索采购单'},
			{id:'purm_export',label:'导出采购单'},
	     ],
	    _dataSource='pur/getPurList?type=0',
	    tabId=addTab('center-tab',_tab, _tabId);
	}
	 	var grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看采购单明细
				if(rowData.status<1&&type==0)addPur(rowData.appNo,rowData.stockplaceId);
				else{
					stockFlowInfo(rowData);
				}
			}
		};
		if(type==0){
			var menu_pur_content='menu_applyPur',
			purm_list='purApply_list',
	 		menu_pur_data=[
	 			{id:'myPur_list_all',label:'全部采购'},
	 			{id:'myPur_list_wait',label:'待审核的'},
	 			{id:'myPur_list_pass',label:'已通过的'},
	 			{id:'myPur_list_fail',label:'未通过的'},
	 			{id:'myPur_list_nstock',label:'部分入库'},
	 			{id:'myPur_list_istock',label:'全部入库'}
	 		];
		}else if(type==1){
			var menu_pur_content='menu_verifyPur',
			purm_list='purVerify_list',
			menu_pur_data=[
				{id:'pur_list_all',label:'全部采购'},
				{id:'pur_list_wait',label:'待审核的'},
				{id:'pur_list_pass',label:'已通过的'},
				{id:'pur_list_fail',label:'未通过的'},
				{id:'pur_list_mypass',label:'我通过的'},
		      ];
		}
		var menu_pur_fn=function(item,event){
		   _dataSource =  '';
		if(item.id=='myPur_list_all'){
			_dataSource='pur/getMyPurList?type=10';		
		}
		else if(item.id=='myPur_list_wait'){
			_dataSource='pur/getMyPurList?type=0';
		}
		else if(item.id=='myPur_list_pass'){
			_dataSource='pur/getMyPurList?type=1';
		}
		else if(item.id=='myPur_list_fail'){
			_dataSource='pur/getMyPurList?type=-1';
		}
		else if(item.id=='myPur_list_nstock'){
			_dataSource='pur/getMyPurList?type=3';
		}
		else if(item.id=='myPur_list_istock'){
			_dataSource='pur/getMyPurList?type=9';
		}
		else if(item.id=='pur_list_wait'){
			_dataSource='pur/getPurList?type=0';
		}
		else if(item.id=='pur_list_pass'){
			_dataSource='pur/getPurList?type=1';
		}
		else if(item.id=='pur_list_mypass'){
			_dataSource='pur/getPurList?type=2';
		}
		else if(item.id=='pur_list_fail'){
			_dataSource='pur/getPurList?type=-1';
		}
		else if(item.id=='pur_list_all'){
			_dataSource='pur/getPurList?type=9';
		}
		$('#purApply_list').omButton({
			label:item.label
		});
		$('#purVerify_list').omButton({
			label:item.label
		});
		$('#'+gridId).omGrid({
			dataSource:_dataSource
		});
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	createMenu(menu_pur_content,purm_list, menu_pur_data, menu_pur_fn, tabId);
	
	$('#purm_detail').click(function(){//采购明细
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		purchaseInfo(list[0],'dialog');
	});
	
	$('#purm_add').click(function(){//新增采购
		addPur();
	});
	
	$('#purm_del').click(function(){//删除采购
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		if(list[0].status>=1){
			alert('只有[不通过,待审核]的记录可以删除');
			return;
		}
		if(confirm('确实删除?')){
			var purNos=[],_data;
			for(var i=0;i<list.length;i++){
				purNos[i]=list[i].appNo;	
				}
		    _data={purNos:purNos},
		    re=ajax_json('pur/delPur',_data);
		    alert(re.info);
		    if(re.status){
		    	reloadGrid(gridId);
		    }
		}
	});
	
	$('#purm_so').click(function(){//检索
		var labels=[{name:'采购单号'},{name:'申请日期'},{name:'状态'}],inputs=[{id:'purm_so_k1'},{id:'purm_so_k2'},{id:'purm_so_k3'}],
		dialog={
				title:'模糊检索',
				modal:true,
				width:500,
				height:350,
				buttons:[{text:'检索',click:function(){
					
				}}]
		},
		formId=createFormDialog('dialog', labels, inputs, dialog, 1, '模糊检索');
		
		$('#purm_so_k2').omCalendar();
		$('#purm_so_k3').omCombo({
			dataSource:[{text:'待审核',value:0},{text:'已审核,待入库',value:1},{text:'部分入库',value:3},{text:'已入库',value:9},{text:'不通过',value:-1}]
		})
	});
	
	$('#purm_export').click(function(){//导出
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}		
		waitingPage('请等待', '数据生成中...', 3000);
		var re=ajax_list('pur/exportPurList');	
		window.location=re.obj;
	});
	
	$('#purm_pass').click(function(){//通过
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		var purNos=[],_data;
		for(var i=0;i<list.length;i++){
			purNos[i]=list[i].appNo;	
			}
	    _data={purNos:purNos,opt:1},
	    re=ajax_json('pur/verifyPur',_data);
	    alert(re.info);
	    if(re.status){
	    	reloadGrid(gridId);
	    }
	});
	
	$('#purm_fail').click(function(){//不通过
		var list=getGridSelections(gridId),
		_buttons=[
	    	{text:'提交',click:function(){
	    		_data={purNos:purNos,opt:-1,hint:$('#hint').val()};
	    		re=ajax_json('pur/verifyPur',_data);
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
		var purNos=[],_data;
		for(var i=0;i<list.length;i++){
			purNos[i]=list[i].appNo;	
			}
	});
}

///**
// * 采购明细
// */
//function purchaseInfo(rowData,type){
//	var purNo=rowData.appNo,_title='',_button;
//	var re = ajax_list('pur/getPurInfo?purNo='+purNo);
//	var l=re.list.length,infoList=re.list,purList=[];
//	if(rowData.status<1&&type==0){
//		_title='修改采购明细';
//		_button=[
//	    	{text:'提交',click:function(){
//	    		for(var i=0;i<l;i++){
//	    			var purInfo={};
//	    			purInfo.price=$('#purPrices_'+i+'').val();
//	    			purInfo.number=$('#purNumbers_'+i+'').val();
//	    			purInfo.sum=$('#purSums_'+i+'').val();
//	    			purInfo.remark=$('#purRemarks_'+i+'').val();
//	    			purInfo.optUser=$('#purBuyers_'+i+'').val();
//	    			purInfo.content=$('#purContents_'+i+'').val();
//	    			purInfo.id=infoList[i].id;
//	    			purList.push(purInfo);
//	    		}
//		    	var _data={itemList:purList},
//		    	re=ajax_json('pur/updatePurInfo',_data);
//	    		alert(re.info);
//		    	if(re.status==1){
//			    	closeDialog('dialog');
//		    	}
//		    }}
//	    ];
//	}
//	else _title='查看采购明细';
//	var infodialog={
//			title:_title,
//			width:900,
//			height:600,
//			modal:true,
//			buttons:_button
//		},
//	labels=[{name:'名称'},{name:'型号'},{name:'单价'},{name:'数量'},{name:'单位'},{name:'小计'},{name:'用途'},{name:'采购人'},{name:'备注'}],
//	infologId=createDialog("dialog",labels,infodialog);
//	var content='';
//	for(i=0;i<l;i++){
//		content+=('<tr><td><input id="purNames_'+i+'" name="name" class="width_120" value="'+infoList[i].name+'" /></td><td><input id="purModels_'+i+'" name="model"  class="width_60" value="'+infoList[i].model+'" /></td><td><input id="purPrices_'+i+'" name="price" class="width_60" value="'+infoList[i].price+'"/></td><td><input id="purNumbers_'+i+'" name="number" class="width_60" value="'+infoList[i].number+'" /></td><td><input id="purUnits_'+i+'" name="unit"  class="width_60" value="'+infoList[i].unit+'"/></td><td><input id="purSums_'+i+'" name="sum" class="width_60" value="'+infoList[i].sum+'"/></td><td><input id="purContents_'+i+'" name="content" class="width_90" value="'+infoList[i].content+'"/></td><td><input id="purBuyers_'+i+'" name="optUser" class="width_60"  value="'+infoList[i].optUser+'"/></td><td><input id="purRemarks_'+i+'" name="remark" class="width_90" value="'+infoList[i].remark+'"/></td></tr>');
//	}	
//	$('#'+infologId+"_table").append(content);
//	$('input[name="name').attr('readonly','readonly');
//	$('input[name="model').attr('readonly','readonly');
//	$('input[name="unit').attr('readonly','readonly');
//	if(rowData.status>=1||type==1){
//		$('input[name="price').attr('readonly','readonly');
//		$('input[name="number').attr('readonly','readonly');
//		$('input[name="sum').attr('readonly','readonly');
//		$('input[name="content').attr('readonly','readonly');
//		$('input[name="optUser').attr('readonly','readonly');
//		$('input[name="remark').attr('readonly','readonly');
//	}
//	$('input[name="price"]').omNumberField();
//	$('input[name="number"]').omNumberField();
//	$('input[name="sum"]').omNumberField();
//	$('input[name="optUser"]').omCombo({
//		dataSource:"com/getPurTaskerListCombo",
//		width:'90px',
//		lazyLoad:true,
//		listMaxHeight:150,
//		editable : false,
//		onValueChange:function(target,newValue,oldValue,event){ 					    
//			$('input[name="purBuyers"]').val(target.val());//获取当前所选标签值
//			$('input[name="purBuyerIds"]').val(newValue);
//		}
//	});
//}


function getPurTask(){
	var _header=["采购单号",'物品名称','物品型号','预计单价','采购数量','数量单位','预计总价','目标仓库','申请日期'],
		_name=['preAppNo','name','model','price','number','unit','sum','stockplace','verifyDate'],
		_width=['180',180,150,100,100,100,100,100,200],
		_renderer=[0,0,0,0,0,0,0,0,0],
		_colModel=createModel(_header, _name, _width,_renderer),
		_title='采购清单列表',_tab="采购任务",_tabId="tab_purTask",tabId=addTab('center-tab',_tab, _tabId),
		_dataSource='pur/getPurTask',
		btnsbar=[									
			{id:'purm_finish',label:'完成采购'},
	     ],
	 	grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
		},
		gridId=createTabContent(tabId, btnsbar, grid);
	
		$('#purm_finish').click(function(){//完成采购
			var list=getGridSelections(gridId);
			if(isEmpty(list)){
				alert('请选择记录');
				return;
			}
			var itemList=[],content='';
			$('#dialog').html('<form id="mpa_buy_finish_form"><table class="myTable" id="table_mpa_buy_finish"><tr><th>名称</th><th>参考价格</th><th>实际价格</th><th>数量</th><th>实际总价</th></tr></table></form>')
			for(var i=0;i<list.length;i++){
				content+=('<tr><input name="ids" type="hidden" value="'+list[i].id+'" /><td>'+list[i].name+'</td><td>'+list[i].price+'</td><td><input id="realPrice_'+i+'" name="realPrice" value="'+list[i].price+'" /></td><td>'+list[i].number+'</td><td><input id="realSum_'+i+'" name="realSum" value="'+list[i].sum+'" /></td></tr>');
			}
			$('#table_mpa_buy_finish').append(content);
			$('#dialog').omDialog({
				title:'完成采购,确定采购价格',
				modal:true,
				width:680,
				height:500,
				buttons:[{text:'确定',click:function(){
					for(var i=0;i<list.length;i++){
						var row={};
						row.id=list[i].id;
						row.price = $('#realPrice_'+i+'').val();
						row.sum = $('#realSum_'+i+'').val();
						itemList.push(row);
					}
					var _data={itemList:itemList},
					re=ajax_json('pur/doPurTask',_data);
					alert(re.info);
					if(re.status){
						closeDialog('dialog');
						reloadGrid(gridId);
					}
				}}]
			});
			$('input[name="prices"]').omNumberField();
	});
}
function getMyPurTask(){
	var _header=["采购单号",'物品名称','物品型号','采购单价','采购数量','数量单位','采购总价','目标仓库','申请日期'],
		_name=['preAppNo','name','model','price','number','unit','sum','stockplace','verifyDate'],
		_width=['180',180,150,100,100,100,100,100,200],
		_renderer=[0,0,0,0,0,0,0,0,0],
		_colModel=createModel(_header, _name, _width,_renderer),
		_title='我的已采购列表',_tab="我的采购",_tabId="tab_myPurTask",tabId=addTab('center-tab',_tab, _tabId),
		_dataSource='pur/getMyPurTask',
		btnsbar=[									
			{id:'purm_so',label:'模糊搜索'},
	     ],
	 	grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
		},
		gridId=createTabContent(tabId, btnsbar, grid);
}