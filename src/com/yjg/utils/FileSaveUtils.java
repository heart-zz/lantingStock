package com.yjg.utils;
/**
 * 文件保存后辅助类
 * @author yjg
 *
 */
public class FileSaveUtils {

	//原文件名称
	private String preFileName;
	//新文件名称
	private String newFileName;
	//新文件全路径[绝对路径]
	private String filepath;
	//文件后缀名
	private String filetype;
	//文件大小 kb
	private float fileSize_kb;
	/**
	 * 获取  原文件名称
	 * @return preFileName
	 */
	public String getPreFileName() {
		return preFileName;
	}
	
	/**
	 * 设置  原文件名称
	 * @param preFileName
	 */
	public void setPreFileName(String preFileName) {
		this.preFileName = preFileName;
	}
	
	/**
	 * 获取  新文件名称
	 * @return newFileName
	 */
	public String getNewFileName() {
		return newFileName;
	}
	
	/**
	 * 设置  新文件名称
	 * @param newFileName
	 */
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}
	
	/**
	 * 获取  新文件全路径[绝对路径]
	 * @return filepath
	 */
	public String getFilepath() {
		return filepath;
	}
	
	/**
	 * 设置  新文件全路径[绝对路径]
	 * @param filepath
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	/**
	 * 获取  文件后缀名
	 * @return filetype
	 */
	public String getFiletype() {
		return filetype;
	}
	
	/**
	 * 设置  文件后缀名
	 * @param filetype
	 */
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	
	/**
	 * 获取  文件大小 kb
	 * @return fileSize_kb
	 */
	public float getFileSize_kb() {
		return fileSize_kb;
	}
	
	/**
	 * 设置  文件大小 kb
	 * @param fileSize_kb
	 */
	public void setFileSize_kb(float fileSize_kb) {
		this.fileSize_kb = fileSize_kb;
	}
	
	
}
