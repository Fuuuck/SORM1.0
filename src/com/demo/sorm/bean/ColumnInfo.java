package com.demo.sorm.bean;
/**
 * 封装表中的一个字段的信息
 * @author cacer
 * @version 0.1
 *
 */
public class ColumnInfo {
	/**
	 * 字段名称
	 */
	private String name;
	/**
	 * 字段的数据类型
	 */
	private String dataType;
	/**
	 * 字段的键的类型（0-普通键 1-主键 2-外键 ）
	 */
	private int keyType;
	/**
	 * 空构造器
	 */
	public ColumnInfo(){}
	/**
	 * 带参构造器
	 * @param name
	 * @param dataType
	 * @param keyType
	 */
	public ColumnInfo(String name, String dataType, int keyType) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.keyType = keyType;
	}
	/**
	 * get set 方法
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getKeyType() {
		return keyType;
	}
	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}
	 
	
}
