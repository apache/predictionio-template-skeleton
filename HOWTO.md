#Using MLLib for Linear Regression

This HOWTO describes how the vanilla prediction-io template can be modified to turn it into a regression template with MLLib-Linear Regression integration.  You can easily add and use any other MLlib regression algorithms. The following will demonstrate how to add the MLlib Linear REgression algorithm into the engine.

## Updating Algorithm.scala

Since we have to include and use an algorithm from a library, this is possibly the most important step in the integration. In 'Algorithm.scala'  import the MLlib Linear Regression algorithm by adding the following lines:
  
```Scala
  import org.apache.spark.mllib.regression.LinearRegressionWithSGD
  import org.apache.spark.mllib.clustering.LinearRegressionModel
  import org.apache.spark.mllib.linalg.Vector
  import org.apache.spark.mllib.linalg.Vectors
```

These are the necessary classes in order to use the MLLib's Linear Regression algporithm.
Modify the AlgorithmParams class for the MLLib Linear Regression algorithm:

```Scala
  case class AlgorithmParams(
  val intercept : Double
  ) extends Params
```
The parameters of the Linear Regression algorithm were obtained by refering to the MLLib documentation for the Linear Regression Algorithm.
This class contains the inputs to the training algorithm, other than the training data. In the case of regression, it turns out to be whether the algorithm should train with an intercept or not

Since we have added some parameters that are specific to the algorithm, the *engine.json* file has to be changed suitably, to include the newly added parameters.


Original:
```Javascript
  "algorithms": [
    {
      "name": "algo",
      "params": {
        "mult" : 1
      }
    }
  ]
  ```
  Changed to:
```Javascript
 "algorithms": [
    {
      "name": "algo",
      "params": {
        "intercept" : 2,
			  
      }
    }
  ]
  ```
This *engine.json* file can be found in the main directory of the vanilla template.

After effecting the above changes, we need to change class *Algorithm* because the model in consideration is LinearRegressionModel. *Model* what is used has to be replaced by *LinearRegressionModel*

Original:
```Scala
class Algorithm(val ap: AlgorithmParams)
  // extends PAlgorithm if Model contains RDD[]
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {
```
Changed to:
```Scala
class Algorithm(val ap: AlgorithmParams)
  // extends PAlgorithm if Model contains RDD[]
  extends P2LAlgorithm[PreparedData, LinearRegressionModel, Query, PredictedResult] {
 ```

Next, we look at the 2 functions that are implemented in *Algorithm.scala*. These are the *train* and *predict* functions. We look at *train* first. *train*  is used to prepare the LinearRegressionModel.


The code which accomplishes this is:

Train:

```Scala
 def train(data: PreparedData): KMeansModel = {
    

      // Creates a new LinearRegression class which generates the LinearRegressionModel
    val lin = new LinearRegressionWithSGD()

      // Setting the parameters obtained
     lin.setIntercept(ap.Intercept.equals(1.0)) 
     //Training the model on the data obtained from the preparator class
     lin.run(data.points)
  }
  ```
Next we look at the *predict* function. Using the model that has been trained, this function returns the required prediction on a new data point, and send to the *serving* class


  Predict:
  ```Scala
  def predict(model: LinearRegressionModel, query: Query): PredictedResult = {
    // Use the KMeansModel to predict cluster for new dataPoint
    val result = model.predict(Vectors.dense(query.features))
    new PredictedResult(result)
  }
  ```
## Updating DataSource.scala

*DataSource.scala* has to be customised depending on both the input data format and the format required by the *Preparator* and *Algorithm* class. In the linear regression case, the inputs are all double, with a special atrribute that we want to predict. This can be easily obtained in the form of RDD[Vector], which also happens to be the class required by Linear Regression algorithm in MLLib, making the Preparator class simple. We first import the required libraries

   
```Scala
  import org.apache.spark.mllib.linalg.Vector
  import org.apache.spark.mllib.linalg.Vectors
```
The main function in the DataSource class is the *readTraining* function. It reads the data points from the prediction-io event server and adds it to the RDD of Vector which the Preparator class is expecting.
For the dataset that this template uses, there are 9 attributes and a value to be predicted. All these features are compulsory. This has to be reflected in *readTraining* function. Also, all these features have to be considered as *double*, because the features take double values

Original:
```Scala
 def readTraining(sc: SparkContext): TrainingData = {

    // read all events of EVENT involving ENTITY_TYPE and TARGET_ENTITY_TYPE
    val eventsRDD: RDD[Event] = PEventStore.find(
      appName = dsp.appName,
      entityType = Some("ENTITY_TYPE"),
      eventNames = Some(List("EVENT")),
      targetEntityType = Some(Some("TARGET_ENTITY_TYPE")))(sc)

    new TrainingData(eventsRDD)
  }
 ```
Changed to:
```Scala
 def readTraining(sc: SparkContext): TrainingData = {
    val pointsDb = Storage.getPEvents()
      // read all events involving "point" type (the only type in our case)
    println("Gathering data from event server.")
    val pointsRDD: RDD[Vector] = pointsDb.aggregateProperties(
      appId = dsp.appId,
      entityType = "point",
      required = Some(List("plan","attr0","attr1", "attr2", "attr3", "attr4", "attr5", "attr6", "attr7")))(sc)
        .map { case (entityId, properties) =>
        try {
          
        // Convert the data from an Array to a RDD[vector] which is what KMeans 
	   	       // expects as input  
		       	  	  Vectors.dense(Array(
              properties.get[Double]("attr0"),
              properties.get[Double]("attr1"),
              properties.get[Double]("attr2"),
              properties.get[Double]("attr3"),
              properties.get[Double]("attr4"),
              properties.get[Double]("attr5"),
              properties.get[Double]("attr6"),
              properties.get[Double]("attr7")
        ))
          
        } catch {
          case e: Exception => {
            logger.error(s"Failed to get properties ${properties} of" +
              s" ${entityId}. Exception: ${e}.")
            throw e
          }
        }
      }
	
    new TrainingData(training_points)
  }
  ```
Note :

* The class TrainingData has attribute training_points which is RDD[LabeledPoint]

* LabeledPoint has a double value : label, which will the target variable in the case of regression

* LabeledPoint has a Vector[Doubles] as the features, and this is obtained using Vectors.dense()

* In order to use another dataset, the number of 'required' attributes and expected attributes that are present in the dataset have to be updated accordingly in *DataSource.scala*


* A python wrapper code was written to parse the input and pass the values to *eventserver* in the format expected by *DataSource.scala*

* As mentioned earlier, *preparator.scala* doesn't have to do much in this case. We just have to ensure consistency in the datatypes 

* *Serving.scala* can be personalised further, depending on the requirements. The main step is to ensure consistency among all datatypes used

We have now succesfully integreated the Linear Regression Algorithm from MLLib. To use the template,

```Scala
pio build
pio train
pio deploy
```
For detailed instructions on how to run the template, once created, check the Quixk Start Guide for Vanilla Template
