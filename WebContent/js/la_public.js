/*!
 * 系统公用方法-不做修改
 */
/**
 * 系统面板高度
 */
var PANEL_HEIGHT=0;
/**
 * 系统数据表格高度
 */
var GRID_HEIGHT=0;

/**
 * tabs数组
 */
var TAB_ARRAY=new Array();

/**
 * 当前用户权限列表
 */
var AUTH_LIST=[];

/**
 * 主页初始化
 */
function pageFrameInit(){
	 $('body').omBorderLayout({
     	   panels:[{
     	        id:"north-panel",
     	        region:"north",
     	        header : false,
     	        height : 70
     	    },{
     	        id:"center-panel",
     	        region:"center"
     	    },{
     	        id:"west-panel",
     	        resizable:true,
     	        collapsible:true,
     	        region:"west",
     	        expandToBottom : true,
     	        width:185
     	    }],
     	    hideCollapsBtn : true,
     	    spacing : 8
      });
      
      var menuPanel = [{id:"nav-panel-0" , title:"系统管理"},
                       {id:"nav-panel-2" , title:"人事管理"},
                       {id:"nav-panel-3" , title:"采购管理"},
                       {id:"nav-panel-5" , title:"领料管理"},
                       {id:"nav-panel-4" , title:"库存管理"},
                      ];
      $(menuPanel).each(function(index , panel){
          $("#"+panel.id).omPanel({
              title : panel.title,
              collapsible:true,
              collapsed:true,
              // 面板收缩和展开的时候重新计算自定义滚动条是否显示
              onCollapse : function(){
                  $("#west-panel").omScrollbar("refresh");
              },
              onExpand : function(){
                  $("#west-panel").omScrollbar("refresh");
              }
          });
      });

      // 初始化中间的tab页签

      $('#center-tab').omTabs({
      	height:"fit",
		border:false,
		closable:true,
		onClose:function(n,event){
			delTab('center-tab', $('#center-tab').omTabs('getActivated'));
		},
		onActivate:function(n,event){
		}
      	});
      removeMenuInvalid();
      
      $('#indextips').omPanel({
    	  iconCls:'icon1',
    	  title:'温馨提示',
    	  collapsible:true
      });

      $("#normalWork").omPanel({
    	  iconCls:'icon2',
          title:"常用办公",collapsible:true
        });
      
      $("#todayWork").omPanel({
    	iconCls:'icon3', 
      	title:"待处理任务<a href='#' onclick='getMsgFromServer();'>刷新</a>",collapsible:true
      });
      $("#news").omPanel({
        iconCls:'icon4',  
      	title:"消息墙",collapsible:true
      });
      // 导航面板使用自定义滚动条
      $("#west-panel").omScrollbar();
      
      /**定时从服务器读取信息**/
     // window.setInterval('getMsgFromServer()',120000);//ms
     
      
      if ($.browser.msie){
  		if($.browser.version<8)
  			alert('该版本浏览器可能与系统不完全兼容,请升级或更换');
  	  }     
      PANEL_HEIGHT=document.body.clientHeight-150;
  	  var _gridHeight=PANEL_HEIGHT*0.9;
  	  GRID_HEIGHT=PANEL_HEIGHT-50;
  	  if(_gridHeight>GRID_HEIGHT)
  		GRID_HEIGHT=_gridHeight;          
}

/**
 * 移除权限外的菜单
 * 	有父权限则不用传子权限，如移除1000，则不用移除1001
 */
function removeMenuInvalid()
{
	var re = ajax_list("sys/getAuthUnauthed");
	if(re.status == 1){
		unauth_list = re.list;
		if(typeof(unauth_list)=="undefined" || unauth_list.length<=0)
			return;
		for(var i=0;i<unauth_list.length;i++){
			$('#'+unauth_list[i].authCode).remove();
		}
//		unauth_list.forEach(function(element){
//			$('#'+element).remove();
//		});
	}else{
		//获取失败
	}
}
/**
 * 根据权限设置是否可见
 * @param list 待处理的含id字段的组件列表
 * @param type 0=需要分割线，1=不需要分割线
 * @return type=1时被删除id的数组,type=0时 新的数组
 */
function createUnitShow(list,type){
	type=type || 0;
	var _list=[],_id,_sep={separtor:true},list2=[];
	for(var i=0;i<list.length;i++){
		_id=list[i].id || '';
		if(_id=='')
			continue;
		if(!checkUserAuth(_id)){
			_list.push(list[i].id);			
			list.splice(i,1);
			i=i-1;
		}
	}
	if(type==0){
		for(var i=0;i<list.length;i++){
			list2.push(list[i],_sep);
		}
		list=list2;
		return list;
	}	
	return _list;
}

/**
 * 获取首页数据
 */
function getIndexInfo(){	
	var re=ajax_list('index/getIndexInfo');
	if(re.status==status_success){		
		loadTodayWorks(re.list[1]);
	}
	else{//服务器非正确响应
		if(re.info=='没有权限访问'){//登录信息失效
			loginPage();
		}
	}
}

/**
 * 登录
 */
function loginPage(){
	var labels=[{name:'用户名'},{name:'密码'}],
	inputs=[{id:'lp_username',value:LOGIN_USER.username},{id:'lp_pass',type:'password'}],
	dialog={
			title:'重新登录',
			width:500,
			height:300,
			modal:true,
			buttons:[{text:'登录',click:function(){
				var _pass=$('#lp_pass').val(),_username=encodeURI($('#lp_username').val());
				_pass=encodeMD5(_pass);
				var re=ajax_list('sys/loginWeb2?username='+_username+'&password='+_pass);
				alert(re.info);
				if(re.status==status_success){
					closeDialog('dialog');
				}
			}}]
	},
	formId=createFormDialog('dialog', labels, inputs, dialog, 1, '重新登录');
}

/*
 * 载入待处理事项
 */
