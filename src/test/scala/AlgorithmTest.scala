package org.example.vanilla

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import org.apache.predictionio.data.storage.Event

class AlgorithmTest
  extends FlatSpec with SharedSingletonContext with Matchers {

  val params = AlgorithmParams(mult = 7)
  val algorithm = new Algorithm(params)
  val dataSource = Seq(
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"),
    Event(event = "test", entityType = "example", entityId = "1"))

  "train" should "return a model" in {
    val dataSourceRDD = sparkContext.parallelize(dataSource)
    val preparedData = new PreparedData(events = dataSourceRDD)
    val model = algorithm.train(sparkContext, preparedData)
    model shouldBe a [Model]
  }
}