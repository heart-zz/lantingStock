package com.yjg.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.ioc.loader.annotation.Inject;

import com.yjg.AppService;
import com.yjg.utils.ServiceData;
/**
 * 文件下载Servlet<br/>
 * request里面存储文件记录ID
 * @author yan
 *
 */
public class FileDownload extends HttpServlet{

	/**
	 * 默认编号
	 */
	private static final long serialVersionUID = 1L;
	
	private static Dao dao;
	
	@Inject
	private static AppService service;
	
	/**
	 * 获取一个dao实例
	 * @return
	 * @throws ClassNotFoundException
	 */
	private void getDao() throws ClassNotFoundException{
		if(service==null)
			service=new AppService();
		SimpleDataSource ds=new SimpleDataSource();
		ds.setDriverClassName(service.getParamsProperty("db_driver"));
		ds.setJdbcUrl(service.getParamsProperty("db_url"));
		ds.setUsername(service.getParamsProperty("db_user"));
		ds.setPassword(service.getParamsProperty("db_password"));
		dao=new NutDao(ServiceData.minaDDS);			
	}
	/**
	 * 
	 */
	@Override
	public void service(HttpServletRequest req,HttpServletResponse res) throws IOException{		
		String filepath=new String(req.getParameter("filepath"));
		String filename=new String(req.getParameter("filename"));		
		File file=new File(filepath);
			if(!file.exists()){
				res.setContentType("text/html;charset=UTF-8");
				res.getWriter().print("指定文件不存在");
				return;
			}
			else{
				ServletOutputStream out=res.getOutputStream();
				String out_filename=null;
				String agent=req.getHeader("User-Agent");
				if(agent==null)
					agent=req.getHeader("user-agent");
				if(agent==null)
					agent=req.getHeader("User-agent");
				if(agent!=null && agent.contains("MSIE")){
					out_filename=URLEncoder.encode(filename, "UTF-8");
				}
				else
					out_filename=new String((filename).replaceAll(" ", "").getBytes("utf-8"),"iso8859-1");
				res.setContentType("multipart/form-data");
				res.setHeader("Content-disposition", "attachment;filename="+out_filename);
				res.addHeader("Content-Length", ""+file.length());
				BufferedInputStream bis=null;
				BufferedOutputStream bos=null;
				try{
					bis=new BufferedInputStream(new FileInputStream(filepath));
					bos=new BufferedOutputStream(out);
					byte[] buff=new byte[1024];
					int bytesRead;
					while(-1!=(bytesRead=bis.read(buff,0,buff.length))){
						bos.write(buff,0,bytesRead);
					}
				}catch(Exception e){
					e.printStackTrace();
					res.setContentType("text/html;charset=UTF-8");
					res.getWriter().print("文件已找到,下载失败");
					return;
				}finally{
					if(bis!=null)
						bis.close();
					if(bos!=null)
						bos.close();					
				}
			}		
		}
}
