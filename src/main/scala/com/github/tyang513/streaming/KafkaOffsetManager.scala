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
      ZkUtils.updatePersistentPath(zkClient, offsetPath, offsetVal + data)

      //LOGGER.debug("persisting offset details - topic: {}, partition: {}, offset: {}, node path: {}", Seq[AnyRef](or.topic, or.partition.toString, offsetVal.toString, offsetPath): _*)
    })
  }

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("Kafka-Offset-Management-Blog")
      .setMaster("local[4]") //Uncomment this line to test while developing on a workstation
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(10))

    val kafkaParams = Map[String, String](
      "bootstrap.servers" -> "localhost:9092,anotherhost:9092",
      "key.deserializer" -> "",
      "value.deserializer" -> "",
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false"
    )
    val topics = Seq("topicA", "topicB")
    val zkUrl = args(0)
    val sessionTimeout = args(1).toInt
    val connectionTimeout = args(2).toInt

    val zkConnect = ""
    val zkSessionTimeoutMs = 6000
    val zkConnectionTimeoutMs = 30
    val zkClient = new ZkClient(zkConnect, zkSessionTimeoutMs, zkConnectionTimeoutMs, ZKStringSerializer)
    val offset = readOffsets(topics, kafkaParams.apply("group.id").toString, zkClient)
    val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key(), mmd.message())
    val inputDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](ssc, kafkaParams, offset, messageHandler)

    inputDStream.foreachRDD((rdd, batchTime) => {
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      offsetRanges.foreach(offset => println(offset.topic, offset.partition, offset.fromOffset, offset.untilOffset))
      persistOffsets(offsetRanges.toSeq, kafkaParams.apply("group.id").toString, true, zkClient)
    })
  }
}
