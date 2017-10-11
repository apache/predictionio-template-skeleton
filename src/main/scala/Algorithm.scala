package org.example.vanilla

import org.apache.predictionio.controller.P2LAlgorithm
import org.apache.predictionio.controller.Params

import org.apache.spark.SparkContext

import grizzled.slf4j.Logger

case class AlgorithmParams(mult: Int) extends Params

class Algorithm(val ap: AlgorithmParams)
  // extends PAlgorithm if Model contains RDD[]
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): Model = {
    // Simply count number of events
    // and multiple it by the algorithm parameter
    // and store the number as model
    val count = data.events.count().toInt * ap.mult
    new Model(mc = count)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    // Prefix the query with the model data
    val result = s"${model.mc}-${query.q}"
    PredictedResult(p = result)
  }
}

class Model(val mc: Int) extends Serializable {
  override def toString = s"mc=${mc}"
}