function loadTodayWorks(list){	
	var content='';
	if(!isEmpty(list)){
		for(var i=0;i<list.length;i++)
			content+=list[i]+'<br/>';
		$('#todayWorks').html(content);
	}	
}

/**
 * 离开系统
 */
function exit(){
	closeAllThread();
	window.location="exit";
}

/**
 * 检查用户是否有权限,有返回 true,没有返回false
 * @param authCode 权限代码
 * @returns {Boolean}
 */
function checkUserAuth(authCode){
	for(var i=0;i<AUTH_LIST.length;i++){
		if(AUTH_LIST[i]==authCode){
			return true;
		}
	}
	return false;
}

/**
 * 清除所有线程
 */
function closeAllThread(){
	for(var i=0;i<THREAD_IDs.length;i++){
		clearInterval(THREAD_IDs[i]);		
	}
}

/**
 * 利用jquery $().val()功能赋值
 * @param divId 目标divId
 * @param value 值
 */
function setValue(divId,value){
	$('#'+inputId).val(value);
}

/**
 * 获取远程界面,将远程页面内容写入指定DIV
 * @param divId
 * @param pageUrl 远程页面URL
 * @param fn 处理成果的回调函数
 * @param param 回调函数参数
 */
function getPagerInfo(divId,pageUrl,fn,param){
	fn=fn || null;
	param=param || null;
	$('#'+divId).load(pageUrl,function(response,status,xhr){
		if(fn!=null)
			fn(param);
	});	
}

/**
 * 增加新tab
 * @param tabFatherId 父tab ID 默认为'center-tab'
 * @param titleString 标题名称
 * @param tabId 新tab id
 * @returns 新tab内容主体id
 */
function addTab(tabFatherId,titleString,tabId){	
	tabFatherId=tabFatherId || 'center-tab';	
	for(var i=0;i<TAB_ARRAY.length;i++){
		if(TAB_ARRAY[i]==tabId){//该tab已创建				
			$('#'+tabFatherId).omTabs({active:tabId});//激活此tab
			return tabId;
		}
	}
		
	$('#'+tabFatherId).omTabs('add',{
		title:titleString,
		tabId:tabId,
		content:'<div id="'+tabId+'"></div>'
	});
	TAB_ARRAY.push(tabId);
	return tabId;
}

/**
 * 删除tab
 * @param tabId
 */
function delTab(tabId){
	for(var i=0;i<TAB_ARRAY.length;i++){
		if(TAB_ARRAY[i]==tabId){//该tab已创建
			TAB_ARRAY[i]='null';
			return ;
		}
	}
}

/**
 * 弹出服务器无响应提示框
 */
function serverIsBad(){
	$.omMessageBox.alert({
		title:'警告',
		type:'error',
		content:'远程服务器无响应'
	});
}

/**
 * ajax 方式提交请求,并返回后台处理信息
 * @param url 请求路径,不带参数
 * @param param 参数集合.格式[{name:'',value:''},...]
 * @returns 服务器处理结果
 */
function ajax_param(url,param){
	param=param || [];
	var url_param='?';
	for(var i=0;i<param.length;i++){
		url_param+=param[i].name+'='+param[i].value+'&';
	}
	return eval("("+$.ajax({
		url:url+url_param,
		type:'post',
		async:false,		
		error:function(emsg){
			serverIsBad();
			closeAllThread();			
		}
	}).responseText+")");
}

/**
 * ajax方式提交请求，返回结果集
 * @param url 请求
 * @returns 服务器处理结果
 */
function ajax_http(url){
	return eval("("+$.ajax({
		url:url,
		type:'post',
		async:false,
		error:function(emsg){
			serverIsBad();
			closeAllThread();			
		}
	}).responseText+")");
}

/**
 * ajax方式提交请求，返回结果集
 * @param url 请求
 * @returns 服务器处理结果
 */
function ajax_list(url){
	return eval("("+$.ajax({
		url:url,
		type:'post',
		async:false,
		error:function(emsg){
			serverIsBad();
			closeAllThread();			
		}
	}).responseText+")");
}

/**
 * 提交json格式数据
 * @param _url 服务器路径
 * @param _data json格式数据
 * @returns 处理结果
 */
function ajax_json(_url,_data){
	return eval("("+$.ajax({
		url:_url,
		type:'post',
		contentType: "application/json;charset=utf-8",
		async:false,
		data: JSON.stringify(_data),		
        dataType: "json",
		error:function(emsg){
			serverIsBad();
			closeAllThread();			
		}
	}).responseText+")");
}

/**
 * ajax 方式提交表单
 * @param url 服务器路径
 * @param formId 表单id
 * @returns 服务器处理结果
 */
function ajax_form(_url,formId){	
	return eval("("+$.ajax({
		url:_url,
		data:$('#'+formId).serialize(),
		type:'post',
		async:false,
		error:function(emsg){
			serverIsBad();	
			closeAllThread();			
		}
	}).responseText+")");
}

/**
 * 从服务器获取js文件
 * @param _url js路径
 * @param _fn_success 载入成功调用方法
 */
function ajax_script(_url,_fn_success){
	$.ajax({
		 url: _url,
		 type: "GET",
		 async:false,
		 cache:true,
		 success: _fn_success,
		 error:function(XMLHttpRequest, textStatus, errorThrown){			
			 alert(textStatus);
		 },
		 dataType: "script"
		});
}

/**
 * 动态生成表单,采用默认内置样式,该表单所有的控件均为input<br/>
 * lables默认样式:leftlabel,input默认样式:leftinput
 * @param lables 存储字段名称,有name,style 属性
 * @param inputs 存储控件input各项属性,有id,name,style,value,type.如果type=textarea,则该标签卫textarea,同时应该会有cols和rows属性 
 * @param colNum 每行的列数,默认为2
 * @param formId 生产的表单id
 * @param title 若不为空 会生成表头
 * @returns 生成的表单html代码(表格ID=formId_table)
 */
