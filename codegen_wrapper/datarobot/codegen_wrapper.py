from py4j.java_gateway import GatewayParameters, CallbackServerParameters
from py4j.java_gateway import JavaGateway
import subprocess
import os
import glob
import logging
import time

import socket
from contextlib import closing

class CodegenPredictor(object):
    def __init__(self, model_id, path_to_codegen_jar = None, logging_level = "INFO"):
        logging.basicConfig(
            format="{} - %(levelname)s - %(asctime)s - %(message)s".format(__name__)
        )
        self.logger = logging.getLogger(__name__)
        self.logging_level = logging_level
        self.model_id = model_id
        self.model_jar = os.path.realpath(path_to_codegen_jar)
        self.logger.setLevel(self.logging_level)
        self.logger.info("{} exists: {}".format(self.model_jar, os.path.exists(self.model_jar)))

        self.path = os.path.dirname(__file__)
        self.gateway_open = False
        self.predictor_loaded = False
        self.jar_files = glob.glob(os.path.join(os.path.dirname(__file__), "*.jar"))
        self.java_server = [m for m in self.jar_files if "py4j-scoring" in m].pop()
        
        self.predictor = None
        self.features = None

    def java_gateway_init(self, connect_timeout = 0, read_timeout = 0):
        self._java_port = str(find_free_port())
        self._python_port = str(find_free_port())

        cmd = ["java", 
               "-cp", ":".join([self.model_jar, self.java_server]),
               "com.github.timsetsfire.gateway.Main",
               self._java_port, self._python_port, str(connect_timeout), str(read_timeout)]

        self.logger.info("java gateway - {}".format(" ".join(cmd)))

        self.jgateway = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        gateway_params = GatewayParameters(
                        port=int(self._java_port)
                    )
        
        self.gateway_params = GatewayParameters(port=self._java_port)
        print("starting gateway")
        self.gateway = JavaGateway(python_proxy_port = self._python_port,gateway_parameters=gateway_params)
        time.sleep(1)
        print("gateway started")
        self.gateway_open = True

    def load_predictor(self):
        if not self.gateway_open:
            self.java_gateway_init()
            self.gateway_open = True
        self.predictor = self.gateway.jvm.com.datarobot.prediction.Predictors.getPredictor(self.model_id)
        self.predictor_loaded = True
        
    def score(self, row):
        if not self.gateway_open:
            self.java_gateway_init()
            self.gateway_open = True
        if not self.predictor_loaded:
            self.load_predictor()
        record = self.gateway.jvm.java.util.HashMap()
        for k,v in row.items():
            record.put(k,v)
        score = self.predictor.score(record)
        return score

    def terminate_gateway(self):
        try:
            self.gateway.shutdown()
        except Exception as e:
            print(e)
        print("terminating gateway")
        self.jgateway.terminate()
        self.gateway_open = False
        print(self.jgateway.stdout.readlines())

def find_free_port():
    with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
        s.bind(("", 0))
        return s.getsockname()[1]

# java -jar ./target/scala-2.12/py4j-scoring-assembly-0.1.0-SNAPSHOT.jar 1234 1235