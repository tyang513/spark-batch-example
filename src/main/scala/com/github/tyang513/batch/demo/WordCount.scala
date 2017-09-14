package com.github.tyang513.batch.demo

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by yangtao on 2017/9/8.
  */
object WordCount {


  def main(args: Array[String]): Unit = {

    val filePath = "/home/hadoop/tao.yang/sparkwordcount.txt"
    // spark://ip-172-31-24-33.cn-north-1.compute.internal:7077
    val conf = new SparkConf().setAppName("spark-word-count").setMaster("local[4]")
    val sparkContext = new SparkContext(conf)
    val textFile = sparkContext.textFile(filePath);

//    textFile.foreach(line => println(line))

    val wordCount = textFile.collect.map(line => println(line))
//    val counts = wordCount.map(word => (word, 1)).reduceByKey(_ + _)
//    counts.map(x => x._1 + " " + x._2).saveAsTextFile("/home/hadoop/tao.yang/sparkwordcount.result")
  }

}
