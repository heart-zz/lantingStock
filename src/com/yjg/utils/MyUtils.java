package com.yjg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import com.yjg.entity.Product;
import com.yjg.entity.User;
/**
 * 项目辅助类
 * @author yjg
 *
 */
public class MyUtils {
	
	private static Logger logger=Logger.getLogger("yjg.utils.MyUtils");

	/**
	 * 默认文件大小 300 000 000 b
	 */
	private static int fileSize=300000000;
	
	
	/**
	 * 16进制每位字符
	 */
	private static char[] hexBytes={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	
	/**
	 * 对字符串进行MD5加密处理，返回密文
	 * @param input 原始字符串
	 * @return 加密字符串
	 */
	public static String changeStr(String input){
		String output="";
		try{
			MessageDigest md5=MessageDigest.getInstance("MD5");
			output=byte2hex2(md5.digest(input.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * 双重MDG加密
	 * @param input 明文
	 * @return 密文
	 */
	public static String dbChangeStr(String input){
		return changeStr(changeStr(input));
	}
	
	/**
	 * 将字节数组转成字符串(每个字节用空格区分)
	 * @param 字节数组
	 * @return 字符串
	 */
	public static String byte2hex(byte[] b)
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
		stmp = Integer.toHexString(b[n] & 0XFF);
		if (stmp.length() == 1)
		hs = hs + " 0" + stmp;
		else
		hs = hs +" "+stmp;
		}
	return hs.toUpperCase();
	}
	
	/**
	 * 将字节数组转成字符串(字节间没有空格)
	 * @param 字节数组
	 * @return 字符串
	 */
	public static String byte2hex2(byte[] b)
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
		stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
		if (stmp.length() == 1)
		hs = hs + "0" + stmp;
		else
		hs = hs +stmp;
		}
	return hs.toUpperCase();
	}
	
	/**
	 * 将文件存入指定路径，并返回文件名和保存路径,以,分割[注:文件真实名称将会重新设定]
	 * @param fileItem 文件
	 * @param filePath 文件保存路径(结尾带 /)
	 * @return {@link Result} re.obj= {@link FileSaveUtils}
	 */
	public static Result saveFile(FileItem fileItem,String filePath){
		Result re=new Result();
		String path = fileItem.getName();//获取文件的全路径名称
		long size = fileItem.getSize();
		if (null==path || "".equals(path) ) {
			re.setInfo("文件源路径为空");
			re.setStatus(Params.status_failed);
			return re;
		}
		else if(size<=0){
			re.setStatus(Params.status_failed);
			re.setInfo("文件大小为0");
			return re;
		}
		else{
			
			String t_name = path.substring(path.lastIndexOf("\\") + 1);//得到去除路径的文件名
			String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);//得到文件的扩展名(无扩展名时将得到全名)			
			String timestamp=String.valueOf(System.currentTimeMillis());//产生时间戳
			String u_name = filePath +timestamp + "." + t_ext;//保存的最终文件完整路径,保存在filepath路径下
			try {
				File filedir=new File(filePath);
				if(!filedir.exists()){//路径不存在,则建立
					filedir.mkdirs();
				}
				//保存文件
				fileItem.write(new File(u_name));
			} catch (Exception e) {
				e.printStackTrace();
				re.setStatus(Params.status_failed);
				re.setInfo("操作出错:"+e.getCause());
				return re;
			} 
			FileSaveUtils fsu=new FileSaveUtils();
			fsu.setFilepath(u_name);
			fsu.setFileSize_kb(size/1000);
			fsu.setFiletype(t_ext);
			fsu.setPreFileName(t_name);
			fsu.setNewFileName(timestamp + "." + t_ext);
			re.setObj(fsu);		
			re.setInfo("操作成功");
			re.setStatus(Params.status_success);			
			return re;
		}
	}
	
