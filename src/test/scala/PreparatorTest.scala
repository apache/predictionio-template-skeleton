package org.example.vanilla

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import org.apache.predictionio.data.storage.Event

class PreparatorTest
  extends FlatSpec with SharedSingletonContext with Matchers {

  val dataSource = Seq(
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"))

  "prepare" should "return the events" in {
    val dataSourceRDD = sparkContext.parallelize(dataSource)
    val preparedData = new PreparedData(events = dataSourceRDD)
    preparedData shouldBe a [PreparedData]
  }
}