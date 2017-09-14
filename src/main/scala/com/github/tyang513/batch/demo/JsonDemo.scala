package com.github.tyang513.batch.demo

import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.tyang513.batch.probe.WiFiProbeProcessor
import com.github.tyang513.batch.probe.model.WiFiDataEntity

import scala.io.Source
import scala.runtime.BoxedUnit

/**
  * Created by yangtao on 2017/9/13.
  */
object JsonDemo {

  def main(args: Array[String]): Unit = {
    val file = Source.fromFile("C:\\Users\\yangtao\\Documents\\WeChat Files\\tyang513\\Files\\data.log", "utf-8")
    // "D:\\json.txt"

    val json = file.getLines().toList.map(line => line.replaceFirst("wifi:", "").replaceFirst("WiFiDataEntity", ""))
    val json2object = json.map(json => {
      val mapper = new ObjectMapper() with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)
      mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true)
      try
        mapper.readValue(json, classOf[WiFiDataEntity])
      catch {
        case e: Exception => println("json解析异常")
      }
    }).filter(f => f.getClass != classOf[BoxedUnit]) //.map(x => println(x.getClass))

    //    val lines = json2object.map(f => if (f.isInstanceOf[WiFiDataEntity]) WiFiProbeProcessor.parseWifiAnalyticsLog(f.asInstanceOf[WiFiDataEntity])).map(l => {
    //      if (l.isInstanceOf[Line]) {
    //        val line = l.asInstanceOf[Line]
    //        val machash = MurmurHash.hash64(line.get(LineKeyConstants.mac) + "")
    //        println(machash + "  " + line.get(LineKeyConstants.mac))
    //      }
    //      if (l.getClass == classOf[ArrayBuffer[Line]]) {
    //        val a = l.asInstanceOf[ArrayBuffer[Line]]
    //        println("l is array, length = " + a.length)
    //      }
    //    })

    val lineArray = json2object.map(f => WiFiProbeProcessor.parseWifiAnalyticsLog(f.asInstanceOf[WiFiDataEntity])).toArray.flatten
    println(lineArray.getClass)
//    lineArray.map(i => println(i.getClass))

    //    json2object.map(f => if (f.isInstanceOf[WiFiDataEntity]){
    //      val data = f.asInstanceOf[WiFiDataEntity]
    //      println(data.getWifidata.getApmac)
    //    })

  }
}
