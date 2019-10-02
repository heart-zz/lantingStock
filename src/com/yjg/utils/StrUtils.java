package com.yjg.utils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class StrUtils {

	/**
	 * 默认数据页尺寸
	 */
	private static int pageSize=20;
	/**
	 * 默认文件大小 300 000 000 b
	 */
	private static int fileSize=300000000;
	
	/**
	 * 获取天气URL
	 */
	private static String weatherUrl="http://www.weather.com.cn/data/cityinfo/101210601.html";//台州城市编码:101210601
	
	/**
	 * 批量采购
	 */
	private static String stockInBatch="批量采购";
	
	private static String _609EntityUrl="com.imeOM.entity.";
	
	/**
	 * 对字符串进行MD5加密处理，返回密文
	 * @param input 原始字符串
	 * @return 加密字符串
	 */
	public static String changeStr(String input){
		String output="";
		try{
			MessageDigest md5=MessageDigest.getInstance("MD5");
			output=byte2hex(md5.digest(input.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * 将二进制数组转成字符串
	 * @param b 二进制数组
	 * @return 字符串
	 */
	public static String byte2hex(byte[] b)
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
		stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
		if (stmp.length() == 1)
		hs = hs + "0" + stmp;
		else
		hs = hs + stmp;
		}
	return hs.toUpperCase();
	}
	
	/**
	 * 将文件存入指定路径，并返回文件名和保存路径,以,分割
	 * @param fileItem 文件
	 * @param filePath 文件保存路径
	 * @return 文件名称+文件保存路径
	 */
	public static String saveFile(FileItem fileItem,String filePath){
		
		String path = fileItem.getName();//获取文件的全路径名称
		long size = fileItem.getSize();
		if ("".equals(path) ) {
			return "path_null";
		}
		else if(size<=0){
			return "size_null";
		}
		else{
			
			String t_name = path.substring(path.lastIndexOf("\\") + 1);//得到去除路径的文件名
			String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);//得到文件的扩展名(无扩展名时将得到全名)
			String fileName=t_name.substring(0, t_name.lastIndexOf("."));//获取去除扩展名的文件名
			String timestamp=String.valueOf(System.currentTimeMillis());//产生时间戳
			String u_name = filePath +fileName+timestamp + "." + t_ext;//保存的最终文件完整路径,保存在filepath路径下
			try {
				//保存文件
				fileItem.write(new File(u_name));
			} catch (Exception e) {
				e.printStackTrace();
				return "save_error";
			} 
			return t_name+","+u_name;
		}
	}
	
	/**
	 * 将String转成Date
	 * @param input 输入的String
	 * @return
	 */
	public static Date strToDate(String input){
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return df.parse(input);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyy-mm-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String DateToStr(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yyyy-mm-dd
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
	 * 格式化date,以String型返回。格式为 yymmdd
	 * @param date
	 * @return
	 */
	public static String DateToStr2(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyMMdd");
		
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yymm
	 * @param date
	 * @return
	 */
	public static String DateToStr3(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyyMM");
		
		return df.format(date);
	}
	
	/**
	 * 格式化date,以String型返回。格式为 yymm
	 * @param date
	 * @return
	 */
	public static String DateToStr4(Date date){
		if(date==null)
			return "";
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		
		return df.format(date);
	}
	
	/**
	 * 将标准的编号  yyyyMM##分割为一个字符数组
	 * @param no
	 * @return
	 */
	public static String[] splitAppNo(String no){
		String[] re=new String[3];
		re[0]=no.substring(0,4);
		re[1]=no.substring(4,6);
		re[2]=no.substring(6,no.length());
		return re;
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
	 * 读取session中记载的文件上传信息
	 * @param req request请求
	 * @return
	 */
	public static int[] readFileUpload(HttpServletRequest req){
		HttpSession session =req.getSession();
		int[]result=new int[2];
		result[0]=Integer.parseInt(session.getAttribute("readBytes").toString());
		result[1]=Integer.parseInt(session.getAttribute("totalBytes").toString());
		return result;
	}
	
	/**
	 * 返回分页大小
	 * @return
	 */
	public static int getPageSize(){
		return pageSize;
	}
	
	/**
	 * 返回分页查找起始页,配合前台传入参数使用
	 * @param start 记录开始索引
	 * @param limit 每页记录数
	 * @return
	 */
	public static int getPageNo(int start,int limit){
		return start/limit+1;
	}
	
	/**
	 * 数据库连接或通信出错
	 */
	public static void dbError(String param){
		System.out.println("数据库连接出错:"+param);
	}

	/**
	 * 获取天气URL
	 * @return
	 */
	public static String getWeatherUrl() {
		return weatherUrl;
	}

	/**
	 * 获取 批量采购 字符串
	 * @return
	 */
	public static String getStockInBatch() {
		return stockInBatch;
	}

	/**
	 * 获取本项目实体类的路径
	 * @return
	 */
	public static String get_609EntityUrl() {
		return _609EntityUrl;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input){
		if(input==null || "".equals(input))
			return true;
		return false;
	}
	
	/**
	 * 根据变量名称从params.properties文中获取对应的值
	 * @param propertyName 变量名称
	 * @return 相应的值
	 */
	public static String getParamsProperty(String propertyName){
		if(propertyName==null || propertyName.equals(""))
			return "";
		InputStream is=StrUtils.class.getClassLoader().getResourceAsStream("params.properties");
		Properties p=new Properties();
		try{
			p.load(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		String re="";
		try {
			re = new String(p.getProperty(propertyName).getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			re="";
		}
		return re;
	}
	/**
	  *  判断某个字符串是否存在于数组中
	  *  @param stringArray 原数组
	  *  @param source 查找的字符串
	  *  @return 是否找到
	  */
	 public static boolean contains(String[] stringArray, String source) {
	  // 转换为list
	  List<String> tempList = Arrays.asList(stringArray);
	  
	  // 利用list的包含方法,进行判断
	  if(tempList.contains(source))
	  {
	   return true;
	  } else {
	   return false;
	  }
	 } 
}

