package com.github.tyang513.batch.demo

import scala.collection.GenSeq

/**
 * Hello world!
 *
 */
object App {

  def main(args: Array[String]): Unit = {
    println("x")
    println(2.to(10))
    val m = Array(1, 2, 3, 4, 5, 6)
    val m1 = m.map(x => x + 1)
    m1.foreach(println)
    val l = List(4)
    println(l.head)
    println(l.tail)
    val l2 = 9 :: List(4, 2)
    l2.foreach(x => println(x))
    val double = (x : Int) => {x * 2}
    println(double(2))

    val sele = App.selector("s") _
    sele("ssss")

    println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
    val numbers = List(2, 4, 6, 8)
    val ma = numbers.fold(0)((m: Int, n: Int) => {println("m = " + m + " n = " + n + "    m + n = " + (m + n)); m + n})
    println(ma)

    val days = Array(1, 2, 3, 4, 5, 6)
    days.zipWithIndex.foreach{case(x, i) => println(x + " = " + i)}
    days.foreach{ x => println(x)}
    for ((x, i) <- days.zip(Stream from 1)){
      println(x + " " + i)
    }

    val x = List(15, 10, 5, 8, 20, 12)
    x.groupBy(_ > 10).foreach(println)
    println (x.partition(_ > 10))

    println("======================")
    numbers.foreach(print)
    val sa = GenSeq("a", "bb", "ccc", "dddd")
    val max  = (x : Int, y : String) => {println("max " + x + " " + y); 0+y.length}
    val sum = (x : Int, y : Int) => {println("sum " + x + " " + y); x + y}
    val b = sa.aggregate(0)(max(_, _), sum(_, _))
    val c = sa.foldLeft(0)(max(_, _))
    println(b)
    println(c)

    println("========================= aggregate example ")
    val aex = List(1,2,3,4,5,6)

    def myfunc(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
      iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
    }
    println("==========aex.par.aggregate(0)(math.max(_, _), _ + _)")
    println(aex.par.aggregate(0)(math.max(_, _), _ + _))
    (aex).iterator.map(println)

    println(aex.aggregate(0)(math.max(_,_), _ + _))
    println(aex.aggregate(10)(math.max(_,_), _ + _) )
    println(List('a', 'b', 'c').aggregate(0)({ (sum, ch) => println("" + sum + " = " + ch.toInt); sum + ch.toInt }, { (p1, p2) => p1 + p2 }))
    println(aex.foldLeft(0)((m, n) => m +n ))
  }

  def selector(s1 : String)(status : String)(`type` : Int) : String = {
    println("ssssssssssssssssssssssssssssssss")
    println(s1)
    println(status)
    println(`type`)
    "s"
  }

}
