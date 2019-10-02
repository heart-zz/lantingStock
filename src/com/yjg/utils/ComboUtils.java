package com.yjg.utils;
/**
 * 前端combo封装类
 * @author yjg
 *
 */
public class ComboUtils {

	/**
	 * 属性名
	 */
	private String text;
	/**
	 * 属性值
	 */
	private String value;
	
	public ComboUtils(){
		
	}
	
	/**
	 * 待参构造函数
	 * @param text
	 * @param value
	 */
	public ComboUtils(String text,String value){
		this.text=text;
		this.value=value;
	}
	
	/**
	 * 获取 属性名
	 * @return text属性名
	 */
	public String getText() {
		return text;
	}
	/**
	 * 设置 属性名
	 * @param text属性名
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * 获取 属性值
	 * @return value属性值
	 */
	public String getValue() {
		return value;
	}
	/**
	 * 设置 属性值
	 * @param value属性值
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
