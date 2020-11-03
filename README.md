# DataRobot Codegen Python Wrapper

Simple wrapper to leverage DataRobot Codegen Models in Python

# Requirements

* `make`
* `sbt`
* valid datarobot codegen jar

Run `make` in root of this directory and then `pip install -U ./codegen_wrapper/dist/datarobot_codegen*.whl` 

# Usage 

```
from datarobot.codegen_wrapper import CodegenPredictor
cp = CodegenPredictor(model_id, path_to_codegen_jar)
cp.score(df) # df is some pandas dataframe
```


