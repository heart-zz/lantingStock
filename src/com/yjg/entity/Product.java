package com.yjg.entity;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import com.yjg.utils.IdEntity;

/**
 * 标准品名表
 * @author yjg
 *
 */
@Table("st_product")
public class Product extends IdEntity{

			//图片1路径
			@Column
			@ColDefine(width=128)
			private String picUrl1;
			
			//图片2路径
			@Column
			@ColDefine(width=128)
			private String picUrl2;
			
			//图片3路径
			@Column
			@ColDefine(width=128)
			private String picUrl3;
			
			//品名
			@Column
			@ColDefine(width=128)
			private String name;
			
			//型号
			@Column
			@ColDefine(width=128)
			private String model;
			
			//产品编号
			@Column
			@ColDefine(width=128)
			private String proNo;
			
			//单位
			@Column
			private String unit;
			
			//备注
			@Column
			@ColDefine(width=128)
			private String remark;
			
			//最后修改人
			@Column
			@ColDefine(width=128)
			private String lastModifier;

			/**
			 * 获取  图片1路径
			 * @return picUrl1
			 */
			public String getPicUrl1() {
				return picUrl1;
			}
			

			/**
			 * 设置  图片1路径
			 * @param picUrl1
			 */
			public void setPicUrl1(String picUrl1) {
				this.picUrl1 = picUrl1;
			}
			

			/**
			 * 获取  图片2路径
			 * @return picUrl2
			 */
			public String getPicUrl2() {
				return picUrl2;
			}
			

			/**
			 * 设置  图片2路径
			 * @param picUrl2
			 */
			public void setPicUrl2(String picUrl2) {
				this.picUrl2 = picUrl2;
			}
			

			/**
			 * 获取  图片3路径
			 * @return picUrl3
			 */
			public String getPicUrl3() {
				return picUrl3;
			}
			

			/**
			 * 设置  图片3路径
			 * @param picUrl3
			 */
			public void setPicUrl3(String picUrl3) {
				this.picUrl3 = picUrl3;
			}
			

			/**
			 * 获取  品名
			 * @return name
			 */
			public String getName() {
				return name;
			}
			

			/**
			 * 设置  品名
			 * @param name
			 */
			public void setName(String name) {
				this.name = name;
			}
			

			/**
			 * 获取  型号
			 * @return model
			 */
			public String getModel() {
				return model;
			}
			

			/**
			 * 设置  型号
			 * @param model
			 */
			public void setModel(String model) {
				this.model = model;
			}
			

			/**
			 * 获取  单位
			 * @return unit
			 */
			public String getUnit() {
				return unit;
			}
			

			/**
			 * 设置  单位
			 * @param unit
			 */
			public void setUnit(String unit) {
				this.unit = unit;
			}
			

			/**
			 * 获取  备注
			 * @return remark
			 */
			public String getRemark() {
				return remark;
			}
			

			/**
			 * 设置  备注
			 * @param remark
			 */
			public void setRemark(String remark) {
				this.remark = remark;
			}


			public String getLastModifier() {
				return lastModifier;
			}


			public void setLastModifier(String lastModifier) {
				this.lastModifier = lastModifier;
			}


			public String getProNo() {
				return proNo;
			}


			public void setProNo(String proNo) {
				this.proNo = proNo;
			}
			
			
}