function createForm(labels,inputs,colNum,formId,title){
	formId=formId || '';
	title=title || '';
	labels=labels || [];
	inputs=inputs || [];
	colNum=colNum || 2;
	var css_label='leftlabel',css_input='leftinput',defaultValue='';
	var content;
	if(title=='')
		content='<form id="'+formId+'"><table class="myTable" id="'+formId+'_table">';
	else
		content='<form id="'+formId+'"><table class="myTable" id="'+formId+'_table"><tr><th colspan='+colNum+'>'+title+'</th></tr>';
	var i=0,j=1;
	var length=labels.length;
	if(length>0){
		while(i<length){
			if(labels[i]==null)//ie8下出现的一个bug,计算数组长度出错,多一个
				break;
			if(j==1)
				content+='<tr>';
			if(inputs[i].type=='textarea'){
				content+='<td> <lable id="'+(labels[i].id || defaultValue)+'" class="'+(labels[i].style+" "+css_label || css_label)+'">'+(labels[i].name || defaultValue)+':</lable><textarea id="'
				+(inputs[i].id || defaultValue)+'" name="'+(inputs[i].name || defaultValue)+'" class="'
				+(inputs[i].style || css_input)+'" cols="'+(inputs[i].cols || 25)+'"  rows="'+(inputs[i].rows || 2)+'">'+(inputs[i].value || "")+' </textarea></td>';
			}
			else if(inputs[i].type=='button'){
				content+='<td><lable id="'+(labels[i].id || defaultValue)+'" class="'+(labels[i].style+" "+css_label || css_label)+'">'+(labels[i].name || defaultValue)+':</lable><button type="button" id="'
				+(inputs[i].id || defaultValue)+'" name="'+(inputs[i].name || defaultValue)+'" class="'
				+(inputs[i].style || css_input)+'">'+(inputs[i].value || '按钮名称')+'</button></td>';
			}
			else{
				content+='<td><lable id="'+(labels[i].id || defaultValue)+'" class="'+(labels[i].style+" "+css_label || css_label)+'">'+(labels[i].name || defaultValue)+':</lable><input id="'
				+(inputs[i].id || defaultValue)+'" name="'+(inputs[i].name || defaultValue)+'" class="'
				+(inputs[i].style || css_input)+'" value="'+(inputs[i].value || defaultValue)+'" type="'+
				(inputs[i].type || 'text')+'" /></td>';
			}
			
			j++;
			if(j>colNum){
				content+='</tr>';
				j=1;
			}
			i++;
		}
	}
	content+='</table></form>';
	return content;
}

/**
 * 动态生成表单,采用默认内置样式,该表单所有的控件均为input<br/>
 * lables默认样式:leftlabel,input默认样式:leftinput
 * @param lables 存储字段名称,有name,style 属性
 * @param inputs 存储控件input各项属性,有id,name,style,value,type.如果type=textarea,则该标签卫textarea,同时应该会有cols和rows属性 
 * @param colNum 每行的列数,默认为2
 * @param title 若不为空 会生成表头
 * @returns 生成的表单html代码(没有formId,没有table标签)
 */
function createFormContent(labels,inputs,colNum,title){
	title=title || '';
	labels=labels || [];
	inputs=inputs || [];
	colNum=colNum || 2;
	var css_label='leftlabel',css_input='leftinput',defaultValue='';
	var content='';
	if(!isEmpty(title))
		content='<tr><th colspan='+colNum+'>'+title+'</th></tr>';	
	var i=0,j=1;
	var length=labels.length;
	if(length>0){
		while(i<length){
			if(labels[i]==null)//ie8下出现的一个bug,计算数组长度出错,多一个
				break;
			if(j==1)
				content+='<tr>';
			if(inputs[i].type=='textarea'){
				content+='<td> <lable class="'+(labels[i].style+" "+css_label || css_label)+'">'+(labels[i].name || defaultValue)+':</lable><textarea id="'
				+(inputs[i].id || defaultValue)+'" name="'+(inputs[i].name || defaultValue)+'" class="'
				+(inputs[i].style || css_input)+'" cols="'+(inputs[i].cols || 25)+'"  rows="'+(inputs[i].rows || 2)+'">'+(inputs[i].value || "")+' </textarea></td>';
			}
			else{
				content+='<td><lable class="'+(labels[i].style+" "+css_label || css_label)+'">'+(labels[i].name || defaultValue)+':</lable><input id="'
				+(inputs[i].id || defaultValue)+'" name="'+(inputs[i].name || defaultValue)+'" class="'
				+(inputs[i].style || css_input)+'" value="'+(inputs[i].value || defaultValue)+'" type="'+
				(inputs[i].type || 'text')+'" /></td>';
			}
			
			j++;
			if(j>colNum){
				content+='</tr>';
				j=1;
			}
			i++;
		}
	}	
	return content;
}

/**
 * 动态生成表单，以对话框的形式展示
 * @param divId 目标DIV id
 * @param labels 存储字段名称,有name,style 属性
 * @param inputs 存储控件input各项属性,有id,name,style,value
 * @param dialog 存储dialog属性,title,width(默认620px),modal(默认false),resizable(默认true),buttons
 * @param colNum 表单列数 默认为2
 * @returns formId 表单DIV id
 */
function createFormDialog(divId,labels,inputs,dialog,colNum,title){
	divId=divId || 'dialog';
	title=title || '';
	var formId='form_'+(new Date).getMilliseconds();
	if(divId=='')
		divId='dialog';
	colNum=colNum || 2;
	$('#'+divId).html(createForm(labels,inputs,colNum,formId,title));
	$('#'+divId).omDialog({
		title:dialog.title,
		height:dialog.height || 500,
		width:dialog.width || 700,
		modal:dialog.modal || true,
		resizable: dialog.resizable || true,
        buttons :dialog.buttons || []
	});
	return formId;
}

