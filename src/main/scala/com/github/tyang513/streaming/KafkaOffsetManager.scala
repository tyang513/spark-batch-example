package com.github.tyang513.streaming


import _root_.kafka.utils.{ZKGroupTopicDirs, ZKStringSerializer, ZkUtils}
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.I0Itec.zkclient.ZkClient
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.data.ACL

import scala.collection.Seq
import scala.collection.mutable.ListBuffer


object KafkaOffsetManager {


  def readOffsets(topics: Seq[String], groupId: String, zkClient: ZkClient): Map[TopicAndPartition, Long]

  //val zkUtils: ZkUtils = ZkUtils.apply(zkServers, ZK_TIMEOUT_MSEC, ZK_TIMEOUT_MSEC, false)
  = {
    val topicPartOffsetMap = collection.mutable.HashMap.empty[TopicAndPartition, Long]
    val partitionMap = ZkUtils.getPartitionsForTopics(zkClient, topics)

    // /consumers/<groupId>/offsets/<topic>/
    partitionMap.foreach(topicPartitions => {
      val zkGroupTopicDirs = new ZKGroupTopicDirs(groupId, topicPartitions._1)
      topicPartitions._2.foreach(partition => {
        val offsetPath = zkGroupTopicDirs.consumerOffsetDir + "/" + partition
        try {
          val offsetStatTuple = ZkUtils.readData(zkClient, offsetPath)
          if (offsetStatTuple != null) {
            println("offset path : " + offsetPath + " partition = " + partition + " " + " offsetStatTuple = " + offsetStatTuple)
            //LOGGER.info("retrieving offset details - topic: {}, partition: {}, offset: {}, node path: {}", Seq[AnyRef](topicPartitions._1, partition.toString, offsetStatTuple._1, offsetPath): _*)
            topicPartOffsetMap.put(new TopicAndPartition(topicPartitions._1, Integer.valueOf(partition)), offsetStatTuple._1.toLong)
          }
        } catch {
          case e: Exception =>
            //LOGGER.warn("retrieving offset details - no previous node exists:" + " {}, topic: {}, partition: {}, node path: {}", Seq[AnyRef](e.getMessage, topicPartitions._1, partition.toString, offsetPath): _*)
            topicPartOffsetMap.put(new TopicAndPartition(topicPartitions._1, Integer.valueOf(partition)), 0L)
        }
      })
    })
    topicPartOffsetMap.toMap
  }


  def persistOffsets(offsets: Seq[OffsetRange], groupId: String, storeEndOffset: Boolean, zkClient: ZkClient): Unit = {
    offsets.foreach(or => {
      val zkGroupTopicDirs = new ZKGroupTopicDirs(groupId, or.topic);

      val acls = new ListBuffer[ACL]()
      val acl = new ACL
      acl.setId(ZooDefs.Ids.ANYONE_ID_UNSAFE)
      acl.setPerms(ZooDefs.Perms.ALL)
      acls += acl

      val offsetPath = zkGroupTopicDirs.consumerOffsetDir + "/" + or.partition;
      val offsetVal = if (storeEndOffset) or.untilOffset else or.fromOffset
      val data = offsetVal.toString
      println("offset path = " + offsetPath + " offset value = " + offsetVal + "  data = " + data)
      ZkUtils.updatePersistentPath(zkClient, offsetPath, offsetVal.toString)

      //LOGGER.debug("persisting offset details - topic: {}, partition: {}, offset: {}, node path: {}", Seq[AnyRef](or.topic, or.partition.toString, offsetVal.toString, offsetPath): _*)
    })
  }

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("KafkaOffsetManager").setMaster("spark://172.23.7.126:7077")
      .set("spark.streaming.backpressure.enabled", "true")
      .set("spark.streaming.kafka.maxRatePerPartition", "300")
    val sparkConfig = new SparkContext(sparkConf)
    val streamingContext = new StreamingContext(sparkConfig, Seconds(3))

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> "172.23.7.125:9092",
      "request.required.acks" -> "1",
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "enable.auto.commit" -> "false"
    )
    val topics = Seq("test")

    val zkConnect = "172.23.7.125:2181"
    val zkSessionTimeoutMs = 6000
    val zkConnectionTimeoutMs = 30
    val zkClient = new ZkClient(zkConnect, zkSessionTimeoutMs, zkConnectionTimeoutMs, ZKStringSerializer)
    val offset = readOffsets(topics, kafkaParams.apply("group.id").toString, zkClient)
    val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key(), mmd.message())
    val inputDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](streamingContext, kafkaParams, offset, messageHandler)

    inputDStream.foreachRDD((rdd, batchTime) => {
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      offsetRanges.foreach(offset => println("topic = " + offset.topic, " partition = " + offset.partition, " from offset = " + offset.fromOffset + "until offset = " + offset.untilOffset, " read length = " + (offset.untilOffset - offset.fromOffset)))
      persistOffsets(offsetRanges.toSeq, kafkaParams.apply("group.id").toString, true, zkClient)
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
