package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 角色表
 * @author yjg
 *
 */
@Table("role")
public class Role extends IdEntity{

	/**
	 * 角色名称
	 */
	@Column
	private String roleName;
	/**
	 * 可以删除
	 */
	@Column
	private boolean canDel;
	
	/**
	 * 备注
	 */
	@ColDefine(type=ColType.VARCHAR,width=127)
	@Column
	private String remark;

	/**
	 * 获取 角色名称
	 * @return rolename角色名称
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * 设置 角色名称
	 * @param rolename角色名称
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * 获取 可以删除
	 * @return canDel可以删除
	 */
	public boolean isCanDel() {
		return canDel;
	}

	/**
	 * 设置 可以删除
	 * @param canDel可以删除
	 */
	public void setCanDel(boolean canDel) {
		this.canDel = canDel;
	}

	/**
	 * 获取 备注
	 * @return remark备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置 备注
	 * @param remark备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}	
	
}
