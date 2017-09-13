package com.github.tyang513.batch.demo

/**
  * Created by yangtao on 2017/9/13.
  */
object FlatMapDemo {

  def main(args: Array[String]): Unit = {
    val a1 = Array(1, 2, 3, 4)
    val a2 = Array(3, 4, 5, 6)
    val a = Array(a1, a2)
    a.foreach(println)
    a.flatMap(x => x).foreach(println)

  }

}
