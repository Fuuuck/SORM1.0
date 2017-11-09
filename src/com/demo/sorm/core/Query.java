package com.demo.sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.demo.sorm.bean.ColumnInfo;
import com.demo.sorm.bean.TableInfo;
import com.demo.sorm.utils.JDBCUtils;
import com.demo.sorm.utils.ReflectUtils;

/**
 * 负责查询对外提供核心的类
 * @author cacer
 *
 */
@SuppressWarnings("all")
public abstract class Query implements Cloneable{
	/**
	 * 采用模板方法模式将JDBC操作封装成模板，便于重用 
	 * @param sql sql的语句
	 * @param params sql的参数
	 * @param clazz 记录要封装到的java类
	 * @param callback CallBack的实现类，实现回调
	 * @return
	 */
	public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack callback){
		Connection conn = DBManager.getConn();
		List list = null;//存放查询结果的容器
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps .executeQuery();
			return callback.doExecute(conn, ps, rs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			DBManager.close(conn);
		}
	}
	/**
	 * 直接执行DML语句
	 * @param sql sql语句
	 * @param params 参数
	 * @return	执行sql语句后影响记录的行数
	 */
	public int execueDML(String sql,Object[] params){
		Connection conn = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBManager.close(conn);
		}
		return count;
	}
	/**
	 * 讲一个对象存储到数据库中
	 * 把对象不为null的属性往数据库中存储  如果数字为null则 放0
	 * @param obj 要存储的对象
	 */
	public void insert(Object obj){
		//把对象--》表中     insert into 表名 (id,uname,pwd) values(?,?,?);
				Class c = obj.getClass();
				List<Object> params = new ArrayList<Object>();//存储sql的参数对象
				TableInfo tableInfo = TableContext.poClassTableMap.get(c);
				StringBuilder sql = new StringBuilder("  insert into "+tableInfo.getTname()+" (");
				int countNotnullField = 0;//计算补位空的属性
				Field[] fs = c.getDeclaredFields();
				for(Field f : fs){
					String fieldName = f.getName();
					Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
					if(fieldName!=null){
						countNotnullField++;
						sql.append(fieldName+",");
						params.add(fieldValue);
					}
				}
				sql.setCharAt(sql.length()-1, ')');
				sql.append(" values (");
				for(int i=0;i<countNotnullField;i++){
					sql.append("?,");
				}
				sql.setCharAt(sql.length()-1, ')');
				execueDML(sql.toString(), params.toArray());
	}
	/**
	 * 删除CLAZZ表示类对应的表中的记录（指定主键值ID的记录）
	 * @param clazz 跟表对应的类的clazz对象
	 * @param id 主键的值
	 * 
	 */
	public void delete(Class clazz,Object id){
		// Emp.class + id---->delete from emp where id = 2;
		
				//1.通过class对象找tableInfo 对象与表绑定 
				TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
				//2。通过ID找到表
				 ColumnInfo onlyKey = tableInfo.getOnlyKey();
				 String sql = "delete from "+tableInfo.getTname()+" where "+onlyKey.getName()+" = ?";
				 execueDML(sql, new Object[]{id});
	}
	/**
	 * 删除对象在数据库中对应的记录（对象所在的类对应的表，对象的主键的值对应的记录）
	 * @param obj
	 */
	public void delete(Object obj){
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyKey = tableInfo.getOnlyKey();//获得主键
		Object priKeyValue = ReflectUtils.invokeGet(onlyKey.getName(), obj);
		delete(c,priKeyValue);
	}
	/**
	 * 更新对象对应的记录，并且只更新指定字段的值
	 * @param obj 所要跟新的对象
	 * @param filedName 跟新的属性列表
	 * @return 执行sql语句后影响记录的行数
	 */
	public int update(Object obj,String[] filedNames){
		//obj{"uname","pwd"}-->update 表名  set uname=?,pwd=? where id = ?
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();//存储sql的参数对象
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo priKey = tableInfo.getOnlyKey();
		StringBuilder sql = new StringBuilder("  update "+tableInfo.getTname()+" set ");
		for(String fname:filedNames){
			Object fvalue = ReflectUtils.invokeGet(fname, obj);
			params.add(fvalue);
			sql.append(fname+"=?,");
		}
		sql.setCharAt(sql.length()-1, ' ')	;
		sql.append(" where ");
		sql.append(priKey.getName()+"=? ");
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));//主键的值
		return execueDML(sql.toString(), params.toArray());
	}//update user set user=?,pwd=?
	/**
	 * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的javabean 类的clazz对象
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public List queryRows(final String sql,final Class clazz,final Object[] params){
		return (List)executeQueryTemplate(sql, params, clazz, new CallBack(){
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					//多行
					while(rs.next()){
						if(list==null){
							list = new ArrayList();
						}
						Object rowObj = clazz.newInstance();//调用javabean的无参构造函数
						//多列 select uname,pwd,age fromm user where id>? and age>18
						for(int i=0;i<metaData.getColumnCount();i++){
							String columnName = metaData.getColumnLabel(i+1);//假设为uname
							Object columnValue = rs.getObject(i+1);
							
							//调用rowObj对象的setUsername(String uname)方法，将columnValue的值设置进去
							ReflectUtils.invokeSet(rowObj, columnName, columnValue);
						}
						list.add(rowObj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return list;
			}
			});
	}
	/**
	 * 查询返回一行记录，并将该记录封装到clazz指定的类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的javabean 类的clazz对象
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public Object queryUniqueRows(String sql,Class clazz,Object[] params){
		List list = queryRows(sql, clazz, params);
		return (list==null&&list.size()>0)?null:list.get(0);
	}
	/**
	 * 查询返回一个值（一行一列），并将该值返回
	 * @param sql 查询语句
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public Object queryValue(String sql,Object[] params){
		return executeQueryTemplate(sql, params, null, new CallBack(){

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while(rs.next()){
						//select count(*) from user
							value = rs.getObject(1);
						}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}});
	}
	/**
	 * 查询返回一个值（一行一列），并将该值返回
	 * @param sql 查询语句
	 * @param params sql的参数
	 * @return 查询到的数字
	 */
	public Number queryNumber(String sql,Object[] params){
		return (Number)queryValue(sql, params);
	}
	/**
	 * 分页查询
	 * @param pageNum  第几页数据
	 * @param size 每页显示多少记录
	 * @return
	 */
	public abstract Object querPagenate(int pageNum,int size);
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
