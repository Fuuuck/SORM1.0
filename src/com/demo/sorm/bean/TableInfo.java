package com.demo.sorm.bean;

import java.util.List;
import java.util.Map;

/**
 * 存储表结构的信息
 * @author cacer
 *
 */
public class TableInfo {
	/*
	 *表名 
	 */
	private String tname;
	/**S
	 * 所有字段的信息
	 */
	private Map<String,ColumnInfo> columns;
	/**
	 * 唯一主键（目前只能处理表中有且只有一个主键的情况）
	 */
	private ColumnInfo onlyKey;
	private List<ColumnInfo> priKeys;//如果联合主键，则在这里存储
	
	public List<ColumnInfo> getPriKeys() {
		return priKeys;
	}

	public void setPriKeys(List<ColumnInfo> priKeys) {
		this.priKeys = priKeys;
	}

	public TableInfo() {
	}

	public TableInfo(String tname, Map<String, ColumnInfo> columns, ColumnInfo onlyKey) {
		super();
		this.tname = tname;
		this.columns = columns;
		this.onlyKey = onlyKey;
	}
	
	public TableInfo(String tname,List<ColumnInfo> priKeys,Map<String, ColumnInfo> columns) {
		super();
		this.tname = tname;
		this.columns = columns;
//		this.onlyKey = onlyKey;
		this.priKeys = priKeys;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Map<String, ColumnInfo> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, ColumnInfo> columns) {
		this.columns = columns;
	}

	public ColumnInfo getOnlyKey() {
		return onlyKey;
	}

	public void setOnlyKey(ColumnInfo onlyKey) {
		this.onlyKey = onlyKey;
	}
	
}
