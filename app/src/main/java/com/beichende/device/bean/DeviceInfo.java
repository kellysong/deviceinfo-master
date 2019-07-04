package com.beichende.device.bean;

/**
 * 
 * 设备信息实体
 * @author Kelly
 * @version 1.0.0
 * @filename DeviceInfo.java
 * @time 2017年12月1日 上午10:59:32
 * @copyright(C) 2017 song
 */
public class DeviceInfo {
	private String ip;
	private String mac;
	
	
	public DeviceInfo(String ip, String mac) {
		this.ip = ip;
		this.mac = mac;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	@Override
	public String toString() {
		return "本机ip地址和物理地址： [ip=" + ip + ", mac=" + mac + "]";
	}
	
}
