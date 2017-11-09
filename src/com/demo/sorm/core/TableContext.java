package com.demo.sorm.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.demo.sorm.bean.ColumnInfo;
import com.demo.sorm.bean.TableInfo;
import com.demo.sorm.utils.JavaFileUtils;
import com.demo.sorm.utils.StringUtils;
import com.mysql.jdbc.DatabaseMetaData;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生产类结构
 * @author cacer
 *
 */
public class TableContext {
	/**
	 * 表名为key 表信息对应为value
	 */
	public static Map<String,TableInfo> tables = new HashMap<String,TableInfo>();
	/**
	 * 将po的class对象和表信息关联起来 便于调用
	 */
	public static Map<Class,TableInfo> poClassTableMap = new HashMap<Class,TableInfo>();
	private TableContext(){}
	static{
		//初始化获得表信息
		Connection con = DBManager.getConn();
		try {
			DatabaseMetaData dbmd = (DatabaseMetaData) con.getMetaData();
			ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[] { "TABLE" });
			while(tableRet.next()){
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				TableInfo ti = new TableInfo(tableName,new ArrayList<ColumnInfo>(),new HashMap<String,ColumnInfo>());
				tables.put(tableName, ti);
				ResultSet set = dbmd.getColumns(null,"%", tableName, "%");//查询表中的所有字段
				while(set.next()){
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"),set.getString("TYPE_NAME"),0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
				while(set2.next()){
					ColumnInfo ci2= ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);
					ti.getPriKeys().add(ci2);
				}
				if(ti.getPriKeys().size()>0){
					ti.setOnlyKey(ti.getPriKeys().get(0));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//更新类结构 每次启动这个框架的时候调用
		updateJavaPOFile();
		//加载po包下面所有的类 便于重用 提高效率
		loadPOTable();
	}
	/**
	 * 根据表结构，更新配置的po包下面的java类
	 * 实现了从表结构转换Wie类结构
	 * 项目启动的时候掉这个方法
	 */
	public static void updateJavaPOFile(){
		Map<String,TableInfo> map = TableContext.tables;
		for (TableInfo t:map.values()) {
			JavaFileUtils.createJavaPOFiled(t,new MySqlTypeConvertor());
		}
	}
	/**
	 * 加载po包下面的类
	 */
	public static void loadPOTable(){
		//1.通过反射 找到所需要的类
//		Class c = Class.forName("");
		//2。把这个类和表放到同一个map容器中
//		poClassTableMap(c,tableInfo);
		
		for(TableInfo tableInfo:tables.values()){
			try {
				Class c = Class.forName(DBManager.getConf().getPoPackage() + "."
						+ StringUtils.firstChar2UpperCase(tableInfo.getTname()));
				poClassTableMap.put(c,tableInfo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}
	public static Map<String,TableInfo> getTableInfos(){
		return tables;
	}
}
