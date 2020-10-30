.PHONY: gateway

build: gateway
	mvn clean package
	cp ./target/py4j-scoring*with-dependencies.jar ../datarobot
	mvn clean

# clean:
# 	rm -rf *.jar