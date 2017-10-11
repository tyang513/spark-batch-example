package com.github.tyang513.streaming

import java.util.Properties

import com.github.tyang513.kafka.service.PipelineDefinitionService
import com.github.tyang513.kafka.util.ApplicaitonContextManager
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by yangtao on 2017/9/12.
  */
object WordCountStreaming {

  def main(args: Array[String]): Unit = {

    val streamingProperties : Properties  = ApplicaitonContextManager.getInstance().getBean("spark-batch-example-streaming", classOf[java.util.Properties])

    val topic = streamingProperties.getProperty("marketing.streaming.kafka.topic", "test").split(",").toSet

    val kafkaParam = Map[String, String](
      "metadata.broker.list" -> streamingProperties.getProperty("marketing.streaming.kafka.brokers"),
      "request.required.acks" -> streamingProperties.getProperty("request.required.acks", "1"),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false"
    )

    val sparkConfig = new SparkConf().setAppName("word-count-streaming").setMaster("spark://172.23.7.126:7077")
    val streamingContext = new StreamingContext(sparkConfig, Seconds(10))

    println(topic + "    "  + kafkaParam.toString())

    val directKafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](streamingContext, kafkaParam, topic)
    var offsetRanges = Array.empty[OffsetRange]
    directKafkaStream.transform { rdd =>
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }.foreachRDD(rdd =>
      for (o <- offsetRanges) {
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    )

    println("===============================")
    directKafkaStream.foreachRDD(rdd => rdd.map(m => {
      val service = new PipelineDefinitionService()
      val pipelineDefinition = service.findPipelineDefinition(18);
      println("rdd ====" + m._1 + " = " + m._2 + " " + pipelineDefinition.toString)
    }).collect())
    println("-------------------------------")

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}
