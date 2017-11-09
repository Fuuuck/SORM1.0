package com.demo.sorm.core;

import com.demo.pool.DBConnPool;

/**
 * 创建Query对象的工厂类
 * @author a c e r
 *
 */
public class QueryFactory {
	private static QueryFactory factory = new QueryFactory();
	private static Query prototypeObject;
	static{
		try {
			Class c = Class.forName(DBManager.getConf().getQueryClass());//加载指定的query类
			prototypeObject = (Query)c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private QueryFactory() {//私有构造器
	}
	public static Query createQuery(){
		try {
			return (Query) prototypeObject.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
