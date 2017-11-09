package com.demo.sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装了JDBC查询的常用操作
 * @author cacer
 *
 */
public class JDBCUtils {
	/**
	 * //给sql设参数
	 * @param ps 预编译sql语句
	 * @param params 参数
	 */
		public static void  handleParams(PreparedStatement ps,Object[] params){
			if(params!=null){
				for(int i=0;i<params.length;i++){
					try {
						ps.setObject(1+i, params[i]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
}
