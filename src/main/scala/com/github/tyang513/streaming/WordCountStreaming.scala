package com.github.tyang513.streaming

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}

/**
  * Created by yangtao on 2017/9/12.
  */
object WordCountStreaming {

  def main(args: Array[String]): Unit = {

    val topic = "test".split(",").toSet
    val kafkaParam = Map[String, String](
      "metadata.broker.list" -> "172.23.7.125:9092"
    )

    val sparkConfig = new SparkConf().setAppName("word-count-streaming").setMaster("spark://172.23.7.126:7077")
    val streamingContext = new StreamingContext(sparkConfig, Seconds(2))

    val directKafkaStream  = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](streamingContext, kafkaParam, topic)

    var offsetRanges = Array.empty[OffsetRange]
    directKafkaStream.transform{ rdd =>
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }.foreachRDD(rdd =>
      for (o <- offsetRanges){
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    )

    println("===============================")
    directKafkaStream.foreachRDD(rdd => rdd.map(m => println("rdd ====" + m._1 + " = " + m._2)).collect())
    println("-------------------------------")

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}
