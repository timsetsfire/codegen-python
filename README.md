# CHECK OUT [DATAROBOT-PREDICT](https://pypi.org/project/datarobot-predict/)

this rinky dink repo has inspired DataRobot to deliver DataRobot Prediction Library is a Python library for making predictions using various prediction methods supported by DataRobot. The intention is to provide a common interface for making predictions, making it easy to swap out the underlying implementation.

install is via `pip install datarobot-predict`.  Check it out on [pypi](https://pypi.org/project/datarobot-predict/)

# DataRobot Codegen Python Wrapper

__NOT OFFICIALLY SUPPORTED__

Simple wrapper to leverage DataRobot Codegen Models in Python.  This works for the following codegen exports
* Regression
* Classificaiton
* Time Series Regression Forecasting


# Requirements

* `jdk11`
* python >= 3.7
* valid datarobot codegen jar

To create the whl, run `setup.py`
```
cd codegen_wrapper
python3 ./setup.py bdist_wheel 
```

After you have the whl, install with pip:
```
pip3 install -U dist/datarobot_codegen_wrapper*.whl
```

If you would like to build locally, you will need

* `make`
* `sbt`

from root run 

```
make
pip3 install -U ./codegen_wrapper/dist/datarobot_codegen_wrapper*.whl
```

# Usage 

```
from datarobot.codegen_wrapper import CodegenPredictor
cp = CodegenPredictor(model_id, path_to_codegen_jar)
cp.load_model()
cp.score(df) # df is some pandas dataframe or list of dicts.  
cp.terminate_gateway()
```

If you get the following exception 
```
Py4JNetworkError: An error occurred while trying to connect to the Java server
```

Try again.  You may have requested a prediction before the gateway was ready.  


