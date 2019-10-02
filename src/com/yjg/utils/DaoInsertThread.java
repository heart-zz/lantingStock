package com.yjg.utils;

import org.apache.log4j.Logger;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;

import com.alibaba.druid.pool.DruidDataSource;
import com.yjg.entity.SystemLog;

/**
 * 专门负责处理数据库插入线程
 * @author yjg
 *
 */
public class DaoInsertThread extends Thread{

	private boolean flag=true;
	private Logger logger=Logger.getLogger(DaoInsertThread.class);
	public void stopDaoInsertThread(){
		flag=false;
	}
	
	@Override
	public void run(){
		logger.info("Mina数据插入处理线程已启动");
		DruidDataSource dds=ServiceData.minaDDS;
		if(dds==null){
			logger.error("Mina数据插入处理线程失败:专用数据库连接池为空");
		}
		else{
			Dao dao=new NutDao(dds);
			while(flag){
				try {
					Object obj=ServiceData.datasWaitingInsert.poll();
					if(obj==null){
						Thread.sleep(500);
					}
					else{
						dao.fastInsert(obj);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//e.printStackTrace();
					logger.error("Mina数据插入线程出错:"+e.toString());
					SystemLog sl=new SystemLog(this.getClass().getName(),e.toString(),"run();","");
					dao.fastInsert(sl);
				}
			}
		}
		
		logger.info("Mina数据插入处理线程已停止");
	}
}
