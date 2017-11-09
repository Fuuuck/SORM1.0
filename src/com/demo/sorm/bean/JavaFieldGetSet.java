package com.demo.sorm.bean;
/**
 * 封装了java属性和 get set 方法的源代码
 * @author a c e r
 *
 */
public class JavaFieldGetSet {
	/**
	 *属性的源码信息 
	 */
	private String filedInfo;
	/**
	 * get方法信息
	 */
	private String getInfo;
	/**
	 * set方法信息
	 */
	private String setInfo;
	public JavaFieldGetSet() {
	}
	public JavaFieldGetSet(String filedInfo, String getInfo, String setInfo) {
		super();
		this.filedInfo = filedInfo;
		this.getInfo = getInfo;
		this.setInfo = setInfo;
	}
	public String getFiledInfo() {
		return filedInfo;
	}
	public void setFiledInfo(String filedInfo) {
		this.filedInfo = filedInfo;
	}
	public String getGetInfo() {
		return getInfo;
	}
	public void setGetInfo(String getInfo) {
		this.getInfo = getInfo;
	}
	public String getSetInfo() {
		return setInfo;
	}
	public void setSetInfo(String setInfo) {
		this.setInfo = setInfo;
	}
	@Override
	public String toString() {
		System.out.println(filedInfo);
		System.out.println(getInfo);
		System.out.println(setInfo);
		return super.toString();
	}
}