function createDialog(divId,labels,dialog){
	var formId='form_'+(new Date).getMilliseconds(),length=labels.length,i=0;
	var content='<form id="'+formId+'"><table class="myTable" id="'+formId+'_table"><tr>';
	while(i<length){
		content+='<th>'+labels[i].name+'</th>';
		i++;
	}
	content+="</tr></table></form>";
	$('#'+divId).html(content);
	$('#'+divId).omDialog({
		title:dialog.title,
		height:dialog.height || 500,
		width:dialog.width || 700,
		modal:dialog.modal || true,
		resizable: dialog.resizable || true,
        buttons :dialog.buttons || []
	});
	return formId;
}

/**
 * 创建标准tab内容,包括一个buttonsbar和一个grid
 * @param divId 目标DIV id,默认 center-tab
 * @param btnsbar buttonsbar控件 按钮内容,包括id,label属性,可以在两个按钮中间添加分割线{separtor:true}
 * @param grid grid控件,包括 title,height(默认500px),singleSelect(默认false),dataSource(服务器端action url),
 * colModel(数据显示模板[{header(标题标签),name(服务器端传回字段属性名称),width,editor(editable默认false,type数据类型,options其他选项),renderer(针对数据处理,传入一个函数方法,function(colValue,rowData,rowIndex))}]),
 * 具体可以参见om-operamaks手册关于colModel说明
 * @returns gird divId
 */
function createTabContent(divId,btnsbar,grid){
	divId=divId || 'center-tab';
	var barId=divId+"_bar";
	var gridId=divId+"_grid";
	$('#'+divId).html('<div id="'+barId+'"></div><div id="'+gridId+'"></div>');
	if(btnsbar)
		$('#'+barId).omButtonbar({
			width:'100%',
			btns:btnsbar
		});
	if(grid){
		
		$('#'+gridId).omGrid({
			title:grid.title || '未知标题',
			dataSource:grid.dataSource,
			height:grid.height || PANEL_HEIGHT*0.9,
			singleSelect:grid.singleSelect==false?false:true,//若该元素值不存在，默认为true
			colModel:grid.colModel,
			onRowDblClick:grid.onRowDblClick || null,
			onRowClick:grid.onRowClick || null,
			onSuccess:grid.onSuccess || null
		});
	}		
	return gridId;
	}


/**
 * 采用默认样式生成表格
 * @param divId 目标DIV
 * @param data 数据,格式[[],[],...],按行记录
 * @param cols 
 * @param title 标题
 */
function createTable(divId,data,cols,title){
	divId=divId || 'dialog';
	data=data || [];
	cols=cols || 1;
	title=title ||'';
	if(data==[] || data==null){
		alert('数据为空,无法生成表格');
		return;
	}
	var content='<h2>'+title+'</h2><table class="myTable">';
	var row;
	for(var i=0;i<data.length;i++){
		row=data[i] || [];
		content+='<tr>';
		for(var j=0;j<row.length;j++){
			content+='<td>'+row[j]+'</td>';
		}
		content+='</tr>';
	}
	content+='</table>';
	$('#'+divId).html(content);
}

/**
 * 返回YYYY-MM-DD 格式的日期字符串,
 * @param dateString 格式 YYYY-MM-DD HH:MM:SS
 */
function getDateYMD(dateString){
	if(isEmpty(dateString))
		return null;	
	return dateString.split(" ")[0];
}

/**
 * 关闭对话框
 * @param divId 对话框divId,默认为dialog
 */
function closeDialog(divId){
	divId=divId || 'dialog';
	$('#'+divId).omDialog('close');
}

/**
 * 清空div内容,利用jquery $().val('')
 * @param divId
 */
function clearValue(divId){
	divId=divId || '';
	$('#'+divId).val('');
}


/**
 * 生成等待提示页面
 */
function waitingPage(wait_title,wait_content,wait_time){
	wait_time=wait_time || 2500;
	$.omMessageBox.waiting({
        title:wait_title,
        content:wait_content
    });
    setTimeout("$.omMessageBox.waiting('close');",wait_time);
}

/**
 * 定时从服务器处理信息
 * @param seconds 定时时间 秒,默认60秒
 */
function getPersonalInfoFromServer(seconds,method){
	method=method || 'getMsgFromServer()';
	seconds=seconds*1000 || 60000;
	return setInterval(method,seconds);  
}


/**
 * 检查输入变量是否为空
 * @param input 输入变量
 * @returns {Boolean}
 */
function isEmpty(input){
	input=input || '';
	if(input==null || input=='' || input.length==0){
		return true;
	}
}

/**
 * 为menu 绑定事件
 * @param menudiv menu超链接divId
 * @param menucontent menu内容divId
 */
function menuFunction(menudiv,menucontent){
	$('#'+menudiv).click(function(){//点击事件
		$('#'+menucontent).omMenu('show',this);
	});
}

/**
 * 全选/取消全选
 * @param allCheck
 * @param checks
 */
function checkAll(allCheck,checks){
	checks=checks || '';
	var ifChecked=$('input[name="'+allCheck+'"]').attr('checked');
	if(ifChecked=='checked')
		$('input[name="'+checks+'"]').each(function(){
			$(this).attr('checked',true);
		});
	else{
		$('input[name="'+checks+'"]').each(function(){
			$(this).attr('checked',false);
		});
	}
}


/**
 * 返回grid选择记录
 * @param gridId grid divId
 * @returns
 */
function getGridSelections(gridId){
	gridId=gridId || '';
	var records=$('#'+gridId).omGrid('getSelections',true);
	return records;
}

/**
 * grid数据刷新
 * @param gridId
 */
function reloadGrid(gridId){
	gridId=gridId || '';
	$('#'+gridId).omGrid('reload');
}

/**
 * 检查必填项是否为完整，若有空,返回false.
 * @param inputs,传入的input ids,格式[id1,id2,...];
 * @returns {Boolean}
 */