	/**
	 * 将String转成Date
	 * @param input 输入的String
	 * @return
	 */
	public static Date strToDate(String input){
		if(input==null || input.equals(""))
			return null;
		DateFormat df1=new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df2=new SimpleDateFormat("yyyy/MM/dd");
		DateFormat df3=new SimpleDateFormat("yyyy年MM月dd日");
		DateFormat df4=new SimpleDateFormat("yyyy.MM.dd");
		Date out=null;
		try{
			out=df1.parse(input);
		}catch(Exception e1){
			e1.printStackTrace();
			try{
				out=df2.parse(input);
			}catch(Exception e2){
				e2.printStackTrace();
				try{
					out=df3.parse(input);
				}catch(Exception e3){
					e3.printStackTrace();
					try{
						out=df4.parse(input);
					}catch(Exception e4){
						e4.printStackTrace();
						return null;
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyy-mm-dd hh:mm:ss
	 * @param date
	 * @return
	 */
	public static String DateToStr(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String DateToStrYMD(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyy年MM月dd日
	 * @param date
	 * @return
	 */
	public static String DateToStrYMD2(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyy年MM月dd日");
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyyMM
	 * @param date
	 * @return
	 */
	public static String DateToStrYMD3(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyyMM");
		return df.format(date);
	}
	
	/**
	 * 初始化文件工厂，为保存文件、获取表单数据做准备
	 * @param req 请求
	 * @param progressListener 是否设置上传进度信息
	 * @return 文件（表单）列表
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<FileItem> initFileFactory(HttpServletRequest req,boolean progressListener){
		DiskFileItemFactory dfif = new DiskFileItemFactory();// 实例化一个硬盘文件工厂,用来配置上传组件ServletFileUpload
		dfif.setSizeThreshold(1024 * 5);// 设置上传文件时用于临时存放文件的内存大小.多于的部分将临时存在硬盘
		ServletFileUpload sfu = new ServletFileUpload(dfif);//用以上工厂实例化上传组件
		sfu.setSizeMax(fileSize);//设置最大上传文件大小
		if(progressListener){
			final HttpSession session = req.getSession();
			sfu.setProgressListener(new ProgressListener() {
				private long temp = -1;
			    @Override
				public void update(long readBytes, long totalBytes, int item) {
					long size = readBytes / 1024 * 1024 * 10;
					if(temp == size){
						return;
					}
					temp = size;
					if (readBytes != -1) {
						session.setAttribute("readBytes", "" + readBytes);
					    session.setAttribute("totalBytes", "" + totalBytes);
					}
			    }
			});
		}
		List<FileItem> fileList = null;//从request得到 所有 上传域的列表
		try{
			fileList = sfu.parseRequest(req);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		if(fileList==null || fileList.size()<=0){
			return null;
		}
		return fileList.iterator();
	}
	
	/**
	 * 返回IP地址
	 * @param req
	 * @return
	 */
	public static String getIP(HttpServletRequest req){
		return req.getRemoteAddr();
	}
	
	/**
	 * 16进制字符串转对应字节数组
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString){
		if(hexString==null || "".equals(hexString)){
			return null;
		}
		hexString=hexString.toUpperCase();
		int length=hexString.length()/2;
		char[] hexchars=hexString.toCharArray();
		byte[] d=new byte[length];
		for(int i=0;i<length;i++){
			int pos=i*2;
		    d[i]=(byte)(charToByte(hexchars[pos])<< 4 | charToByte(hexchars[pos+1]));
		}
		return d;
	}
	
	/**
	 * char转byte
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c){
		return (byte)"0123456789ABCDEF".indexOf(c);
	}
	
	/**
	 * 检查用户是否登录
	 * @param acu {@link AppHttpUtils}
	 * @param session
	 */
	public static boolean checkRequestOk(AppHttpUtils acu,HttpSession session){
		User user=null;
		if(session!=null){
			 user=(User)session.getAttribute(Params.loginUserInSessionStr);
		}
		if(user==null && acu!=null){
			//user=acu.getUser();
			if(acu.getId()>0)
				return true;
		}
		if(user==null)
			return false;
		else
			return true;
	}
	
	/**
	 * 获取用户信息
	 * @param acu 
	 * @param session
	 * @return
	 */
	public static User getUserInfo(AppHttpUtils acu,HttpSession session){
		User user=null;
		if(session!=null){
			 user=(User)session.getAttribute(Params.loginUserInSessionStr);
		}
		if(user==null && acu!=null){
			user=acu.getUser();
		}
		return user;
			
	}
	
	/**
	 * 检查字符串对象是否为空
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input){
		if(null==input || "".equals(input))
			return true;
		return false;
	}
		
	
	
	/**
	 * 设置用户信息，屏蔽敏感信息
	 * @param list
	 * @return
	 */
	public static List<User> setUserInfo(List<User> list){
		for(User user:list){
			user.setPassword("********");		
		}
		return list;
	}
	
	/**
	 * 检查长整型列表内是否包含指定值
	 * @param list
	 * @param value
	 * @return 第一个相等值的下标。-1=不存在
	 */
	public static int containLong(List<Long> list,long value){
		for(int i=0;i<list.size();i++){
			if(list.get(i)==value)
				return i;
		}
		return -1;
	}
	
	/**
	 * 检查长整型列表内是否包含指定值
	 * @param list
	 * @param value
	 * @return 第一个相等值的下标。-1=不存在
	 */
	public static int containString(List<String> list,String value){
		for(int i=0;i<list.size();i++){
			if(value.equals(list.get(i)))
				return i;
		}
		return -1;
	}
	
	/**
	 * 拼接字符串[空字符串不拼接]
	 * @param strArray 已分割的字符串数组
	 * @param splitChar 分割符
	 * @return 拼接后的字符串
	 */
	public static String jointString(String[] strArray,String splitChar){
		StringBuffer sb=new StringBuffer();
		int lastIndex=strArray.length-1;
		for(int i=0;i<strArray.length;i++){
			if(!isEmpty(strArray[i])){
				sb.append(strArray[i]);
				if(i<lastIndex){
					sb.append(splitChar);
				}
			}			
		}
		return sb.toString();
	}
	
	private static int count_conPur=1;
	
	
	/**
	 * 返回1-9大写
	 * @param number
	 * @return
	 */
	public static String getNumberCN(int number){
		String re=null;
		if(number==1)
			re="壹";
		else if(number==2)
			re="贰";
		else if(number==3)
			re="叁";
		else if(number==4)
			re="肆";
		else if(number==5)
			re="伍";
		else if(number==6)
			re="陆";
		else if(number==7)
			re="柒";
		else if(number==8)
			re="捌";
		else if(number==9)
			re="玖";
		else
			re="";
		return re;
	}
	
	
	/**
	 * 发出https请求
	 * @param url 请求地址
	 * @param requestMethod 请求方式,若为NULL或空字符串，则默认为GET.可选：GET/POST/HEAD
	 * @return {@link String} 处理结果字符串
	 */
	public static String httpsRequest(String url,String requestMethod){
		String reStr="";
		HttpsURLConnection httpConn=null;
		BufferedReader in=null;
		if(isEmpty(requestMethod))
			requestMethod="GET";
		try {
			URL _url=new URL(url);
			httpConn=(HttpsURLConnection)_url.openConnection();
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod(requestMethod);
			httpConn.setConnectTimeout(30000);
			httpConn.setRequestProperty("Content-type", "application/json"); 
			
			in=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
			String line="";			
			while((line=in.readLine())!=null){				
				reStr+=line;
			}
		} catch (Exception e) {
			// TODO: handle exception	
			logger.error("处理HTTPS请求出错:"+e.toString());			
			reStr="{status:-1,info:'http请求出错'}";
		}
		finally{
			if(httpConn!=null)
				httpConn.disconnect();
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return reStr;
	}
	/**
	* @Title: checkProHad   
	* @Description: TODO(检测品名是否已存在)   
	* @param name
	* @param model
	* @param dao
	* @return boolean  true 已有     
	* @throws
	 */
	public static boolean checkProHad(String proNo,String companyNo,Dao dao) {
		if(dao.count(Product.class,Cnd.where("proNo","=", proNo).and("companyNo","=",companyNo))!=0)return true;
		else return false;
	}

}

