package org.example.vanilla

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DataSourceTest
  extends FlatSpec with SharedSingletonContext with Matchers {

  ignore should "return the data" in {
    val dataSource = new DataSource(
      new DataSourceParams(appName = "test"))
    val data = dataSource.readTraining(sc = sparkContext)
    data shouldBe a [TrainingData]
  }
}