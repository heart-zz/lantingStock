/**
 * 领料单模块
 */

/** 
 * type 0 领料申请管理	我申请的所有领料，待审核领料，已审核领料，部分出库领料，全部出库领料
 * type 1 领料审核管理 所有领料，所有待审核的，所有审核通过的，我审核通过的
 */
function getPickList(type){
	var _header=['领料单号','申请人','申请日期','出库仓库','操作','状态','审核人','审核日期','回执'],
 		_name=['appNo','appUser','appDate','stockplace','status','status','verifyUser','verifyDate','hint'],
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
 				return '<font color="blue">待出库</font>';
 			else if(v==6)
 				return '<font color="#EE7621">已部分出库</font>';
 			else if(v==9)
 				return '<font color="green">已全部出库</font>';
 			else if(v==-1)
 				return '<font color="red">未通过</font>';
 			else
 				return '<font color="red">未知状态！</font>';
 		},0,0],
 		_colModel=createModel(_header, _name, _width,_renderer);
	
	if(type==0){
		var _title='全部领料单列表',_tab="领料申请管理",_tabId="tab_PickApply",
		btnsbar=[									
	     	{id:'pick_applyList',label:'点击切换列表'},
			{id:'pick_add',label:'新增领料单'},
			{id:'pick_del',label:'删除领料单'},
			{id:'pick_so',label:'检索领料单'},
			{id:'pick_export',label:'导出领料单'},
	     ],
	    _dataSource='pick/getMyPickList?type=10',//我的全部
	    tabId=addTab('center-tab',_tab, _tabId);
	}else if(type==1){
		var _title='待审领料单列表',_tab="领料审核管理",_tabId="tab_PickVerify",
		btnsbar=[									
	     	{id:'pick_verifyList',label:'点击切换列表'},
			{id:'pick_pass',label:'通过审核'},
			{id:'pick_fail',label:'不通过审核'},
			{id:'pick_so',label:'检索领料单'},
			{id:'pick_export',label:'导出领料单'},
	     ],
	    _dataSource='pick/getPickList?type=0',
	    tabId=addTab('center-tab',_tab, _tabId);
	}
	 	var grid={
			title:_title,
			colModel:_colModel,
			dataSource:_dataSource,
			height:PANEL_HEIGHT*0.9,
			singleSelect:false,
			onRowDblClick:function(rowIndex,rowData,event){//双击查看领料单明细
				if(rowData.status<1&&type==0)sendApplyOut(rowData.appNo,rowData.stockplaceId);
				else{
					stockFlowInfo(rowData);
				}
			}
		};
		if(type==0){
			var menu_pick_content='menu_applyPick',
			pick_list='pick_applyList',
			menu_pick_data=[
	 			{id:'myPick__all',label:'全部领料单'},
	 			{id:'myPick__wait',label:'待审核的'},
	 			{id:'myPick__pass',label:'已通过的'},
	 			{id:'myPick__fail',label:'未通过的'},
	 			{id:'myPick__nstock',label:'部分出库'},
	 			{id:'myPick__istock',label:'全部出库'}
	 		];
		}else if(type==1){
			var menu_pick_content='menu_verifyPick',
			pick_list='pick_verifyList',
			menu_pick_data=[
				{id:'pick_all',label:'全部采购'},
				{id:'pick_wait',label:'待审核的'},
				{id:'pick_pass',label:'已通过的'},
				{id:'pick_fail',label:'未通过的'},
				{id:'pick_mypass',label:'我通过的'},
		      ];
		}
		var menu_pick_fn=function(item,event){
		   _dataSource =  '';
		   _title='';
		   if(type==0){
				if(item.id=='myPick__all'){
					_dataSource='pick/getMyPickList?type=10';
					_title='全部领料单列表';
				}
				else if(item.id=='myPick__wait'){
					_dataSource='pick/getMyPickList?type=0';
					_title='待审领料单列表';
				}
				else if(item.id=='myPick__pass'){
					_dataSource='pick/getMyPickList?type=1';
					_title='已通过领料单列表';
				}
				else if(item.id=='myPick__fail'){
					_dataSource='pick/getMyPickList?type=-1';
					_title='未通过领料单列表';
				}
				else if(item.id=='myPick__nstock'){
					_dataSource='pick/getMyPickList?type=3';
					_title='部分出库领料单列表';
				}
				else if(item.id=='myPick__istock'){
					_dataSource='pick/getMyPickList?type=9';
					_title='完全出库领料单列表';
				}
				$('#pick_applyList').omButton({
					label:item.label
				});
		   }else if(type==1){
				if(item.id=='pick_wait'){
					_dataSource='pick/getPickList?type=0';
					_title='待审领料单列表';
				}
				else if(item.id=='pick_pass'){
					_dataSource='pick/getPickList?type=1';
					_title='已通过领料单列表';
				}
				else if(item.id=='pick_mypass'){
					_dataSource='pick/getPickList?type=2';
					_title='我通过领料单列表';
				}
				else if(item.id=='pick_fail'){
					_dataSource='pick/getPickList?type=-1';
					_title='未通过领料单列表';
				}
				else if(item.id=='pick_all'){
					_dataSource='pick/getPickList?type=9';
					_title='全部领料单列表';
				}
				$('#purVerify_list').omButton({
					label:item.label
				});
		   }
		   $('#'+gridId).omGrid({
			   dataSource:_dataSource,
			   title:_title
		   });
	},
	gridId=createTabContent(tabId, btnsbar, grid);
	createMenu(menu_pick_content,pick_list, menu_pick_data, menu_pick_fn, tabId);
	
	$('#purm_detail').click(function(){//采购明细
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		purchaseInfo(list[0],'dialog');
	});
	
	$('#pick_add').click(function(){//新增领料
		sendApplyOut();
	});
	
	$('#pick_del').click(function(){//删除领料
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		if(list[0].status>=1){
			alert('该状态下,无法删除');
			return;
		}
		if(confirm('确实删除?')){
			var pickNos=[],_data;
			for(var i=0;i<list.length;i++){
				pickNos[i]=list[i].appNo;	
				}
		    _data={pickNos:pickNos},
		    re=ajax_json('pick/delPick',_data);
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
	
	$('#pick_pass').click(function(){//通过
		var list=getGridSelections(gridId);
		if(isEmpty(list)){
			alert('请选择记录');
			return;
		}
		var pickNos=[],_data;
		for(var i=0;i<list.length;i++){
			pickNos[i]=list[i].appNo;	
			}
	    _data={pickNos:pickNos,opt:1},
	    re=ajax_json('pick/verifyPick',_data);
	    alert(re.info);
	    if(re.status){
	    	reloadGrid(gridId);
	    }
	});
	
	$('#pick_fail').click(function(){//不通过
		var list=getGridSelections(gridId),
		_buttons=[
	    	{text:'提交',click:function(){
	    		_data={pickNos:pickNos,opt:-1,hint:$('#hint').val()};
	    		re=ajax_json('pick/verifyPick',_data);
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
		var pickNos=[],_data;
		for(var i=0;i<list.length;i++){
			pickNos[i]=list[i].appNo;	
			}
	});
}

/**
 * 采购明细
 */
function purchaseInfo(rowData,type){
	var purNo=rowData.appNo,_title='',_button;
	var re = ajax_list('pur/getPurInfo?purNo='+purNo);
	var l=re.list.length,infoList=re.list,purList=[];
	if(rowData.status<1&&type==0){
		_title='修改采购明细';
		_button=[
	    	{text:'提交',click:function(){
	    		for(var i=0;i<l;i++){
	    			var purInfo={};
	    			purInfo.price=$('#purPrices_'+i+'').val();
	    			purInfo.number=$('#purNumbers_'+i+'').val();
	    			purInfo.sum=$('#purSums_'+i+'').val();
	    			purInfo.remark=$('#purRemarks_'+i+'').val();
	    			purInfo.optUser=$('#purBuyers_'+i+'').val();
	    			purInfo.content=$('#purContents_'+i+'').val();
	    			purInfo.id=infoList[i].id;
	    			purList.push(purInfo);
	    		}
		    	var _data={itemList:purList},
		    	re=ajax_json('pur/updatePurInfo',_data);
	    		alert(re.info);
		    	if(re.status==1){
			    	closeDialog('dialog');
		    	}
		    }}
	    ];
	}
	else _title='查看采购明细';
	var infodialog={
			title:_title,
			width:900,
			height:600,
			modal:true,
			buttons:_button
		},
	labels=[{name:'名称'},{name:'型号'},{name:'单价'},{name:'数量'},{name:'单位'},{name:'小计'},{name:'用途'},{name:'采购人'},{name:'备注'}],
	infologId=createDialog("dialog",labels,infodialog);
	var content='';
	for(i=0;i<l;i++){
		content+=('<tr><td><input id="purNames_'+i+'" name="name" class="width_120" value="'+infoList[i].name+'" /></td><td><input id="purModels_'+i+'" name="model"  class="width_60" value="'+infoList[i].model+'" /></td><td><input id="purPrices_'+i+'" name="price" class="width_60" value="'+infoList[i].price+'"/></td><td><input id="purNumbers_'+i+'" name="number" class="width_60" value="'+infoList[i].number+'" /></td><td><input id="purUnits_'+i+'" name="unit"  class="width_60" value="'+infoList[i].unit+'"/></td><td><input id="purSums_'+i+'" name="sum" class="width_60" value="'+infoList[i].sum+'"/></td><td><input id="purContents_'+i+'" name="content" class="width_90" value="'+infoList[i].content+'"/></td><td><input id="purBuyers_'+i+'" name="optUser" class="width_60"  value="'+infoList[i].optUser+'"/></td><td><input id="purRemarks_'+i+'" name="remark" class="width_90" value="'+infoList[i].remark+'"/></td></tr>');
	}	
	$('#'+infologId+"_table").append(content);
	$('input[name="name').attr('readonly','readonly');
	$('input[name="model').attr('readonly','readonly');
	$('input[name="unit').attr('readonly','readonly');
	if(rowData.status>=1||type==1){
		$('input[name="price').attr('readonly','readonly');
		$('input[name="number').attr('readonly','readonly');
		$('input[name="sum').attr('readonly','readonly');
		$('input[name="content').attr('readonly','readonly');
		$('input[name="optUser').attr('readonly','readonly');
		$('input[name="remark').attr('readonly','readonly');
	}
	$('input[name="price"]').omNumberField();
	$('input[name="number"]').omNumberField();
	$('input[name="sum"]').omNumberField();
	$('input[name="optUser"]').omCombo({
		dataSource:"com/getPurTaskerListCombo",
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