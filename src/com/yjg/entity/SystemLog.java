package com.yjg.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;
/**
 * 系统自定义日志类
 * @author yjg
 *
 */
@Table("systemlog")
public class SystemLog extends IdEntity{
	
	/**
	 * 空参构造函数--用于映射数据表
	 */
	public SystemLog(){
		
	}
	
	/**
	 * 带参构造函数,用于快速生成日志
	 * @param _classname 所在类名
	 * @param _errorInfo 错误信息
	 * @param _methodname 所在方法名
	 * @param _remark 备注
	 */
	public SystemLog(String _classname,String _errorInfo,String _methodname,String _remark){
		classname=_classname;
		errorInfo=_errorInfo;
		methodname=_methodname;
		remark=_remark;
		date=new Date();
	}

	@Column
	private Date date;
	@Column
	private String classname;
	@Column
	@ColDefine(type=ColType.TEXT)
	private String errorInfo;
	@Column
	@ColDefine(type=ColType.VARCHAR,width=128)
	private String methodname;
	@Column
	@ColDefine(type=ColType.VARCHAR,width=128)
	private String remark;
	/**
	 * 获取 date
	 * @return datedate
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * 设置 date
	 * @param datedate
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * 获取 classname
	 * @return classnameclassname
	 */
	public String getClassname() {
		return classname;
	}
	/**
	 * 设置 classname
	 * @param classnameclassname
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	/**
	 * 获取 errorInfo
	 * @return errorInfoerrorInfo
	 */
	public String getErrorInfo() {
		return errorInfo;
	}
	/**
	 * 设置 errorInfo
	 * @param errorInfoerrorInfo
	 */
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	/**
	 * 获取 methodname
	 * @return methodnamemethodname
	 */
	public String getMethodname() {
		return methodname;
	}
	/**
	 * 设置 methodname
	 * @param methodnamemethodname
	 */
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	/**
	 * 获取 remark
	 * @return remarkremark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 设置 remark
	 * @param remarkremark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