function isInputNeeded(inputs){
	inputs=inputs || [];
	for(var i=0;i<inputs.length;i++){
		var inputVal=getElemValue(inputs[i]);
		if(isEmpty(inputVal)){
			return false;
		}
	}
	return true;
}

/**
 * 自定义提示框
 * @param info 提示信息
 * @param _type 提示类型 默认为 alert[success,error,question,warning]
 * @param onCloseFn function(value){} 关闭对话框时回调函数
 */
function myAlert(info,_type,onCloseFn){
	_type=_type || 'alert';
	$.omMessageBox.alert({
        type:_type,
        title:'提示',
        content:info ,
        onClose:onCloseFn
    });
}

/**
 * 获取元素值,对jqeury val()封装
 * @param id 元素ID
 * @returns
 */
function getElemValue(id){
	return $('#'+id).val();
}

/**
 * 获取元素值,进行URI编码
 * @param id 元素ID
 * @returns
 */
function getElemValueURI(id){
	return encodeURI($('#'+id).val());
}

/**
 * 动态生成 数据模型
 * @param _header 标签名数组
 * @param _name 参数名数组
 * @param _width 对应宽度
 * @param _renderer 处理函数function(value , rowData , rowIndex){},如果没有函数,传入数字0
 * @param _align 文字对齐
 */
function createModel(_header,_name,_width,_renderer,_align){
	_header=_header || [];
	_name=_name || [];
	_width=_width || [];
	_renderer=_renderer || [];
	_align=_align || [];
	var model=[],hasRenderer=false;
	for(var i=0;i<_header.length;i++){
		if(_renderer[i]==0)
			model.push({header:_header[i],name:_name[i],width:_width[i],align: (_align[i] || 'center')});
		else
			model.push({header:_header[i],name:_name[i],width:_width[i],align: (_align[i] || 'center'),renderer:_renderer[i]});
	}
	return model;
}

/**
 * 动态生成 数据模型
 * @param _header 标签名数组
 * @param _name 参数名数组
 * @param _width 对应宽度
 * @param _renderer 处理函数function(value , rowData , rowIndex){},如果没有函数,传入数字0
 * @param _editor 是否可编辑
 */
function createModel2(_header,_name,_width,_renderer,_editor){//可编辑
	_header=_header || [];
	_name=_name || [];
	_width=_width || [];
	_renderer=_renderer || [];
	_editor=_editor || [];
	var model=[],hasRenderer=false;
	for(var i=0;i<_header.length;i++){
		if(_renderer[i]==0)
			model.push({header:_header[i],name:_name[i],width:_width[i],align:'center',editor:_editor[i]});
		else
			model.push({header:_header[i],name:_name[i],width:_width[i],align:'center',editor:_editor[i],renderer:_renderer[i]});
	}
	return model;
}

/**
 * 获取所选记录id字符串
 * @param gridId
 * @returns {String}
 */
function getGridSelectIds(gridId){
	var ids='',selects=getGridSelections(gridId);
	if(!isEmpty(selects)){
		for(var i=0;i<selects.length;i++)
			ids+=selects[i].id+',';
	}
	return ids;	
}

/**
 * 创建菜单
 * @param div_content 菜单内容 divId
 * @param div_title 菜单标题 divId
 * @param menudata 菜单数据[{id:_id,label:_label}]
 * @param fn 选择事件函数 function(item, event){}
 * @param divId 菜单所在divId,若divId不为空,则自动将div_content添加到divId中
 */
function createMenu(div_content,div_title,menudata,fn,divId){
	divId=divId || '';
	if(divId!='')
		$('#'+divId).append('<div id="'+div_content+'"></div>');
	$('#'+div_content).omMenu({
		dataSource:menudata,
		minWidth:150,
		maxWidth:350,
		onSelect: fn
	});
	menuFunction(div_title, div_content);
}


/**
 * 下载文件
 * @param filename 文件名
 * @param filepath 文件路径
 */
function downLoadFile(filename,filepath){
	window.location="fileDownload?filename="+filename+'&filepath='+filepath;
}

/**
 * 在线预览文件
 * @param dialogId
 * @param pf 项目文件类
 */
function readFile(dialogId,pf){
	dialogId=dialogId || 'dialgo2';
	//弹出提示
    $.omMessageBox.waiting({
        title:'请稍候',
        content:'服务器正在处理您的请求，请稍候...',
    });

	if(pf.fileExtension=='png' || pf.fileExtension=='jpg' || pf.fileExtension=='gif' || pf.fileExtension=='jpeg'){			
		var re=ajax_list('pro/readProFileImg?pfId='+pf.id);
		if(re.status==status_success){
			 //关闭提示
		    $.omMessageBox.waiting('close');
			$('#'+dialogId).html('<img src="'+re.obj+'" />');
			$('#'+dialogId).omDialog({
				title:pf.fileType+'在线预览[文件名='+pf.filename+']',
				modal:true,
				width:800,
				height:460
			});
		}
		else{
			 //关闭提示
		    $.omMessageBox.waiting('close');
			alert(re.info);
		}
	}
	else if(pf.fileExtension=='pdf'){
		var re=ajax_list('pro/readProFileImg?pfId='+pf.id);
		if(re.status==status_success){
			 //关闭提示
		    $.omMessageBox.waiting('close');
		    window.open(re.obj,'_blank');
		}
	}
	else if(pf.fileExtension=='doc' || pf.fileExtension=='docx'){							
		var re=ajax_list('pro/readDocOnline?pfId='+pf.id);
		if(re.status==status_success){	
			 //关闭提示
		    $.omMessageBox.waiting('close');
			window.open(re.info,'_blank');
		}
		else{
			 //关闭提示
		    $.omMessageBox.waiting('close');
			alert(re.info);
		}
	}	
	else{
		 //关闭提示
	    $.omMessageBox.waiting('close');
		alert(pf.fileExtension+'文件类型不支持在线预览');
	}
}

