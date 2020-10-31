# DataRobot Codegen Python Wrapper

Simple wrapper to leverage DataRobot Codegen Models in Python

# Requirements

* `make`
* `mvn`
* valid datarobot codegen jar

Run `make` in root of this directory and then `pip install -U ./codegen_wrapper/dist/datarobot_codegen*.whl` 

# Usage 

```
from datarobot.codegen_wrapper import CodegenPredictor
cp = CodegenPredictor(model_id, path_to_codegen_jar)
with open("../data/dataset.tsv", "r") as f:
  headers = f.readline().strip("\n").split("\t")
  values = f.readline().strip("\n").split("\t")
row = dict(list(zip(headers, values)))
cp.score(row)
```


