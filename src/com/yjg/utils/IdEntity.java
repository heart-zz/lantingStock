package com.yjg.utils;

import java.util.Date;


import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
/**
 * 主键-实体类
 * @author yjg
 *
 */
public class IdEntity {

	//记录id
	@Id
	private long id;
	//记录创建日期
	@Column
	private Date dateCreater;
	
	//记录更新日期
	@Column
	private Date dateUpdater;
	//创建人记录id
	@Column
	private long authorId;
	//创建人姓名
	@Column
	private String author;
	
	@Column
	private boolean isDeleted;

	
	//所属公司记录id
	@Column
	private  String companyNo;
	/**
	 * 获取  记录id
	 * @return id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * 设置  记录id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * 获取  记录创建日期
	 * @return dateCreater
	 */
	public Date getDateCreater() {
		return dateCreater;
	}
	
	/**
	 * 设置  记录创建日期
	 * @param dateCreater
	 */
	public void setDateCreater(Date dateCreater) {
		this.dateCreater = dateCreater;
	}
	
	/**
	 * 获取  创建人记录id
	 * @return authorId
	 */
	public long getAuthorId() {
		return authorId;
	}
	
	/**
	 * 设置  创建人记录id
	 * @param authorId
	 */
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	
	/**
	 * 获取  创建人姓名
	 * @return author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * 设置  创建人姓名
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDateUpdater() {
		return dateUpdater;
	}

	public void setDateUpdater(Date dateUpdater) {
		this.dateUpdater = dateUpdater;
	}

	public String getCompanyNo() {
		return companyNo;
	}

	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
	
}