/**
 * 追加input到表单里
 * @param inputs 输入框数组，每个元素为{name:_name,id:_id,type=_type,value=_value},只接受input
 * @param formId 表单id
 */
function appendInputToForm(inputs,formId){
	inputs=inputs || [];
	formId=formId || '';
	if(isEmpty(formId)){
		alert('参数出错');
		return;
	}
	var content='';
	for(var i=0;i<inputs.length;i++){
		content+='<input id="'+inputs[i].id+'" name="'+inputs[i].name+'" type="'+inputs[i].type+'" value="'+inputs[i].value+'" />';
	}
	$('#'+formId).append(content);
}

/**
 * 删除表格指定行
 * @param table  表格id
 * @param trIndex 行的索引号
 */
function delTableTr(tableId,trIndex){
	var trs=($('#'+tableId)[0].childNodes)[0].childNodes;	
	if(trIndex<trs.length)
		trs[trIndex].parentNode.removeChild(trs[trIndex]);
}

/**
 * 删除指定id的行
 * @param trId 行id
 */
function delTableTr(trId){
	var tr=$('#'+trId)[0];
	tr.parentNode.removeChild(tr);
}

/**
 * 向表格追加行
 * @param tableId 表格id
 * @param trArray 行数组 [{name:_name,style:_style,value:_value,id_id}]
 * @param number 本次追加行数
 */
function addTableTr(tableId,trArray,number){
	number=number || 1;
	var content='',tdContent='';
	for(var i=0;i<number;i++){
		tdContent='';
		for(var j=0;j<trArray.length;j++){
			var _td=trArray[j];
			tdContent+='<td><input id="'+_td.id+'" name="'+_td.name+'" class="'+(_td.style || "width_90")+'" value="'+(_td.value || "")+'" /></td>';
		}
			
		content+='<tr>'+tdContent+'</tr>';
	}
	$('#'+tableId).append(content);
}

/**
 * 查看我的权限
 */
function getMyAuthList(){
	var _header,_name,_width,_colModel,_dataSource,_title,grid;
	$('#myAuthList').html('');
	$('#myAuthList').omDialog({
//		title:"我的账号权限"
	});	
	
	$('#myAuthList').append('<div id="data"></div>');
		_header=['权限名称'],_name=['auth'],_width=[200],
		_colModel=createModel(_header, _name, _width),
		_dataSource='auth/getMyAuthList',
		_title='权限列表',
	    grid={
	    		title:_title,
	    		colModel:_colModel,
	    		dataSource:_dataSource,
	    		height:PANEL_HEIGHT*0.7,
	    };
	 $('#data').omGrid(grid);
	
}

/**
 * 明文MD5加密
 * @param data
 * @returns
 */
