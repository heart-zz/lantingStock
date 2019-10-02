var ioc = {
	dataSource : {        
        type:"com.alibaba.druid.pool.DruidDataSource",    
		events : {
                    depose : 'close'
            },
            fields : {
                    driverClassName : 'com.mysql.jdbc.Driver',            	    
                    url : 'jdbc:mysql://localhost:3306/lantingStock?useUnicode=true&characterEncoding=utf-8',            	   
                    initialSize:5,
                    maxIdle:5,
                    maxWait:15,
                    maxActive:50,
                    testWhileIdle:true,
                    username : 'root',                      
                    password : '123456'
            }
    },
    dao : {
    		type : "org.nutz.dao.impl.NutDao",
    		args : [{refer:'dataSource'}]
    },
    
    service:{
    	type:"com.yjg.AppService"
    },
    
    filePool:{//临时文件池
    	type:'org.nutz.filepool.NutFilePool',
    	args:['~/files/tmp',100]
    }
   
};