package com.github.tyang513.batch.probe.model;

import com.github.tyang513.batch.probe.common.Line;
import com.github.tyang513.batch.probe.util.LineKeyConstants;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * wifi数据格式实体类,字段含义详见文档说明
 * Created by loong on 4/15/16.
 */
public class WiFiDataEntity implements Serializable {

	private String version;

	private String devtype;

	private String keytype;

	private long tsreceive;
	
	private WifiData wifidata;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDevtype() {
		return devtype;
	}

	public void setDevtype(String devtype) {
		this.devtype = devtype;
	}

	public String getKeytype() {
		return keytype;
	}

	public void setKeytype(String keytype) {
		this.keytype = keytype;
	}

	public WifiData getWifidata() {
		return wifidata;
	}

	public void setWifidata(WifiData wifidata) {
		this.wifidata = wifidata;
	}
	
	@Override
	public String toString() {
		return "WiFiDataEntity [version=" + version + ", devtype=" + devtype
				+ ", keytype=" + keytype + ", tsreceive=" + tsreceive
				+ ", wifidata=" + wifidata + "]";
	}

	public long getTsreceive() {
		return tsreceive;
	}

	public void setTsreceive(long tsreceive) {
		this.tsreceive = tsreceive;
	}

	public List<Line> split(){
		List<Line> lineList = new ArrayList<Line>();
		WifiData wifiData = this.getWifidata();
		// WiFiDataEntity
		String version = this.getVersion();
		String devtype = this.getDevtype();
		String keytype = this.getKeytype();
		long tsreceive = this.getTsreceive();

		// WifiData
		String apmac = paddingMac(wifiData.getApmac());
		int num = wifiData.getNum();
		long tssend = wifiData.getTssend();
		for (WifiTa wifiTa : wifiData.getWifitalist()) {
			String rssi = wifiTa.getRssi();
			String mac = paddingMac(wifiTa.getMac());//
			int dist = wifiTa.getDist();
			long duringstart = wifiTa.getDuringstart();
			long duringend = wifiTa.getDuringend();
			int packetnumup = wifiTa.getPacketnumup();
			int packetnumdown = wifiTa.getPacketnumdown();
			int volumeup = wifiTa.getVolumeup();
			int volumedown = wifiTa.getVolumedown();
			int authidtype = wifiTa.getAuthidtype();
			String authid = wifiTa.getAuthid();
			String tatype = wifiTa.getTatype();
			String tabrand = wifiTa.getTabrand();
			String tasystem = wifiTa.getTasystem();
			String applicationlist = wifiTa.getApplicationlist();//
			String urllist = wifiTa.getUrllist();//
			String dns = wifiTa.getDns();
			Line line = new Line();
			String taEventJsonString = "";
			if(null != wifiTa.getTaevent()){
//				taEventJsonString = JsonUtils.objectToJsonStr(wifiTa.getTaevent());
				taEventJsonString = getEncodeString(taEventJsonString);
			}
			line.put(LineKeyConstants.version, version);
			line.put(LineKeyConstants.devtype, devtype);
			line.put(LineKeyConstants.keytype, keytype);
			line.put(LineKeyConstants.tsreceive, tsreceive);
			line.put(LineKeyConstants.apmac, apmac);
			line.put(LineKeyConstants.num, num);
			line.put(LineKeyConstants.tssend, tssend);
			line.put(LineKeyConstants.rssi, rssi);
			line.put(LineKeyConstants.mac, mac);
			line.put(LineKeyConstants.dist, dist);
			line.put(LineKeyConstants.duringstart, duringstart);
			line.put(LineKeyConstants.duringend, duringend);
			line.put(LineKeyConstants.packetnumup, packetnumup);
			line.put(LineKeyConstants.packetnumdown, packetnumdown);
			line.put(LineKeyConstants.volumeup, volumeup);
			line.put(LineKeyConstants.volumedown, volumedown);
			line.put(LineKeyConstants.authidtype, authidtype);
			line.put(LineKeyConstants.authid, authid);
			line.put(LineKeyConstants.tatype, tatype);
			line.put(LineKeyConstants.tabrand, tabrand);
			line.put(LineKeyConstants.tasystem, tasystem);
			line.put(LineKeyConstants.applicationlist, applicationlist);
			line.put(LineKeyConstants.urllist, urllist);
			line.put(LineKeyConstants.dns, dns);
			line.put(LineKeyConstants.taevent, taEventJsonString);
			lineList.add(line);
//			logger.error("-------------taevent is null!apmac=" + apmac);
		}
		return lineList;
	}


	/**
	 * 补全12位mac地址为带:的
	 *
	 * @return
	 */
	public String paddingMac(String mac) {
		StringBuffer paddingMac = new StringBuffer();
		if (null != mac && mac.length() == 12) {
			for (int i = 0; i < 12; i = i + 2) {
				paddingMac.append(mac.substring(i, i + 2)).append(":");
			}
			paddingMac.delete(paddingMac.length() - 1, paddingMac.length());
			return paddingMac.toString();
		}
		return mac;
	}

	public String getEncodeString(String str) {
		try {
			if (null != str) {
				return URLEncoder.encode(str, "UTF-8");
			}
			return "";
		} catch (Exception e) {

			return str;
		}
	}
}
