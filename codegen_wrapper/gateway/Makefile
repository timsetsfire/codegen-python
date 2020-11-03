.PHONY: gateway

build: gateway
	sbt clean assembly
	cp ./target/scala-2.12/py4j-scoring*.jar ../datarobot
	sbt clean