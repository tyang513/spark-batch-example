package com.github.tyang513.batch.probe

import java.net.URLEncoder

import com.clearspring.analytics.hash.MurmurHash
import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.tyang513.batch.probe.common.Line
import com.github.tyang513.batch.probe.model.WiFiDataEntity
import com.github.tyang513.batch.probe.util.LineKeyConstants
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by yangtao on 2017/9/13.
  */
object WiFiProbeProcessor {

  val logger = LoggerFactory.getLogger(WiFiProbeProcessor.getClass)

  def main(args: Array[String]): Unit = {

//    if (args.length <= 0 || args.length != 2) {
//      logger.info("请输入参数 master filePath")
//      System.exit(0)
//    }

    val master = "local[4]"
//      if (args.length == 2) args(0) else {
//      logger.info("请输入master地址")
//      null
//    } // local "spark://ip-172-31-24-33.cn-north-1.compute.internal:7077"
    val filePath = "/home/hadoop/tao.yang/data.log"
//    if (args.length == 2) args(1) else {
//      logger.info("请输入要解析的文件地址")
//      null
//    } //

    // 初始化spark context
    val sparkConfig = new SparkConf().setAppName("WiFi-Probe-Processor").setMaster(master)
    val sparkContext = new SparkContext(sparkConfig)

    // read file
    val probeLogFile = sparkContext.textFile(filePath)

    val emitterJson = probeLogFile.map(line => {
      val mapper = new ObjectMapper() with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)
      mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true)
      try
        mapper.readValue(line, classOf[WiFiDataEntity])
      catch {
        case e: Exception => logger.error(line, e)
      }
    }).map(data => if (data.isInstanceOf[WiFiDataEntity]) WiFiProbeProcessor.parseWifiAnalyticsLog(data.asInstanceOf[WiFiDataEntity])).map(l => {
      val line = l.asInstanceOf[Line]
      val machash = MurmurHash.hash64(line.get(LineKeyConstants.mac) + "")
      (machash, line)
    }).groupByKey().saveAsTextFile("/home/hadoop/tao.yang/tmp/")

  }

  def parseWifiAnalyticsLog(wifiDataEntity: WiFiDataEntity): List[Line] = {
    val returnList = ArrayBuffer.empty[Line]
    val wifiData = wifiDataEntity.getWifidata

    // WiFiDataEntity
    val version = wifiDataEntity.getVersion
    val devtype = wifiDataEntity.getDevtype
    val keytype = wifiDataEntity.getKeytype
    val tsreceive : java.lang.Long = wifiDataEntity.getTsreceive
    val apmac = paddingMac(wifiData.getApmac)
    val num : java.lang.Integer = wifiData.getNum
    val tssend : java.lang.Long = wifiData.getTssend

    for (wifiTa <- wifiData.getWifitalist) {
      val line = new Line
      val rssi = wifiTa.getRssi
      val mac = paddingMac(wifiTa.getMac)
      //
      val dist : java.lang.Integer = wifiTa.getDist
      val duringstart : java.lang.Long = wifiTa.getDuringstart
      val duringend : java.lang.Long = wifiTa.getDuringend
      val packetnumup : Integer = wifiTa.getPacketnumup
      val packetnumdown : Integer = wifiTa.getPacketnumdown
      val volumeup : Integer = wifiTa.getVolumeup
      val volumedown : Integer = wifiTa.getVolumedown
      val authidtype : Integer = wifiTa.getAuthidtype
      val authid = wifiTa.getAuthid
      val tatype = wifiTa.getTatype
      val tabrand = wifiTa.getTabrand
      val tasystem = wifiTa.getTasystem
      val applicationlist = wifiTa.getApplicationlist
      val urllist = wifiTa.getUrllist
      val dns = wifiTa.getDns

      var taEventJsonString = ""
      if (null != wifiTa.getTaevent) {
        taEventJsonString = "" // JsonUtils.objectToJsonStr(wifiTa.getTaevent)
        //        taEventJsonString = getEncodeString(taEventJsonString)
      }
      line.put(LineKeyConstants.version, version)
      line.put(LineKeyConstants.devtype, devtype)
      line.put(LineKeyConstants.keytype, keytype)
      line.put(LineKeyConstants.tsreceive, tsreceive)
      line.put(LineKeyConstants.apmac, apmac)
      line.put(LineKeyConstants.num, num)
      line.put(LineKeyConstants.tssend, tssend)
      line.put(LineKeyConstants.rssi, rssi)
      line.put(LineKeyConstants.mac, mac)
      line.put(LineKeyConstants.dist, dist)
      line.put(LineKeyConstants.duringstart, duringstart)
      line.put(LineKeyConstants.duringend, duringend)
      line.put(LineKeyConstants.packetnumup, packetnumup)
      line.put(LineKeyConstants.packetnumdown, packetnumdown)
      line.put(LineKeyConstants.volumeup, volumeup)
      line.put(LineKeyConstants.volumedown, volumedown)
      line.put(LineKeyConstants.authidtype, authidtype)
      line.put(LineKeyConstants.authid, authid)
      line.put(LineKeyConstants.tatype, tatype)
      line.put(LineKeyConstants.tabrand, tabrand)
      line.put(LineKeyConstants.tasystem, tasystem)
      line.put(LineKeyConstants.applicationlist, applicationlist)
      line.put(LineKeyConstants.urllist, urllist)
      line.put(LineKeyConstants.dns, dns)
      line.put(LineKeyConstants.taevent, taEventJsonString)
      returnList.add(line)
    }
    returnList.toList
  }

  def paddingMac(mac: String): String = {
    val paddingMac = new StringBuffer
    if (null != mac && mac.length == 12) {
      var i = 0
      while ( {
        i < 12
      }) {
        paddingMac.append(mac.substring(i, i + 2)).append(":")
        i = i + 2
      }
      paddingMac.delete(paddingMac.length - 1, paddingMac.length)
      return paddingMac.toString
    }
    mac
  }

  def getEncodeString(str: String): String = try {
    if (null != str) return URLEncoder.encode(str, "UTF-8")
    ""
  } catch {
    case e: Exception =>
      logger.warn("URLEncoder exception:" + str, e)
      str
  }

}
