import predictionio
engine_client = predictionio.EngineClient(url = "http://localhost:8000")
print engine_client.send_query({"features" :[-1, -2, -1, -3, 0, 0, -1, 0]})

