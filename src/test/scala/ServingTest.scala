package org.example.vanilla

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ServingTest
  extends FlatSpec with Matchers {

  val query = Query(q = "5")
  val predictedResults = Seq(
    PredictedResult(p = "25"),
    PredictedResult(p = "50"),
    PredictedResult(p = "75"))

  "serve" should "return the first prediction" in {
    val serving = new Serving()
    val prediction = serving.serve(
      query = query,
      predictedResults = predictedResults)
    prediction shouldBe a [PredictedResult]
    prediction.p shouldEqual "25"
  }
}