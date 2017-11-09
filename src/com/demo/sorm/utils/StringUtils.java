package com.demo.sorm.utils;
/**
 * 封装了字符串常用的操作
 * @author cacer
 *
 */
public class StringUtils {
	/**
	 * 将目标首字母变为大写
	 * @param str 目标字符串
	 * @return 生成的字符串
	 */
	public static String firstChar2UpperCase(String str){
		//abcd--->Abcd
		//ABCD--->A
		return str.toUpperCase().substring(0,1)+str.substring(1);
		
	}
}
