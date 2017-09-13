package com.github.tyang513.batch.probe.util;

/**
 * @author yangtao
 *
 */
public class LineKeyConstants {

	// WiFiDataEntity
	public static final String version = "version";
	public static final String devtype = "devtype";
	public static final String keytype = "keytype";

	// WifiData
	public static final String apmac = "apmac";
	public static final String num = "num";
	public static final String tssend = "tssend";
	public static final String tsreceive = "tsreceive";

	// WifiTa
	public static final String rssi = "rssi";
	public static final String mac = "mac";
	public static final String dist = "dist";
	public static final String duringstart = "duringstart";
	public static final String duringend = "duringend";
	public static final String packetnumup = "packetnumup";
	public static final String packetnumdown = "packetnumdown";
	public static final String volumeup = "volumeup";
	public static final String volumedown = "volumedown";
	public static final String authidtype = "authidtype";
	public static final String authid = "authid";
	public static final String tatype = "tatype";
	public static final String tabrand = "tabrand";
	public static final String tasystem = "tasystem";
	public static final String applicationlist = "applicationlist";
	public static final String urllist = "urllist";
	public static final String dns = "dns";

	// TaEvent
	public static final String taevent = "taevent";
	public static final String fragtype = "fragtype";
	public static final String dstmac = "dstmac";
	public static final String ssid = "ssid";
	public static final String channel = "channel";
	
	// error discard code
	public static final String discard = "discard";

	// 填充属性  SensorPropFillChanger
	public static final String sensorid = "sensorid";
	public static final String projectid = "projectid";
	public static final String projectplaceid = "projectplaceid";
	public static final String roomid = "roomid";
	public static final String diargamid = "diargamid";
	public static final String tenantid = "tenantid";
	
	public static final String projecttype = "projecttype";
	public static final String openingtime = "openingtime";
	public static final String closingtime = "closingtime";

	
	// 填充属性 idmapping
	public static final String tenantoffset = "tenantoffset";
	public static final String tenantnewflag = "tenantnewflag";
	
	public static final String projectoffset = "projectoffset";
	public static final String projectnewflag = "projectnewflag";
	
	
	public static final String failcode = "failcode";
	public static final String projectname = "projectname";
	public static final String projectplacename = "projectplacename";
	public static final String roomname = "roomname";
	public static final String roomnumber = "roomnumber";
	
	public static final String projectposition = "projectposition";
	public static final String centerlongitude = "centerlongitude";
	public static final String centerlatitude = "centerlatitude";
	public static final String longitude = "longitude";
	public static final String latitude = "latitude";
	public static final String city = "city";
	
	// session 数据
	public static final String projectSessionId = "projectsessionid";
	public static final String projectSessionDuration = "projectsessionduration";
	public static final String roomSessionId = "roomsessionid";
	public static final String roomSessionDuration  = "roomsessionduration";
	public static final String enterRoomType  = "enterroomtype";//进入房间类型 1 是合理，-1 是不合理
	
	//临时变量，是否是店外
	public static final String isOutside         = "isOutside";//是否是店外
	public static final String stayRoomMinutes   = "stayRoomMinutes";//房间停留时长
	public static final String visitMinutes       = "visitMinutes";//案场停留时长
	
	public static final String stayMinutes   = "stayMinutes";//项目停留时长
	
}
