package com.demo.sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.demo.sorm.bean.ColumnInfo;
import com.demo.sorm.bean.JavaFieldGetSet;
import com.demo.sorm.bean.TableInfo;
import com.demo.sorm.core.DBManager;
import com.demo.sorm.core.MySqlTypeConvertor;
import com.demo.sorm.core.TableContext;
import com.demo.sorm.core.TypeConvertor;

/**
 * 封装了生成java文件（源代码）常用操作
 * @author cacer
 *
 */
public class JavaFileUtils {
	/**
	 * e根据字段信息生成java属性信息 如 varchar username -->private String username;以及相应的get set方法源码
	 * @param column 字段信息
	 * @param convertor 类型转换器
	 * @return java属性和get、set方法
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column,TypeConvertor convertor){
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		jfgs.setFiledInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		//生成get方法源码
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+";\n");
		getSrc.append("\t}");
		jfgs.setGetInfo(getSrc.toString());
		//生成get方法源码
		//public void setGetInfo(String getInfo) {this.getInfo = getInfo;}
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"("+javaFieldType+" "+column.getName()+"){\n ");
		setSrc.append("\t\tthis."+column.getName()+" = "+column.getName()+";\n");
		setSrc.append("\t}");
		jfgs.setSetInfo(setSrc.toString());
		return jfgs;
	}
	/**
	 * 根据表信息生成java类的源代码
	 * @param tableinfo 表信息
	 * @param convertor	数据类型转换器
	 * @return java类的元代码
	 */
	public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		
		Map<String,ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javafields = new ArrayList<JavaFieldGetSet>();
		for (ColumnInfo c:columns.values()) {
			javafields.add(createFieldGetSetSRC(c,convertor));
		}
		
		StringBuilder src = new StringBuilder();
		//生成package语句
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		//生成import语句
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		//生成类申明语句
		src.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
		//生成属性列表
		for(JavaFieldGetSet f:javafields){
			 src.append(f.getFiledInfo());
		}
		src.append("\n\n");
		//生成get方法
		for(JavaFieldGetSet f:javafields){
			 src.append(f.getGetInfo());
		}
		//生成set方法
		for(JavaFieldGetSet f:javafields){
			 src.append(f.getSetInfo());
		}
		//生成类结束符
		src.append("\t}");
		return src.toString();
		
	}
	
	public static void createJavaPOFiled(TableInfo tableInfo,TypeConvertor convertor){
		 String src = createJavaSrc(tableInfo,convertor);
		 String srcPath = DBManager.getConf().getSrcPath()+"\\";
		 String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.","/");
		 File f = new File(srcPath+packagePath);
		 if(!f.exists()){
			 f.mkdirs();
		 }
		 BufferedWriter bw = null;
		 try {
			bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()+"/"+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java"));
			bw.write(src);	
			System.out.println("建立表"+tableInfo.getTname()+"对应的java类："+StringUtils.firstChar2UpperCase(tableInfo.getTname()+".java"));
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("id","int",0);
//		JavaFieldGetSet f = createFieldGetSetSRC(ci, new MySqlTypeConvertor());
//		System.out.println(f);
//		Map<String,TableInfo> map = TableContext.tables;
//		TableInfo t = map.get("emp");
//		createJavaPOFiled(t,new MySqlTypeConvertor());
		Map<String,TableInfo> map = TableContext.tables;
		for (TableInfo t:map.values()) {
			JavaFileUtils.createJavaPOFiled(t,new MySqlTypeConvertor());
		}
	}
}