function encodeMD5(data) {

	   // convert number to (unsigned) 32 bit hex, zero filled string
	   function to_zerofilled_hex(n) {     
	       var t1 = (n >>> 0).toString(16)
	       return "00000000".substr(0, 8 - t1.length) + t1
	   }

	   // convert array of chars to array of bytes 
	   function chars_to_bytes(ac) {
	       var retval = []
	       for (var i = 0; i < ac.length; i++) {
	           retval = retval.concat(str_to_bytes(ac[i]))
	       }
	       return retval
	   }


	   // convert a 64 bit unsigned number to array of bytes. Little endian
	   function int64_to_bytes(num) {
	       var retval = []
	       for (var i = 0; i < 8; i++) {
	           retval.push(num & 0xFF)
	           num = num >>> 8
	       }
	       return retval
	   }

	   //  32 bit left-rotation
	   function rol(num, places) {
	       return ((num << places) & 0xFFFFFFFF) | (num >>> (32 - places))
	   }

	   // The 4 MD5 functions
	   function fF(b, c, d) {
	       return (b & c) | (~b & d)
	   }

	   function fG(b, c, d) {
	       return (d & b) | (~d & c)
	   }

	   function fH(b, c, d) {
	       return b ^ c ^ d
	   }

	   function fI(b, c, d) {
	       return c ^ (b | ~d)
	   }

	   // pick 4 bytes at specified offset. Little-endian is assumed
	   function bytes_to_int32(arr, off) {
	       return (arr[off + 3] << 24) | (arr[off + 2] << 16) | (arr[off + 1] << 8) | (arr[off])
	   }

	   /*
	   Conver string to array of bytes in UTF-8 encoding
	   See: 
	   http://www.dangrossman.info/2007/05/25/handling-utf-8-in-javascript-php-and-non-utf8-databases/
	   http://stackoverflow.com/questions/1240408/reading-bytes-from-a-javascript-string
	   How about a String.getBytes(<ENCODING>) for Javascript!? Isn't it time to add it?
	   */
	   function str_to_bytes(str) {
	       var retval = [ ]
	       for (var i = 0; i < str.length; i++)
	           if (str.charCodeAt(i) <= 0x7F) {
	               retval.push(str.charCodeAt(i))
	           } else {
	               var tmp = encodeURIComponent(str.charAt(i)).substr(1).split('%')
	               for (var j = 0; j < tmp.length; j++) {
	                   retval.push(parseInt(tmp[j], 0x10))
	               }
	           }
	       return retval
	   }


	   // convert the 4 32-bit buffers to a 128 bit hex string. (Little-endian is assumed)
	   function int128le_to_hex(a, b, c, d) {
	       var ra = ""
	       var t = 0
	       var ta = 0
	       for (var i = 3; i >= 0; i--) {
	           ta = arguments[i]
	           t = (ta & 0xFF)
	           ta = ta >>> 8
	           t = t << 8
	           t = t | (ta & 0xFF)
	           ta = ta >>> 8
	           t = t << 8
	           t = t | (ta & 0xFF)
	           ta = ta >>> 8
	           t = t << 8
	           t = t | ta
	           ra = ra + to_zerofilled_hex(t)
	       }
	       return ra
	   }

	   // conversion from typed byte array to plain javascript array 
	   function typed_to_plain(tarr) {
	       var retval = new Array(tarr.length)
	       for (var i = 0; i < tarr.length; i++) {
	           retval[i] = tarr[i]
	       }
	       return retval
	   }

	   // check input data type and perform conversions if needed
	   var databytes = null
	   // String
	   var type_mismatch = null
	   if (typeof data == 'string') {
	       // convert string to array bytes
	       databytes = str_to_bytes(data)
	   } else if (data.constructor == Array) {
	       if (data.length === 0) {
	           // if it's empty, just assume array of bytes
	           databytes = data
	       } else if (typeof data[0] == 'string') {
	           databytes = chars_to_bytes(data)
	       } else if (typeof data[0] == 'number') {
	           databytes = data
	       } else {
	           type_mismatch = typeof data[0]
	       }
	   } else if (typeof ArrayBuffer != 'undefined') {
	       if (data instanceof ArrayBuffer) {
	           databytes = typed_to_plain(new Uint8Array(data))
	       } else if ((data instanceof Uint8Array) || (data instanceof Int8Array)) {
	           databytes = typed_to_plain(data)
	       } else if ((data instanceof Uint32Array) || (data instanceof Int32Array) || 
	              (data instanceof Uint16Array) || (data instanceof Int16Array) || 
	              (data instanceof Float32Array) || (data instanceof Float64Array)
	        ) {
	           databytes = typed_to_plain(new Uint8Array(data.buffer))
	       } else {
	           type_mismatch = typeof data
	       }   
	   } else {
	       type_mismatch = typeof data
	   }

	   if (type_mismatch) {
	       alert('MD5 type mismatch, cannot process ' + type_mismatch)
	   }

	   function _add(n1, n2) {
	       return 0x0FFFFFFFF & (n1 + n2)
	   }


	   return do_digest()

	   function do_digest() {

	       // function update partial state for each run
	       function updateRun(nf, sin32, dw32, b32) {
	           var temp = d
	           d = c
	           c = b
	           //b = b + rol(a + (nf + (sin32 + dw32)), b32)
	           b = _add(b, 
	               rol( 
	                   _add(a, 
	                       _add(nf, _add(sin32, dw32))
	                   ), b32
	               )
	           )
	           a = temp
	       }

	       // save original length
	       var org_len = databytes.length

	       // first append the "1" + 7x "0"
	       databytes.push(0x80)

	       // determine required amount of padding
	       var tail = databytes.length % 64
	       // no room for msg length?
	       if (tail > 56) {
	           // pad to next 512 bit block
	           for (var i = 0; i < (64 - tail); i++) {
	               databytes.push(0x0)
	           }
	           tail = databytes.length % 64
	       }
	       for (i = 0; i < (56 - tail); i++) {
	           databytes.push(0x0)
	       }
	       // message length in bits mod 512 should now be 448
	       // append 64 bit, little-endian original msg length (in *bits*!)
	       databytes = databytes.concat(int64_to_bytes(org_len * 8))

	       // initialize 4x32 bit state
	       var h0 = 0x67452301
	       var h1 = 0xEFCDAB89
	       var h2 = 0x98BADCFE
	       var h3 = 0x10325476

	       // temp buffers
	       var a = 0, b = 0, c = 0, d = 0

	       // Digest message
	       for (i = 0; i < databytes.length / 64; i++) {
	           // initialize run
	           a = h0
	           b = h1
	           c = h2
	           d = h3

	           var ptr = i * 64

	           // do 64 runs
	           updateRun(fF(b, c, d), 0xd76aa478, bytes_to_int32(databytes, ptr), 7)
	           updateRun(fF(b, c, d), 0xe8c7b756, bytes_to_int32(databytes, ptr + 4), 12)
	           updateRun(fF(b, c, d), 0x242070db, bytes_to_int32(databytes, ptr + 8), 17)
	           updateRun(fF(b, c, d), 0xc1bdceee, bytes_to_int32(databytes, ptr + 12), 22)
	           updateRun(fF(b, c, d), 0xf57c0faf, bytes_to_int32(databytes, ptr + 16), 7)
	           updateRun(fF(b, c, d), 0x4787c62a, bytes_to_int32(databytes, ptr + 20), 12)
	           updateRun(fF(b, c, d), 0xa8304613, bytes_to_int32(databytes, ptr + 24), 17)
	           updateRun(fF(b, c, d), 0xfd469501, bytes_to_int32(databytes, ptr + 28), 22)
	           updateRun(fF(b, c, d), 0x698098d8, bytes_to_int32(databytes, ptr + 32), 7)
	           updateRun(fF(b, c, d), 0x8b44f7af, bytes_to_int32(databytes, ptr + 36), 12)
	           updateRun(fF(b, c, d), 0xffff5bb1, bytes_to_int32(databytes, ptr + 40), 17)
	           updateRun(fF(b, c, d), 0x895cd7be, bytes_to_int32(databytes, ptr + 44), 22)
	           updateRun(fF(b, c, d), 0x6b901122, bytes_to_int32(databytes, ptr + 48), 7)
	           updateRun(fF(b, c, d), 0xfd987193, bytes_to_int32(databytes, ptr + 52), 12)
	           updateRun(fF(b, c, d), 0xa679438e, bytes_to_int32(databytes, ptr + 56), 17)
	           updateRun(fF(b, c, d), 0x49b40821, bytes_to_int32(databytes, ptr + 60), 22)
	           updateRun(fG(b, c, d), 0xf61e2562, bytes_to_int32(databytes, ptr + 4), 5)
	           updateRun(fG(b, c, d), 0xc040b340, bytes_to_int32(databytes, ptr + 24), 9)
	           updateRun(fG(b, c, d), 0x265e5a51, bytes_to_int32(databytes, ptr + 44), 14)
	           updateRun(fG(b, c, d), 0xe9b6c7aa, bytes_to_int32(databytes, ptr), 20)
	           updateRun(fG(b, c, d), 0xd62f105d, bytes_to_int32(databytes, ptr + 20), 5)
	           updateRun(fG(b, c, d), 0x2441453, bytes_to_int32(databytes, ptr + 40), 9)
	           updateRun(fG(b, c, d), 0xd8a1e681, bytes_to_int32(databytes, ptr + 60), 14)
	           updateRun(fG(b, c, d), 0xe7d3fbc8, bytes_to_int32(databytes, ptr + 16), 20)
	           updateRun(fG(b, c, d), 0x21e1cde6, bytes_to_int32(databytes, ptr + 36), 5)
	           updateRun(fG(b, c, d), 0xc33707d6, bytes_to_int32(databytes, ptr + 56), 9)
	           updateRun(fG(b, c, d), 0xf4d50d87, bytes_to_int32(databytes, ptr + 12), 14)
	           updateRun(fG(b, c, d), 0x455a14ed, bytes_to_int32(databytes, ptr + 32), 20)
	           updateRun(fG(b, c, d), 0xa9e3e905, bytes_to_int32(databytes, ptr + 52), 5)
	           updateRun(fG(b, c, d), 0xfcefa3f8, bytes_to_int32(databytes, ptr + 8), 9)
	           updateRun(fG(b, c, d), 0x676f02d9, bytes_to_int32(databytes, ptr + 28), 14)
	           updateRun(fG(b, c, d), 0x8d2a4c8a, bytes_to_int32(databytes, ptr + 48), 20)
	           updateRun(fH(b, c, d), 0xfffa3942, bytes_to_int32(databytes, ptr + 20), 4)
	           updateRun(fH(b, c, d), 0x8771f681, bytes_to_int32(databytes, ptr + 32), 11)
	           updateRun(fH(b, c, d), 0x6d9d6122, bytes_to_int32(databytes, ptr + 44), 16)
	           updateRun(fH(b, c, d), 0xfde5380c, bytes_to_int32(databytes, ptr + 56), 23)
	           updateRun(fH(b, c, d), 0xa4beea44, bytes_to_int32(databytes, ptr + 4), 4)
	           updateRun(fH(b, c, d), 0x4bdecfa9, bytes_to_int32(databytes, ptr + 16), 11)
	           updateRun(fH(b, c, d), 0xf6bb4b60, bytes_to_int32(databytes, ptr + 28), 16)
	           updateRun(fH(b, c, d), 0xbebfbc70, bytes_to_int32(databytes, ptr + 40), 23)
	           updateRun(fH(b, c, d), 0x289b7ec6, bytes_to_int32(databytes, ptr + 52), 4)
	           updateRun(fH(b, c, d), 0xeaa127fa, bytes_to_int32(databytes, ptr), 11)
	           updateRun(fH(b, c, d), 0xd4ef3085, bytes_to_int32(databytes, ptr + 12), 16)
	           updateRun(fH(b, c, d), 0x4881d05, bytes_to_int32(databytes, ptr + 24), 23)
	           updateRun(fH(b, c, d), 0xd9d4d039, bytes_to_int32(databytes, ptr + 36), 4)
	           updateRun(fH(b, c, d), 0xe6db99e5, bytes_to_int32(databytes, ptr + 48), 11)
	           updateRun(fH(b, c, d), 0x1fa27cf8, bytes_to_int32(databytes, ptr + 60), 16)
	           updateRun(fH(b, c, d), 0xc4ac5665, bytes_to_int32(databytes, ptr + 8), 23)
	           updateRun(fI(b, c, d), 0xf4292244, bytes_to_int32(databytes, ptr), 6)
	           updateRun(fI(b, c, d), 0x432aff97, bytes_to_int32(databytes, ptr + 28), 10)
	           updateRun(fI(b, c, d), 0xab9423a7, bytes_to_int32(databytes, ptr + 56), 15)
	           updateRun(fI(b, c, d), 0xfc93a039, bytes_to_int32(databytes, ptr + 20), 21)
	           updateRun(fI(b, c, d), 0x655b59c3, bytes_to_int32(databytes, ptr + 48), 6)
	           updateRun(fI(b, c, d), 0x8f0ccc92, bytes_to_int32(databytes, ptr + 12), 10)
	           updateRun(fI(b, c, d), 0xffeff47d, bytes_to_int32(databytes, ptr + 40), 15)
	           updateRun(fI(b, c, d), 0x85845dd1, bytes_to_int32(databytes, ptr + 4), 21)
	           updateRun(fI(b, c, d), 0x6fa87e4f, bytes_to_int32(databytes, ptr + 32), 6)
	           updateRun(fI(b, c, d), 0xfe2ce6e0, bytes_to_int32(databytes, ptr + 60), 10)
	           updateRun(fI(b, c, d), 0xa3014314, bytes_to_int32(databytes, ptr + 24), 15)
	           updateRun(fI(b, c, d), 0x4e0811a1, bytes_to_int32(databytes, ptr + 52), 21)
	           updateRun(fI(b, c, d), 0xf7537e82, bytes_to_int32(databytes, ptr + 16), 6)
	           updateRun(fI(b, c, d), 0xbd3af235, bytes_to_int32(databytes, ptr + 44), 10)
	           updateRun(fI(b, c, d), 0x2ad7d2bb, bytes_to_int32(databytes, ptr + 8), 15)
	           updateRun(fI(b, c, d), 0xeb86d391, bytes_to_int32(databytes, ptr + 36), 21)

	           // update buffers
	           h0 = _add(h0, a)
	           h1 = _add(h1, b)
	           h2 = _add(h2, c)
	           h3 = _add(h3, d)
	       }
	       // Done! Convert buffers to 128 bit (LE)
	       return int128le_to_hex(h3, h2, h1, h0).toUpperCase();
	   }   
}
