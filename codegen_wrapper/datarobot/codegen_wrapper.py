from py4j.java_gateway import GatewayParameters, CallbackServerParameters
from py4j.java_gateway import JavaGateway
import subprocess
import os
import glob
import logging
import time
import numpy as np
import pandas as pd
from io import StringIO
import tempfile
import pickle

import socket
from contextlib import closing

class CodegenPredictor(object):
    def __init__(self, model_id, path_to_codegen, logging_level = "INFO",connect_timeout = 600000, read_timeout = 600000, **kwargs):
        logging.basicConfig(
            format="{} - %(levelname)s - %(asctime)s - %(message)s".format(__name__)
        )
        self.logger = logging.getLogger(__name__)
        self.logging_level = logging_level
        self.logger.setLevel(self.logging_level)
        self.connect_timeout = connect_timeout
        self.read_timeout = read_timeout
        self.model_id = model_id
        self.path_to_codegen = os.path.realpath(path_to_codegen)
        if not os.path.exists(self.path_to_codegen):
            raise Exception("{} does not exist".format(self.path_to_codegen))
        self.path = os.path.dirname(__file__)
        self.gateway_open = False
        self.model_loaded = False
        self.jar_files = glob.glob(os.path.join(os.path.dirname(__file__), "*.jar"))
        # self.jar_files.append(self.path_to_codegen)
        with open(self.path_to_codegen, "rb") as f:
            self.jar_bytes = f.read()
        self.java_server = [m for m in self.jar_files if "py4j-scoring" in m].pop()
        self.logger.info("{}".format(self.jar_files))       
        self.model = None
        self.features = None

    def java_gateway_init(self, model_id, connect_timeout = 600000, read_timeout = 600000):
        self._java_port = str(find_free_port())
        self._python_port = str(find_free_port())

        cmd = ["java", 
               "-cp", ":".join(self.jar_files),
               "com.github.timsetsfire.gateway.CodegenGateway",
               self._java_port, self._python_port, str(connect_timeout), str(read_timeout), model_id]

        self.logger.info("java gateway - {}".format(" ".join(cmd)))

        self.jgateway = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        gateway_params = GatewayParameters(
                        port=int(self._java_port)
                    )
        
        self.gateway_params = GatewayParameters(port=self._java_port)
        self.logger.info("starting gateway localhost:{}".format(self._java_port))
        self.gateway = JavaGateway(python_proxy_port = self._python_port,gateway_parameters=gateway_params)
        time.sleep(1)
        self.logger.info("gateway started localhost:{}".format(self._java_port))
        self.gateway_open = True

    def load_model(self):
        tmp = tempfile.NamedTemporaryFile(suffix=".jar", mode="wb")
        tmp.write(self.jar_bytes)
        self.jar_files.append(tmp.name)
        if not self.gateway_open:
            self.java_gateway_init(self.model_id, self.connect_timeout, self.read_timeout)
            self.gateway_open = True
        self.entry_point = self.gateway.entry_point
        self.model = self.entry_point.model
        ## TODO get features
        # self.features = list(self.model.getFeatures().keySet())
        self.model_loaded = True
        tmp.close()

    def score(self, data):
        if type(data) is pd.DataFrame:
            preds = self.entry_point.score(data.to_csv(index=False))
        elif type(data) is list and type(data[0]) is dict:
            df = pd.DataFrame(data)
            preds = self.entry_point.score(df.to_csv(index=False))
        elif type(data) is str:
            preds = self.entry_point.score(data)
        return pd.read_csv(StringIO(preds))

    def terminate_gateway(self):
        try:
            self.gateway.shutdown()
        except Exception as e:
            print(e)
        logging.info("terminating gateway")
        self.jgateway.terminate()
        self.gateway_open = False
        self.logger.info(self.jgateway.stdout.readlines())
        self.gateway = None
        self.jgateway = None
        self.entry_point = None
        self.model = None

    def pickle(self, path):
        self.terminate_gateway()
        with open(path, "wb") as f:
            pickle.dump(self, f)

def find_free_port():
    with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
        s.bind(("", 0))
        return s.getsockname()[1]
def dict_to_hashmap(r, gateway):
    h = gateway.jvm.java.util.HashMap()
    [h.put(k,v) for (k,v) in r.items()]
    return h
