package com.sizmek.rtree2d.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import scala.collection.breakOut

case class PointOfInterest(x: Float, y: Float)

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = Array(
  "-server",
  "-Xms4g",
  "-Xmx4g",
  "-XX:NewSize=3g",
  "-XX:MaxNewSize=3g",
  "-XX:InitialCodeCacheSize=256m",
  "-XX:ReservedCodeCacheSize=256m",
  "-XX:+UseParallelGC",
  "-XX:-UseBiasedLocking",
  "-XX:+AlwaysPreTouch"
))
abstract class BenchmarkBase {
  @Param(Array("1", "10", "100", "1000", "10000", "100000", "1000000"))
  var size = 1000

  @Param(Array("false", "true"))
  var shuffle = true

  var nodeCapacity = 16 // can be a param on demand

  var partToAddOrRemove = (1 - 0.3) / 2 // 0.3 here is a part of ASAP bidding

  var overlap = 0.1f // number of overlaps in each of 4 directions (should be less than 0.5 for no overlap)

  def doShuffle[A](as: Array[A]): Unit = {
    val rnd = new util.Random(0)
    var i = as.length - 1
    while (i > 0) {
      val i1 = rnd.nextInt(i)
      val a = as(i1)
      as(i1) = as(i)
      as(i) = a
      i -= 1
    }
  }

  def genPoints: Array[PointOfInterest] = {
    val l = Math.sqrt(size).toFloat
    val li = l.toInt
    (0 to size).map { i =>
      val x = (i / li) / l
      val y = (i % li) / l
      PointOfInterest(x, y)
    }(breakOut)
  }

  def genRequests(points: Array[PointOfInterest]): Array[Float] = {
    val xys = new Array[Float](2 * size)
    var i = 0
    while (i < xys.length) {
      val p = points(i >> 1)
      xys(i) = p.x
      xys(i + 1) = p.y
      i += 2
    }
    xys
  }
}
